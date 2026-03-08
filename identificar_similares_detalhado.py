#!/usr/bin/env python3
import json
import os
from collections import defaultdict
import re

def normalize_text(text):
    """Normaliza texto para comparação"""
    text = re.sub(r'^\d+/\d+\s*-\s*', '', text)
    return ' '.join(text.lower().strip().split())

def extract_keywords(question):
    """Extrai palavras-chave importantes da questão"""
    q = question.lower()
    q = re.sub(r'^\d+/\d+\s*-\s*', '', q)
    
    # Remove palavras comuns
    stop_words = {'o', 'a', 'os', 'as', 'de', 'da', 'do', 'das', 'dos', 'em', 'na', 'no', 
                  'para', 'por', 'com', 'um', 'uma', 'que', 'qual', 'quais', 'é', 'são',
                  'você', 'sua', 'seu', 'suas', 'seus', 'pode', 'podem', 'deve', 'devem'}
    
    words = q.split()
    keywords = [w for w in words if len(w) > 3 and w not in stop_words]
    
    # Identifica serviços AWS
    aws_services = re.findall(r'(amazon\s+\w+|aws\s+\w+|ec2|s3|rds|vpc|iam|lambda|cloudwatch|cloudtrail|ebs|efs|sns|sqs|redshift|dynamodb|aurora|route\s*53|cloudfront|elb|auto\s*scaling)', q)
    
    return {
        'keywords': set(keywords[:10]),  # Top 10 keywords
        'services': set(aws_services),
        'length': len(words)
    }

def calculate_similarity(q1, q2):
    """Calcula similaridade entre duas questões (0-100%)"""
    k1 = extract_keywords(q1['question'])
    k2 = extract_keywords(q2['question'])
    
    # Similaridade de serviços AWS
    if k1['services'] and k2['services']:
        service_overlap = len(k1['services'] & k2['services']) / max(len(k1['services']), len(k2['services']))
    else:
        service_overlap = 0
    
    # Similaridade de palavras-chave
    if k1['keywords'] and k2['keywords']:
        keyword_overlap = len(k1['keywords'] & k2['keywords']) / max(len(k1['keywords']), len(k2['keywords']))
    else:
        keyword_overlap = 0
    
    # Similaridade de tamanho
    length_similarity = 1 - abs(k1['length'] - k2['length']) / max(k1['length'], k2['length'])
    
    # Peso: serviços 50%, keywords 30%, tamanho 20%
    similarity = (service_overlap * 0.5 + keyword_overlap * 0.3 + length_similarity * 0.2) * 100
    
    return similarity

def load_simulado(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def main():
    assets_dir = 'app/src/main/assets'
    
    print("="*80)
    print("ANÁLISE DETALHADA DE QUESTÕES SIMILARES")
    print("="*80)
    
    # Carregar todos os simulados
    simulados = {}
    for i in range(1, 11):
        filename = f'simulado_{i}.json'
        filepath = os.path.join(assets_dir, filename)
        if os.path.exists(filepath):
            simulados[filename] = load_simulado(filepath)
    
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
    print("Limiar de similaridade: 70%\n")
    
    # Encontrar questões similares
    similar_pairs = []
    checked_pairs = set()
    
    for i, q1 in enumerate(all_questions):
        if (i + 1) % 100 == 0:
            print(f"Progresso: {i+1}/{len(all_questions)}...")
        
        for j, q2 in enumerate(all_questions[i+1:], i+1):
            # Pular se for o mesmo simulado e mesma questão
            if q1['simulado'] == q2['simulado'] and q1['index'] == q2['index']:
                continue
            
            pair_key = tuple(sorted([
                f"{q1['simulado']}:{q1['index']}", 
                f"{q2['simulado']}:{q2['index']}"
            ]))
            
            if pair_key in checked_pairs:
                continue
            
            checked_pairs.add(pair_key)
            
            # Calcular similaridade
            similarity = calculate_similarity(q1, q2)
            
            if similarity >= 70:  # 70% ou mais de similaridade
                similar_pairs.append({
                    'q1': q1,
                    'q2': q2,
                    'similarity': similarity
                })
    
    # Ordenar por similaridade
    similar_pairs.sort(key=lambda x: x['similarity'], reverse=True)
    
    # Salvar relatório
    with open('QUESTOES_SIMILARES_DETALHADO.md', 'w', encoding='utf-8') as f:
        f.write("# RELATÓRIO DE QUESTÕES SIMILARES\n\n")
        f.write("## Critério: Similaridade >= 70%\n\n")
        f.write(f"**Total de pares similares encontrados**: {len(similar_pairs)}\n\n")
        f.write("="*80 + "\n\n")
        
        if similar_pairs:
            f.write("## QUESTÕES SIMILARES ENCONTRADAS\n\n")
            
            for idx, pair in enumerate(similar_pairs, 1):
                f.write(f"### Par Similar #{idx} - Similaridade: {pair['similarity']:.1f}%\n\n")
                f.write(f"**Questão 1**: {pair['q1']['simulado']} - #{pair['q1']['index']}\n")
                f.write(f"```\n{pair['q1']['question']}\n```\n\n")
                f.write(f"**Questão 2**: {pair['q2']['simulado']} - #{pair['q2']['index']}\n")
                f.write(f"```\n{pair['q2']['question']}\n```\n\n")
                f.write("**AÇÃO**: Substituir uma das questões.\n\n")
                f.write("---\n\n")
        else:
            f.write("## ✅ NENHUMA QUESTÃO SIMILAR ENCONTRADA\n\n")
            f.write("Todas as questões são suficientemente diferentes.\n\n")
    
    print(f"\n✓ Relatório salvo em: QUESTOES_SIMILARES_DETALHADO.md")
    print(f"✓ Total de pares similares: {len(similar_pairs)}")
    
    if similar_pairs:
        print(f"\n⚠️  ATENÇÃO: {len(similar_pairs)} pares de questões similares encontrados!")
        print("⚠️  Recomenda-se substituir as questões similares.")
    else:
        print("\n✅ Todas as questões são suficientemente diferentes!")

if __name__ == '__main__':
    main()
