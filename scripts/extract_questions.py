#!/usr/bin/env python3
import sys
import os
import json
import re

try:
    from PyPDF2 import PdfReader
except Exception as e:
    print("PyPDF2 not installed. Install it with: pip install PyPDF2", file=sys.stderr)
    raise

PDF_PATH = sys.argv[1] if len(sys.argv) > 1 else os.path.join(os.path.dirname(__file__), '..', 'app', 'src', 'main', 'assets', 'simulado.pdf')
OUT_PATH = sys.argv[2] if len(sys.argv) > 2 else os.path.join(os.path.dirname(__file__), '..', 'app', 'src', 'main', 'assets', 'questions.json')

PDF_PATH = os.path.abspath(PDF_PATH)
OUT_PATH = os.path.abspath(OUT_PATH)

print(f"Reading PDF: {PDF_PATH}")
print(f"Writing JSON: {OUT_PATH}")

reader = PdfReader(PDF_PATH)
text_parts = []
for page in reader.pages:
    try:
        text_parts.append(page.extract_text() or "")
    except Exception:
        text_parts.append("")

full_text = "\n".join(text_parts)
# Normalize whitespace
full_text = re.sub(r"\r\n", "\n", full_text)
full_text = re.sub(r"\n{2,}", "\n\n", full_text)

# Heuristic splitting: try to split by question numbers like '1.', '1)', 'Questão 1', or lines that end with '?'
candidates = []

# First try find blocks that start with a number and a separator
blocks = re.split(r"\n(?=\s*(?:Quest[oã]o\s+\d+|\d+\s*[\.)]))", full_text, flags=re.IGNORECASE)
if len(blocks) <= 1:
    # fallback: split by double newline
    blocks = full_text.split('\n\n')

for b in blocks:
    b = b.strip()
    if not b:
        continue
    # Consider a block a question if it contains a question mark or several option-like lines
    if '?' in b or re.search(r"\n\s*[A-Za-z]\s*[\).-]", b) or re.search(r"\n\s*\d+\s*[\).-]", b):
        candidates.append(b)

questions = []
for c in candidates:
    # Try to separate question text from options
    lines = [l.strip() for l in c.split('\n') if l.strip()]
    if not lines:
        continue
    # Find the line index where options start (lines that begin with A) B) 1) - or letters)
    opt_idx = None
    for i, line in enumerate(lines[1:], start=1):
        if re.match(r"^[A-Za-z]\s*[\).-]", line) or re.match(r"^\d+\s*[\).-]", line) or re.match(r"^[A-E]\s*[:\)\-]", line, flags=re.IGNORECASE):
            opt_idx = i
            break
    if opt_idx is None:
        # try to find lines that look like options anywhere
        opts = [l for l in lines[1:] if len(l) < 120 and re.match(r"^[A-Za-z0-9].*", l)]
        question_text = lines[0]
        options = opts if opts else []
    else:
        question_text = ' '.join(lines[:opt_idx])
        options = []
        for l in lines[opt_idx:]:
            m = re.sub(r"^[A-Za-z0-9]+[\)\.-:]\s*", "", l)
            options.append(m)

    # final cleanup
    question_text = re.sub(r"^\d+\s*[\.)]?\s*", "", question_text)
    question_text = re.sub(r"^Quest[oã]o\s+\d+\s*[:\.-]*\s*", "", question_text, flags=re.IGNORECASE)

    if not options:
        # try to split by common separators like ' A. ' inside the block
        split_opts = re.split(r"\s+[A-E]\s*[\).:-]\s+", c)
        if len(split_opts) > 1:
            # first part is question
            question_text = split_opts[0].strip()
            options = [s.strip() for s in split_opts[1:] if s.strip()]

    if question_text:
        questions.append({
            "question": question_text,
            "options": options,
            "correctAnswers": []
        })

# Write JSON
os.makedirs(os.path.dirname(OUT_PATH), exist_ok=True)
with open(OUT_PATH, 'w', encoding='utf-8') as f:
    json.dump(questions, f, ensure_ascii=False, indent=2)

print(f"Wrote {len(questions)} question(s) to {OUT_PATH}")
