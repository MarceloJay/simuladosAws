#!/usr/bin/env python3
import os, json, re
from PyPDF2 import PdfReader

BASE = os.path.dirname(__file__)
PDF_PATH = os.path.abspath(os.path.join(BASE, '..', 'app', 'src', 'main', 'assets', 'simulado.pdf'))
Q_PATH = os.path.abspath(os.path.join(BASE, '..', 'app', 'src', 'main', 'assets', 'questions.json'))
BACKUP = Q_PATH + '.refinepages.bak'

reader = PdfReader(PDF_PATH)
pages = [(i, (p.extract_text() or '').replace('\r\n','\n')) for i,p in enumerate(reader.pages)]

with open(Q_PATH, 'r', encoding='utf-8') as f:
    data = json.load(f)

with open(BACKUP, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

changed = 0

# helper to extract options from a block of text
opt_re = re.compile(r'^[A-Ea-e]\s*[\)\.-]|^\d+\s*[\)\.-]|^[-•]\s*')

for idx,item in enumerate(data):
    opts = item.get('options', [])
    q = item.get('question','')
    raw_q = re.sub(r"^\s*\d+/\d+\s*-\s*", "", q).strip()
    # consider bad if only one option or first option repeats question
    if len(opts) > 1 and not (raw_q.lower() in (opts[0] or '').lower()):
        continue

    # search each page for an occurrence of the raw_q (or part of it)
    found = False
    for pnum, text in pages:
        if not text:
            continue
        # try to match a reasonably unique short snippet
        snippet = raw_q[:80]
        if not snippet:
            continue
        pos = text.find(snippet)
        if pos == -1:
            # try shorter snippet
            pos = text.find(snippet[:40]) if len(snippet) > 40 else -1
        if pos == -1:
            continue
        # found on this page; extract following lines
        window = text[pos:pos+1500]
        lines = [l.strip() for l in window.splitlines() if l.strip()]
        # find the line index of snippet
        idx_line = 0
        for i,l in enumerate(lines):
            if snippet.strip()[:30].lower() in l.lower():
                idx_line = i
                break
        # collect candidate option lines after idx_line
        candidates = []
        for l in lines[idx_line+1:]:
            # stop if next question or 'Resposta'
            if re.match(r'^(Quest[oã]o\s+\d+|\d+\s*[\)\.]\s*)', l, re.IGNORECASE):
                break
            if re.match(r'^(Resposta:|Resposta\s*:)', l, re.IGNORECASE):
                break
            # if line looks like an option marker, take it
            if opt_re.match(l) or (len(l) < 140 and '?' not in l and len(l.split())<12):
                clean = re.sub(r'^[A-Za-z0-9]+[\)\.-:\s]+', '', l).replace('✅','').strip()
                if clean and clean not in candidates:
                    candidates.append(clean)
            # stop after collecting 5
            if len(candidates) >= 5:
                break
        # if we didn't find options, also try previous lines (options might be above question)
        if len(candidates) < 2:
            prev_candidates = []
            for l in reversed(lines[:idx_line]):
                if opt_re.match(l) or (len(l) < 140 and '?' not in l and len(l.split())<12):
                    clean = re.sub(r'^[A-Za-z0-9]+[\)\.-:\s]+', '', l).replace('✅','').strip()
                    if clean and clean not in prev_candidates:
                        prev_candidates.insert(0, clean)
                if len(prev_candidates) >= 5:
                    break
            if prev_candidates and len(prev_candidates) > len(candidates):
                candidates = prev_candidates

        # if still short, try next page
        if len(candidates) < 2:
            # try next page text
            if pnum+1 < len(pages):
                next_text = pages[pnum+1][1]
                next_lines = [l.strip() for l in next_text.splitlines() if l.strip()]
                for l in next_lines:
                    if re.match(r'^(Resposta:|Quest[oã]o\s+\d+)', l, re.IGNORECASE):
                        break
                    if opt_re.match(l) or (len(l) < 140 and '?' not in l and len(l.split())<12):
                        clean = re.sub(r'^[A-Za-z0-9]+[\)\.-:\s]+', '', l).replace('✅','').strip()
                        if clean and clean not in candidates:
                            candidates.append(clean)
                    if len(candidates) >= 5:
                        break

        # finalize
        if len(candidates) >= 2:
            item['options'] = candidates[:5]
            # detect check marks in the local window across pages
            correct = []
            combined = window
            if pnum+1 < len(pages):
                combined += '\n' + pages[pnum+1][1][:800]
            for i,opt in enumerate(item['options']):
                if ('✅' in opt) or (opt and (opt + ' ✅') in combined or (opt + '✅') in combined):
                    correct.append(i)
            item['correctAnswers'] = correct
            changed += 1
            found = True
            break
    if not found:
        # leave as-is
        pass

# save
if changed>0:
    with open(Q_PATH, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

print(f'Page-refine: updated {changed} entries. Backup at {BACKUP}')
