#!/usr/bin/env python3
import os, json, re
from PyPDF2 import PdfReader

BASE = os.path.dirname(__file__)
PDF_PATH = os.path.abspath(os.path.join(BASE, '..', 'app', 'src', 'main', 'assets', 'simulado.pdf'))
Q_PATH = os.path.abspath(os.path.join(BASE, '..', 'app', 'src', 'main', 'assets', 'questions.json'))
BACKUP = Q_PATH + '.refinemore.bak'

reader = PdfReader(PDF_PATH)
pages_text = [ (i, (p.extract_text() or '').replace('\r\n','\n')) for i,p in enumerate(reader.pages) ]
full_text = '\n'.join(t for _,t in pages_text)

with open(Q_PATH, 'r', encoding='utf-8') as f:
    data = json.load(f)

with open(BACKUP, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

changed = 0
n = len(data)

# helper: find best page and offset for snippet
for idx,item in enumerate(data):
    opts = item.get('options', [])
    q = item.get('question','')
    raw_q = re.sub(r"^\s*\d+/\d+\s*-\s*", "", q)
    # detect problematic entries
    bad = False
    if len(opts) <= 1:
        bad = True
    else:
        # also bad if first option equals question or contains question words
        if any(raw_q.strip().lower()[:40] in o.lower() for o in opts[:1]):
            bad = True
    if not bad:
        continue

    # search in full_text for snippet
    snippet = ' '.join(raw_q.split())
    snippet_short = snippet[:100]
    pos = full_text.find(snippet_short)
    found_opts = []
    if pos != -1:
        # take window around pos
        window = full_text[max(0,pos-200):pos+1200]
        lines = [l.strip() for l in window.splitlines() if l.strip()]
        # look for option patterns: 'A)', 'A.', '1)', '- Option'
        for i,l in enumerate(lines):
            if re.match(r'^[A-Ea-e]\s*[\)\.-]', l) or re.match(r'^\d+\s*[\)\.-]', l):
                # collect following option-like lines
                j = i
                while j < len(lines) and len(found_opts) < 6:
                    line = lines[j]
                    m = re.sub(r'^[A-Za-z0-9]+[\)\.-:\s]+', '', line).strip()
                    # stop on 'Resposta' or next question
                    if re.match(r'^(Resposta:|Quest[oã]o|\d+\s*[\)\.]\s*)', line, re.IGNORECASE):
                        break
                    if len(m) > 0 and len(m) < 140 and '?' not in m:
                        found_opts.append(m.replace('✅','').strip())
                    j += 1
                if found_opts:
                    break
        # If none via markers, try lines following snippet that are short
        if not found_opts:
            # find lines after pos in full_text
            tail = full_text[pos+len(snippet_short):pos+len(snippet_short)+800]
            lines2 = [l.strip() for l in tail.splitlines() if l.strip()]
            for l in lines2:
                if re.match(r'^(Resposta:|Quest[oã]o|\d+\s*[\)\.]\s*)', l, re.IGNORECASE):
                    break
                if len(l) < 140 and '?' not in l and not l.lower().startswith('resposta'):
                    # skip if repeats question
                    if snippet_short.lower() in l.lower():
                        continue
                    clean = re.sub(r'^[A-Za-z0-9]+[\)\.-:\s]+', '', l).strip()
                    if clean and clean not in found_opts:
                        found_opts.append(clean)
                if len(found_opts) >= 5:
                    break
    # if found reasonable options, replace
    if len(found_opts) >= 2:
        item['options'] = found_opts[:5]
        # detect correct marks in the window (if any) - find index of lines with '✅'
        correct = []
        for i,opt in enumerate(found_opts[:5]):
            # search exact opt in window and check for ✅ nearby
            if full_text.find(opt + ' ✅') != -1 or full_text.find(opt + '✅') != -1:
                correct.append(i)
        item['correctAnswers'] = correct
        changed += 1

# save if changed
if changed > 0:
    with open(Q_PATH, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

print(f'Refined-more: {changed} entries updated. Backup at {BACKUP}')
