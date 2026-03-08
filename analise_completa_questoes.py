#!/usr/bin/env python3
import json
import os
from collections import defaultdict
import re

def normalize_text(text):
    """Normaliza texto para comparação (remove espaços extras, lowercase)"""
    # Remove números de questão (ex: "1/20 - ", "21/65 - ")
    text = re.sub(r'^\d+/\d+\s*-\s*', '', text)
    return ' '.join(text.lower().strip().split())

def extract_core_concept(question):
    """Extrai o conceito principal da questão"""
    # Remove prefixos comuns
    q = question.lower()
    q = re.sub(r'^\d+/\d+\s*-\s*', '', q)
    
    # Identifica serviços AWS mencionados
    aws_services = re.findall(r'(amazon\s+\w+|aws\s+\w+)', q)
    
    # Identifica conceitos chave
    concepts = []
    if 'responsabilidade compartilhada' in q or 'shared responsibility' in q:
        concepts.append('shared_responsibility')
    if 'availability zone' in q or 'az' in q:
        concepts.append('availability_zone')
    if 'região' in q or 'region' in q:
        concepts.append('region')
    if 'custo' in q or 'cost' in q or 'pricing' in q:
        concepts.append('cost')
    if 'segurança' in q or 'security' in q:
        concepts.append('security')
    if 'armazenamento' in q or 'storage' in q:
        concepts.append('storage')
    if 'banco de dados' in q or 'database' in q:
        concepts.append('database')
    if 'rede' in q or 'network' in q or 'vpc' in q:
        concepts.append('network')
    
    return {
        'services': aws_services,
        'concepts': concepts
    }

def load_simulado(filepath):
    """Carrega um arquivo de simulado JSON"""
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def compare_questions(q1, q2):
    """Compara duas questões e retorna o nível de similaridade"""
    norm1 = normalize_text(q1['question'])
    norm2 = normalize_text(q2['question'])
    
    # Comparação exata
    if norm1 == norm2:
        return 'IDENTICA'
    
    # Comparação de conceitos
    concept1 = extract_core_concept(q1['question'])
    concept2 = extract_core_concept(q2['question'])
    
    # Verifica se falam dos mesmos serviços
    same_services = set(concept1['services']) & set(concept2['services'])
    same_concepts = set(concept1['concepts']) & set(concept2['concepts'])
    
    if same_services and same_concepts:
        # Verifica se as respostas são as mesmas
        if q1.get('correctAnswers') == q2.get('correctAnswers'):
            return 'MUITO_SIMILAR'
    
    return 'DIFERENTE'

