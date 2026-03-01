#!/usr/bin/env python3
import json
import re
import os

IN_PATH = os.path.join(os.path.dirname(__file__), '..', 'app', 'src', 'main', 'assets', 'questions.json')
OUT_PATH = IN_PATH

with open(IN_PATH, 'r', encoding='utf-8') as f:
    data = json.load(f)

n = len(data)
cleaned = []
for idx, item in enumerate(data):
    opts = item.get('options', [])
    # find question end index: first option that contains '?'
    q_end = None
    for i, line in enumerate(opts):
        if '?' in line:
            q_end = i
            break
    if q_end is None:
        # fallback: first line is question
        q_end = 0
    question_text = ' '.join([opts[i].strip() for i in range(0, q_end+1)])
    # find start of choices
    choices_start = q_end + 1
    # find index of 'Resposta:' if present
    resp_idx = None
    for i in range(choices_start, len(opts)):
        if re.match(r'^(Resposta:|Resposta\s*[:])', opts[i], re.IGNORECASE):
            resp_idx = i
            break
    choices_end = resp_idx if resp_idx is not None else len(opts)
    choices = [re.sub(r'\s*✅\s*$', '', o).strip() for o in opts[choices_start:choices_end]]
    # detect correct answers by ✅ markers in original options slice
    correct = []
    for i, o in enumerate(opts[choices_start:choices_end]):
        if '✅' in o:
            correct.append(i)
    # if no choices found, try to keep remaining lines as choices except if they look like explanations
    if not choices:
        # heuristics: take lines that are short and not start with 'Resposta' or look like description
        candidates = []
        for o in opts:
            if len(o) < 120 and not re.match(r'^(Resposta:|O |A |Permissões|Resposta|Com a AWS)', o):
                candidates.append(o.strip())
        choices = candidates
    # set question field as '1/65 - question'
    question_number = f"{idx+1}/{n}"
    full_question = question_number + " - " + question_text
    cleaned.append({
        'question': full_question,
        'options': choices,
        'correctAnswers': correct
    })

with open(OUT_PATH, 'w', encoding='utf-8') as f:
    json.dump(cleaned, f, ensure_ascii=False, indent=2)

print(f'Cleaned {len(cleaned)} questions to {OUT_PATH}')
