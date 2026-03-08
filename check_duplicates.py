#!/usr/bin/env python3
import json
import os
from collections import defaultdict

def normalize_text(text):
    """Normaliza texto para comparação (remove espaços extras, lowercase)"""
    return ' '.join(text.lower().strip().split())

def load_simulado(filepath):
    """Carrega um arquivo de simulado JSON"""
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def main():
    # Diretório dos simulados
    assets_dir = 'app/src/main/assets'
    
    # Carregar todos os simulados
    simulados = {}
    for i in range(1, 11):
        filename = f'simulado_{i}.json'
        filepath = os.path.join(assets_dir, filename)
        if os.path.exists(filepath):
            simulados[filename] = load_simulado(filepath)
            print(f"✓ Carregado {filename}: {len(simulados[filename])} questões")
    
    print(f"\nTotal de simulados: {len(simulados)}")
    print(f"Total de questões: {sum(len(q) for q in simulados.values())}")
    print("\n" + "="*80)
    
    # Criar índice de questões por texto normalizado
    question_index = defaultdict(list)
    
    for sim_name, questions in simulados.items():
        for idx, q in enumerate(questions):
            question_text = q.get('question', '')
            normalized = normalize_text(question_text)
            question_index[normalized].append({
                'simulado': sim_name,
                'index': idx,
                'original': question_text[:100] + '...' if len(question_text) > 100 else question_text
            })
    
    # Encontrar duplicatas
    duplicates = {k: v for k, v in question_index.items() if len(v) > 1}
    
    if duplicates:
        print(f"\n🔴 ENCONTRADAS {len(duplicates)} QUESTÕES DUPLICADAS:\n")
        
        for dup_idx, (normalized_text, occurrences) in enumerate(duplicates.items(), 1):
            print(f"\n--- Duplicata #{dup_idx} ---")
            print(f"Texto: {occurrences[0]['original']}")
            print(f"Aparece em {len(occurrences)} simulados:")
            for occ in occurrences:
                print(f"  • {occ['simulado']} (questão #{occ['index'] + 1})")
        
        print(f"\n" + "="*80)
        print(f"RESUMO: {len(duplicates)} questões aparecem em múltiplos simulados")
        print(f"Total de ocorrências duplicadas: {sum(len(v) - 1 for v in duplicates.values())}")
    else:
        print("\n✅ NENHUMA DUPLICATA ENCONTRADA!")
        print("Todos os simulados têm questões únicas.")
    
    # Estatísticas por simulado
    print(f"\n" + "="*80)
    print("ESTATÍSTICAS POR SIMULADO:")
    for sim_name, questions in sorted(simulados.items()):
        print(f"  {sim_name}: {len(questions)} questões")

if __name__ == '__main__':
    main()