def main():
    assets_dir = 'app/src/main/assets'
    
    # Carregar todos os simulados
    print("="*80)
    print("ANÁLISE COMPLETA DE QUESTÕES - AWS QUIZ")
    print("="*80)
    print("\nCarregando simulados...")
    
    simulados = {}
    for i in range(1, 11):
        filename = f'simulado_{i}.json'
        filepath = os.path.join(assets_dir, filename)
        if os.path.exists(filepath):
            simulados[filename] = load_simulado(filepath)
            print(f"✓ {filename}: {len(simulados[filename])} questões")
    
    total_questions = sum(len(q) for q in simulados.values())
    print(f"\nTotal de questões: {total_questions}")
    print("\n" + "="*80)
    print("INICIANDO ANÁLISE DETALHADA...")
    print("="*80)
    
    # Criar índice de todas as questões
    all_questions = []
    for sim_name, questions in simulados.items():
        for idx, q in enumerate(questions):
            all_questions.append({
                'simulado': sim_name,
                'index': idx + 1,
                'question': q['question'],
                'options': q.get('options', []),
                'correctAnswers': q.get('correctAnswers', []),
                'normalized': normalize_text(q['question'])
            })
    
    print(f"\nAnalisando {len(all_questions)} questões...")
    print("Isso pode levar alguns minutos...\n")
    
    # Encontrar duplicatas exatas
    exact_duplicates = defaultdict(list)
    for q in all_questions:
        exact_duplicates[q['normalized']].append(q)
    
    exact_dups = {k: v for k, v in exact_duplicates.items() if len(v) > 1}
    
    # Encontrar questões muito similares
    similar_questions = []
    checked_pairs = set()
    
    for i, q1 in enumerate(all_questions):
        if (i + 1) % 50 == 0:
            print(f"Progresso: {i+1}/{len(all_questions)} questões analisadas...")
        
        for j, q2 in enumerate(all_questions[i+1:], i+1):
            pair_key = f"{q1['simulado']}:{q1['index']}-{q2['simulado']}:{q2['index']}"
            if pair_key in checked_pairs:
                continue
            
            checked_pairs.add(pair_key)
            
            similarity = compare_questions(q1, q2)
            if similarity == 'MUITO_SIMILAR':
                similar_questions.append({
                    'q1': q1,
                    'q2': q2,
                    'similarity': similarity
                })
    
    # Gerar relatório
    print("\n" + "="*80)
    print("RELATÓRIO DE ANÁLISE")
    print("="*80)
    
    if exact_dups:
        print(f"\n🔴 QUESTÕES IDÊNTICAS ENCONTRADAS: {len(exact_dups)}")
        print("\nDetalhes:")
        for idx, (normalized, occurrences) in enumerate(exact_dups.items(), 1):
            print(f"\n--- Duplicata #{idx} ---")
            print(f"Questão: {occurrences[0]['question'][:100]}...")
            print(f"Aparece em {len(occurrences)} simulados:")
            for occ in occurrences:
                print(f"  • {occ['simulado']} - Questão #{occ['index']}")
    else:
        print("\n✅ NENHUMA QUESTÃO IDÊNTICA ENCONTRADA!")
    
    if similar_questions:
        print(f"\n⚠️  QUESTÕES MUITO SIMILARES ENCONTRADAS: {len(similar_questions)}")
        print("\nDetalhes:")
        for idx, sim in enumerate(similar_questions[:10], 1):  # Mostrar apenas as primeiras 10
            print(f"\n--- Similaridade #{idx} ---")
            print(f"Q1: {sim['q1']['simulado']} - #{sim['q1']['index']}")
            print(f"    {sim['q1']['question'][:80]}...")
            print(f"Q2: {sim['q2']['simulado']} - #{sim['q2']['index']}")
            print(f"    {sim['q2']['question'][:80]}...")
        
        if len(similar_questions) > 10:
            print(f"\n... e mais {len(similar_questions) - 10} questões similares")
    else:
        print("\n✅ NENHUMA QUESTÃO MUITO SIMILAR ENCONTRADA!")
    
    # Estatísticas por simulado
    print(f"\n" + "="*80)
    print("ESTATÍSTICAS POR SIMULADO:")
    print("="*80)
    for sim_name, questions in sorted(simulados.items()):
        print(f"  {sim_name}: {len(questions)} questões")
    
    # Resumo final
    print(f"\n" + "="*80)
    print("RESUMO FINAL:")
    print("="*80)
    print(f"  • Total de questões: {total_questions}")
    print(f"  • Questões idênticas: {len(exact_dups)}")
    print(f"  • Questões muito similares: {len(similar_questions)}")
    print(f"  • Questões únicas: {total_questions - len(exact_dups) - len(similar_questions)}")
    
    if len(exact_dups) == 0 and len(similar_questions) == 0:
        print("\n✅ TODAS AS 650 QUESTÕES SÃO ÚNICAS!")
        print("✅ NÃO HÁ DUPLICATAS OU QUESTÕES COM CONTEXTO SIMILAR!")
    else:
        print("\n⚠️  ATENÇÃO: Foram encontradas questões duplicadas ou similares.")
        print("⚠️  Recomenda-se revisar e substituir essas questões.")
    
    print("\n" + "="*80)

if __name__ == '__main__':
    main()
