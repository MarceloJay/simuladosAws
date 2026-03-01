#!/usr/bin/env python3
"""
Script para converter arquivos simulado1-7.json para o formato correto
usado em simulado_1, simulado_2, simulado_3
"""

import json
import os
import re

def extract_correct_answer_index(answer_str, num_options):
    """Converte string de resposta para array de índices (0-based)"""
    if not answer_str:
        return [0]
    
    # Remove espaços e divide por vírgula
    answers = [a.strip() for a in answer_str.split(',')]
    indices = []
    
    for ans in answers:
        try:
            # Converte para int e subtrai 1 (para 0-based)
            idx = int(ans) - 1
            if 0 <= idx < num_options:
                indices.append(idx)
        except ValueError:
            continue
    
    return indices if indices else [0]

def convert_old_format_to_new(old_data):
    """Converte do formato antigo para o novo formato"""
    new_questions = []
    
    # O formato antigo tem uma chave com o título do simulado
    for key, questions_list in old_data.items():
        for q_obj in questions_list:
            # Encontrar a pergunta (chave que termina com /65 ou similar)
            question_key = None
            for k in q_obj.keys():
                if '/' in k and k.split('/')[0].isdigit():
                    question_key = k
                    break
            
            if not question_key:
                continue
            
            question_text = q_obj[question_key]
            
            # Coletar opções (chaves numéricas)
            options = []
            option_keys = sorted([k for k in q_obj.keys() if k.isdigit()], key=int)
            
            for opt_key in option_keys:
                options.append(q_obj[opt_key])
            
            # Obter resposta correta
            answer_str = q_obj.get('Resposta', '1')
            correct_answers = extract_correct_answer_index(answer_str, len(options))
            
            # Criar objeto no novo formato
            new_question = {
                "question": question_text,
                "options": options,
                "correctAnswers": correct_answers
            }
            
            new_questions.append(new_question)
    
    return new_questions

def main():
    # Diretório dos assets
    assets_dir = 'app/src/main/assets'
    
    # Arquivos a converter
    files_to_convert = [
        'simulado1.json',
        'simulado2.json',
        'simulado3.json',
        'simulado4.json',
        'simulado5.json',
        'simulado6.json',
        'simulado7.json'
    ]
    
    for filename in files_to_convert:
        filepath = os.path.join(assets_dir, filename)
        
        if not os.path.exists(filepath):
            print(f"⚠️  Arquivo não encontrado: {filepath}")
            continue
        
        print(f"Convertendo {filename}...")
        
        try:
            # Ler arquivo antigo
            with open(filepath, 'r', encoding='utf-8') as f:
                old_data = json.load(f)
            
            # Converter para novo formato
            new_data = convert_old_format_to_new(old_data)
            
            # Salvar no mesmo arquivo
            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(new_data, f, ensure_ascii=False, indent=2)
            
            print(f"✅ {filename} convertido com sucesso! ({len(new_data)} questões)")
            
        except Exception as e:
            print(f"❌ Erro ao converter {filename}: {e}")
    
    print("\n✨ Conversão concluída!")

if __name__ == '__main__':
    main()
