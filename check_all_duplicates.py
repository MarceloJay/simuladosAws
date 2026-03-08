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
        
        # Agrupar por pares de simulados
        pairs = defaultdict(list)
        for normalized_text, occurrences in duplicates.items():
            if len(occurrences) == 2:
                sim1, sim2 = sorted([occ['simulado'] for occ in occurrences])
                pairs[(sim1, sim2)].append({
                    'text': occurrences[0]['original'],
                    'q1': occurrences[0]['index'] + 1,
                    'q2': occurrences[1]['index'] + 1
                })
        
        print("DUPLICATAS POR PAR DE SIMULADOS:")
        print("="*80)
        for (sim1, sim2), dups in sorted(pairs.items()):
            print(f"\n{sim1} ↔ {sim2}: {len(dups)} questões duplicadas")
            for i, dup in enumerate(dups[:5], 1):  # Mostrar apenas as primeiras 5
                print(f"  {i}. Q#{dup['q1']} ↔ Q#{dup['q2']}: {dup['text']}")
            if len(dups) > 5:
                print(f"  ... e mais {len(dups) - 5} questões duplicadas")
        
        print(f"\n" + "="*80)
        print(f"RESUMO GERAL:")
        print(f"  • Total de questões duplicadas: {len(duplicates)}")
        print(f"  • Total de pares de simulados com duplicatas: {len(pairs)}")
        
        # Verificar se há questões que aparecem em mais de 2 simulados
        multi_dups = {k: v for k, v in duplicates.items() if len(v) > 2}
        if multi_dups:
            print(f"  • ⚠️  Questões que aparecem em 3+ simulados: {len(multi_dups)}")
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
