#!/usr/bin/env python3
import json
import os
from collections import defaultdict
import re

def normalize_text(text):
    """Normaliza texto para comparação"""
    text = re.sub(r'^\d+/\d+\s*-\s*', '', text)
    return ' '.join(text.lower().strip().split())

def load_simulado(filepath):
    """Carrega um arquivo de simulado JSON"""
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def main():
    assets_dir = 'app/src/main/assets'
    
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
                'normalized': normalize_text(q['question'])
            })
    
    # Encontrar duplicatas exatas
    exact_duplicates = defaultdict(list)
    for q in all_questions:
        exact_duplicates[q['normalized']].append(q)
    
    exact_dups = {k: v for k, v in exact_duplicates.items() if len(v) > 1}
    
    # Gerar relatório em arquivo
    with open('RELATORIO_QUESTOES_DETALHADO.md', 'w', encoding='utf-8') as f:
        f.write("# RELATÓRIO DETALHADO DE ANÁLISE DE QUESTÕES\n\n")
        f.write("## Data: 7 de março de 2026\n\n")
        f.write("="*80 + "\n\n")
        
        f.write("## RESUMO EXECUTIVO\n\n")
        f.write(f"- **Total de questões**: 650 (10 simulados × 65 questões)\n")
        f.write(f"- **Questões idênticas encontradas**: {len(exact_dups)}\n")
        f.write(f"- **Status**: {'✅ APROVADO' if len(exact_dups) == 0 else '❌ REPROVADO'}\n\n")
        
        if exact_dups:
            f.write("## QUESTÕES IDÊNTICAS ENCONTRADAS\n\n")
            f.write(f"Foram encontradas **{len(exact_dups)} questões duplicadas**:\n\n")
            
            for idx, (normalized, occurrences) in enumerate(exact_dups.items(), 1):
                f.write(f"### Duplicata #{idx}\n\n")
                f.write(f"**Questão**: {occurrences[0]['question']}\n\n")
                f.write(f"**Aparece em {len(occurrences)} locais**:\n\n")
                for occ in occurrences:
                    f.write(f"- {occ['simulado']} - Questão #{occ['index']}\n")
                f.write("\n")
                f.write("**AÇÃO NECESSÁRIA**: Remover ou substituir esta questão duplicada.\n\n")
                f.write("---\n\n")
        else:
            f.write("## ✅ RESULTADO\n\n")
            f.write("**TODAS AS 650 QUESTÕES SÃO ÚNICAS!**\n\n")
            f.write("Não foram encontradas questões idênticas entre os simulados.\n\n")
        
        f.write("## ESTATÍSTICAS POR SIMULADO\n\n")
        f.write("| Simulado | Questões |\n")
        f.write("|----------|----------|\n")
        for sim_name, questions in sorted(simulados.items()):
            f.write(f"| {sim_name} | {len(questions)} |\n")
        
        f.write("\n## CONCLUSÃO\n\n")
        if len(exact_dups) == 0:
            f.write("✅ O aplicativo está pronto para publicação.\n")
            f.write("✅ Todas as questões são únicas e não há duplicatas.\n")
        else:
            f.write("❌ O aplicativo NÃO está pronto para publicação.\n")
            f.write(f"❌ É necessário corrigir {len(exact_dups)} questões duplicadas.\n")
    
    print("✓ Relatório detalhado salvo em: RELATORIO_QUESTOES_DETALHADO.md")
    
    # Retornar código de saída
    return 0 if len(exact_dups) == 0 else 1

if __name__ == '__main__':
    exit(main())
