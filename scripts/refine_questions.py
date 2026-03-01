#!/usr/bin/env python3
import os
import json
import re
from PyPDF2 import PdfReader

BASE = os.path.dirname(__file__)
PDF_PATH = os.path.abspath(os.path.join(BASE, '..', 'app', 'src', 'main', 'assets', 'simulado.pdf'))
Q_PATH = os.path.abspath(os.path.join(BASE, '..', 'app', 'src', 'main', 'assets', 'questions.json'))
BACKUP_PATH = Q_PATH + '.bak'

# load PDF full text
reader = PdfReader(PDF_PATH)
pages = []
for p in reader.pages:
    try:
        pages.append(p.extract_text() or "")
    except Exception:
        pages.append("")
full_text = "\n".join(pages)
# normalize
full_text = re.sub(r"\r\n", "\n", full_text)

# load current questions
with open(Q_PATH, 'r', encoding='utf-8') as f:
    data = json.load(f)

# backup
with open(BACKUP_PATH, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

changed = 0
n = len(data)
for idx, item in enumerate(data):
    opts = item.get('options', [])
    qtext = item.get('question', '')
    # remove numbering from qtext for search
    raw_q = re.sub(r"^\s*\d+/\d+\s*-\s*", "", qtext)
    # if options are single and look like the question itself, try to find options in PDF
    if len(opts) <= 1:
        # try to locate the question in full_text
        # find a unique substring (take first 40 chars of raw_q)
        snippet = raw_q.strip()
        if len(snippet) > 120:
            snippet = snippet[:120]
        # try to find snippet without numbering
        pos = full_text.find(snippet)
        if pos == -1:
            # try shorter snippet
            short = ' '.join(snippet.split()[:8])
            pos = full_text.find(short) if short else -1
        if pos != -1:
            # get following text after position
            tail = full_text[pos+len(snippet):pos+len(snippet)+1200]
            # split lines
            lines = [l.strip() for l in tail.splitlines() if l.strip()]
            candidates = []
            for l in lines:
                # stop if we reach 'Resposta' or next question number pattern
                if re.match(r'^(Resposta:|Resposta\s*:)', l, re.IGNORECASE):
                    break
                if re.match(r'^\d+\s*[\.)]\s*', l):
                    break
                # heuristics: option-like lines are short and not end with '?'
                if len(l) < 100 and '?' not in l:
                    # skip lines that repeat the question
                    if snippet.lower() in l.lower():
                        continue
                    # drop lines that are like 'A.' or '1.' alone
                    if re.match(r'^[A-Za-z0-9]\s*[:\)\.-]?$|^-$', l):
                        continue
                    candidates.append(re.sub(r'^[A-Za-z0-9]+[\)\.-:\s]+', '', l).strip())
                # stop if we collected enough
                if len(candidates) >= 5:
                    break
            # deduplicate while preserving order
            seen = set()
            clean_candidates = []
            for c in candidates:
                cc = c.replace('✅','').strip()
                if cc and cc not in seen:
                    seen.add(cc)
                    clean_candidates.append(cc)
            if len(clean_candidates) >= 2:
                item['options'] = clean_candidates[:5]
                # detect marks
                correct = []
                for i,c in enumerate(candidates[:5]):
                    if '✅' in c:
                        correct.append(i)
                item['correctAnswers'] = correct
                changed += 1
        else:
            # as fallback, try to split the single option by sentence endings to produce choices
            if len(opts)==1 and len(opts[0].split('?'))>1:
                parts = [p.strip() for p in re.split(r"\n+", opts[0]) if p.strip()]
                if len(parts)>1:
                    item['options'] = parts[1:6]
                    item['correctAnswers'] = []
                    changed += 1

# save back if changed
if changed>0:
    with open(Q_PATH, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

print(f'Refined {changed} questions (out of {n}). Backup at {BACKUP_PATH}')
