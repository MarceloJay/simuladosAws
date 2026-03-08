#!/usr/bin/env python3
import json
import os

def load_simulado(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_simulado(filepath, questions):
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(questions, f, ensure_ascii=False, indent=2)

# Novas questões para substituir as duplicadas
new_questions = {
    'sim1_q56': {
        "question": "56/65 - Qual serviço da AWS fornece um repositório Git totalmente gerenciado para hospedar código-fonte?",
        "options": ["AWS CodeCommit", "AWS CodeBuild", "AWS CodeDeploy", "AWS CodePipeline"],
        "correctAnswers": [0]
    },
    'sim3_q22': {
        "question": "22/65 - Qual serviço da AWS permite criar fluxos de trabalho visuais para orquestrar serviços da AWS?",
        "options": ["AWS Step Functions", "AWS Lambda", "Amazon SQS", "AWS Batch"],
        "correctAnswers": [0]
    },
    'sim3_q29': {
        "question": "29/65 - Qual serviço da AWS fornece análise de dados em tempo real usando Apache Kafka?",
        "options": ["Amazon Kinesis", "Amazon MSK", "Amazon SQS", "AWS Glue"],
        "correctAnswers": [1]
    },
    'sim3_q59': {
        "question": "59/65 - Qual serviço da AWS permite converter texto em fala natural?",
        "options": ["Amazon Transcribe", "Amazon Polly", "Amazon Translate", "Amazon Lex"],
        "correctAnswers": [1]
    }
}

def main():
    assets_dir = 'app/src/main/assets'
    
    print("Corrigindo duplicatas...")
    
    # Corrigir Simulado 1 - Questão 56
    sim1_path = os.path.join(assets_dir, 'simulado_1.json')
    sim1 = load_simulado(sim1_path)
    sim1[55] = new_questions['sim1_q56']  # índice 55 = questão #56
    save_simulado(sim1_path, sim1)
    print("✓ Simulado 1 - Questão #56 substituída")
    
    # Corrigir Simulado 3 - Questões 22, 29, 59
    sim3_path = os.path.join(assets_dir, 'simulado_3.json')
    sim3 = load_simulado(sim3_path)
    sim3[21] = new_questions['sim3_q22']  # índice 21 = questão #22
    sim3[28] = new_questions['sim3_q29']  # índice 28 = questão #29
    sim3[58] = new_questions['sim3_q59']  # índice 58 = questão #59
    save_simulado(sim3_path, sim3)
    print("✓ Simulado 3 - Questões #22, #29, #59 substituídas")
    
    print("\n✅ Todas as duplicatas foram corrigidas!")
    print("✅ Total de questões substituídas: 4")

if __name__ == '__main__':
    main()
