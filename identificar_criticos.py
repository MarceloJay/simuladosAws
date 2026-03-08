#!/usr/bin/env python3
import json
import os
import re
from difflib import SequenceMatcher

def normalize_text(text):
    """Normaliza texto para comparação"""
    text = re.sub(r'^\d+/\d+\s*-\s*', '', text)
    return ' '.join(text.lower().strip().split())

def text_similarity(text1, text2):
    """Calcula similaridade textual direta (0-100%)"""
    return SequenceMatcher(None, text1, text2).ratio() * 100

def load_simulado(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def main():
    assets_dir = 'app/src/main/assets'
    
    print("="*80)
    print("IDENTIFICANDO QUESTÕES CRÍTICAS (>85% SIMILARES)")
    print("="*80)
    
    # Carregar todos os simulados
    simulados = {}
    for i in range(1, 11):
        filename = f'simulado_{i}.json'
        filepath = os.path.join(assets_dir, filename)
        if os.path.exists(filepath):
            simulados[filename] = load_simulado(filepath)
    
    # Criar índice
    all_questions = []
    for sim_name, questions in simulados.items():
        for idx, q in enumerate(questions):
            all_questions.append({
                'simulado': sim_name,
                'index': idx + 1,
                'question': q['question'],
                'normalized': normalize_text(q['question'])
            })
    
    print(f"\nAnalisando {len(all_questions)} questões...\n")
    
    # Encontrar questões muito similares (>85%)
    critical_pairs = []
    
    for i, q1 in enumerate(all_questions):
        if (i + 1) % 100 == 0:
            print(f"Progresso: {i+1}/{len(all_questions)}...")
        
        for j, q2 in enumerate(all_questions[i+1:], i+1):
            if q1['simulado'] == q2['simulado']:
                continue
            
            similarity = text_similarity(q1['normalized'], q2['normalized'])
            
            if similarity >= 85:
                critical_pairs.append({
                    'q1': q1,
                    'q2': q2,
                    'similarity': similarity
                })
    
    critical_pairs.sort(key=lambda x: x['similarity'], reverse=True)
    
    # Agrupar por simulado para facilitar correção
    by_simulado = {}
    for pair in critical_pairs:
        sim1 = pair['q1']['simulado']
        sim2 = pair['q2']['simulado']
        key = tuple(sorted([sim1, sim2]))
        
        if key not in by_simulado:
            by_simulado[key] = []
        by_simulado[key].append(pair)
    
    # Salvar relatório
    with open('QUESTOES_CRITICAS.md', 'w', encoding='utf-8') as f:
        f.write("# QUESTÕES CRÍTICAS (>85% SIMILARES)\n\n")
        f.write(f"**Total**: {len(critical_pairs)} pares\n\n")
        f.write("="*80 + "\n\n")
        
        if critical_pairs:
            f.write("## AGRUPADO POR SIMULADOS\n\n")
            
            for (sim1, sim2), pairs in sorted(by_simulado.items()):
                f.write(f"### {sim1} ↔ {sim2}\n\n")
                f.write(f"**{len(pairs)} questões similares**\n\n")
                
                for idx, pair in enumerate(pairs[:5], 1):  # Mostrar apenas 5 por grupo
                    f.write(f"#### {idx}. Similaridade: {pair['similarity']:.1f}%\n\n")
                    f.write(f"- **{pair['q1']['simulado']}** Q#{pair['q1']['index']}: {pair['q1']['question'][:80]}...\n")
                    f.write(f"- **{pair['q2']['simulado']}** Q#{pair['q2']['index']}: {pair['q2']['question'][:80]}...\n\n")
                
                if len(pairs) > 5:
                    f.write(f"... e mais {len(pairs) - 5} questões similares\n\n")
                
                f.write("---\n\n")
            
            # Lista de questões para substituir
            f.write("## LISTA DE QUESTÕES PARA SUBSTITUIR\n\n")
            f.write("Recomenda-se substituir as seguintes questões:\n\n")
            
            to_replace = set()
            for pair in critical_pairs:
                # Escolher sempre a questão do simulado com número maior
                sim1_num = int(pair['q1']['simulado'].split('_')[1].split('.')[0])
                sim2_num = int(pair['q2']['simulado'].split('_')[1].split('.')[0])
                
                if sim1_num > sim2_num:
                    to_replace.add((pair['q1']['simulado'], pair['q1']['index']))
                else:
                    to_replace.add((pair['q2']['simulado'], pair['q2']['index']))
            
            for sim, idx in sorted(to_replace):
                f.write(f"- {sim} - Questão #{idx}\n")
            
            f.write(f"\n**Total de questões a substituir**: {len(to_replace)}\n")
        else:
            f.write("## ✅ NENHUMA QUESTÃO CRÍTICA ENCONTRADA\n")
    
    print(f"\n✓ Relatório salvo em: QUESTOES_CRITICAS.md")
    print(f"✓ Total de pares críticos: {len(critical_pairs)}")
    
    if critical_pairs:
        to_replace = set()
        for pair in critical_pairs:
            sim1_num = int(pair['q1']['simulado'].split('_')[1].split('.')[0])
            sim2_num = int(pair['q2']['simulado'].split('_')[1].split('.')[0])
            if sim1_num > sim2_num:
                to_replace.add((pair['q1']['simulado'], pair['q1']['index']))
            else:
                to_replace.add((pair['q2']['simulado'], pair['q2']['index']))
        
        print(f"\n⚠️  {len(to_replace)} questões precisam ser substituídas")
        print("⚠️  Veja o arquivo QUESTOES_CRITICAS.md para detalhes")
    else:
        print("\n✅ Nenhuma questão crítica encontrada!")

if __name__ == '__main__':
    main()
