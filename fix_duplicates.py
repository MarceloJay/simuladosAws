#!/usr/bin/env python3
import json
import os

def normalize_text(text):
    """Normaliza texto para comparação"""
    return ' '.join(text.lower().strip().split())

def load_simulado(filepath):
    """Carrega um arquivo de simulado JSON"""
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_simulado(filepath, questions):
    """Salva um arquivo de simulado JSON"""
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(questions, f, ensure_ascii=False, indent=2)

def main():
    assets_dir = 'app/src/main/assets'
    
    # Carregar simulado_4 e simulado_6
    sim4_path = os.path.join(assets_dir, 'simulado_4.json')
    sim6_path = os.path.join(assets_dir, 'simulado_6.json')
    
    sim4 = load_simulado(sim4_path)
    sim6 = load_simulado(sim6_path)
    
    print(f"Simulado 4: {len(sim4)} questões")
    print(f"Simulado 6: {len(sim6)} questões")
    
    # Criar conjunto de questões normalizadas do simulado 4
    sim4_normalized = {normalize_text(q['question']) for q in sim4}
    
    # Filtrar questões do simulado 6 que não estão no simulado 4
    sim6_unique = []
    sim6_duplicates = []
    
    for idx, q in enumerate(sim6):
        normalized = normalize_text(q['question'])
        if normalized in sim4_normalized:
            sim6_duplicates.append(idx + 1)
        else:
            sim6_unique.append(q)
    
    print(f"\nQuestões únicas no simulado 6: {len(sim6_unique)}")
    print(f"Questões duplicadas removidas: {len(sim6_duplicates)}")
    print(f"Questões duplicadas: {sim6_duplicates}")
    
    # Criar backup do simulado 6 original
    backup_path = os.path.join(assets_dir, 'simulado_6.json.backup')
    with open(backup_path, 'w', encoding='utf-8') as f:
        json.dump(sim6, f, ensure_ascii=False, indent=2)
    print(f"\n✓ Backup criado: {backup_path}")
    
    # Salvar simulado 6 com apenas questões únicas
    save_simulado(sim6_path, sim6_unique)
    print(f"✓ Simulado 6 atualizado: {len(sim6_unique)} questões únicas")
    
    print("\n" + "="*80)
    print("ATENÇÃO: O simulado 6 agora tem apenas 38 questões únicas.")
    print("Você precisará adicionar 27 novas questões para completar 65 questões.")
    print("="*80)

if __name__ == '__main__':
    main()
