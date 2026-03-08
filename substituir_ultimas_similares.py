#!/usr/bin/env python3
import json
import os

def load_simulado(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_simulado(filepath, questions):
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(questions, f, ensure_ascii=False, indent=2)

# 3 novas questões completamente diferentes
new_questions = [
    {
        "question": "Qual serviço da AWS permite criar e hospedar sites estáticos com certificados SSL gratuitos?",
        "options": ["Amazon S3 + CloudFront", "AWS Amplify", "Amazon Lightsail", "AWS Elastic Beanstalk"],
        "correctAnswers": [1]
    },
    {
        "question": "Qual das seguintes opções é um serviço de gerenciamento de patches e conformidade para instâncias EC2?",
        "options": ["AWS Systems Manager Patch Manager", "AWS Config", "Amazon Inspector", "AWS CloudTrail"],
        "correctAnswers": [0]
    },
    {
        "question": "Qual serviço da AWS fornece um serviço de conversão de vídeo e áudio na nuvem?",
        "options": ["AWS Elemental MediaConvert", "Amazon Kinesis Video Streams", "AWS Elemental MediaLive", "Amazon Transcribe"],
        "correctAnswers": [0]
    }
]

def main():
    assets_dir = 'app/src/main/assets'
    
    replacements = [
        ('simulado_7.json', 6, 0),
        ('simulado_7.json', 35, 1),
        ('simulado_8.json', 8, 2),
    ]
    
    print("Substituindo últimas questões similares...")
    
    for sim_file, q_num, new_idx in replacements:
        sim_path = os.path.join(assets_dir, sim_file)
        simulado = load_simulado(sim_path)
        simulado[q_num - 1] = new_questions[new_idx]
        save_simulado(sim_path, simulado)
        print(f"✓ {sim_file} - Questão #{q_num} substituída")
    
    print(f"\n✅ Todas as 3 questões similares restantes foram substituídas!")

if __name__ == '__main__':
    main()
