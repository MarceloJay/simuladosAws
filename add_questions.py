#!/usr/bin/env python3
import json

# Carregar simulado_6 atual
with open('app/src/main/assets/simulado_6.json', 'r', encoding='utf-8') as f:
    simulado_6 = json.load(f)

# 27 novas questões únicas
new_questions = [
    {
        "question": "Qual serviço da AWS permite executar código em resposta a eventos sem provisionar servidores e cobra apenas pelo tempo de computação consumido?",
        "options": [
            "Amazon EC2",
            "AWS Lambda",
            "Amazon ECS",
            "AWS Fargate"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual das seguintes opções é um serviço de banco de dados NoSQL totalmente gerenciado que oferece desempenho rápido e previsível com escalabilidade contínua?",
        "options": [
            "Amazon RDS",
            "Amazon Aurora",
            "Amazon DynamoDB",
            "Amazon Neptune"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual serviço da AWS fornece um serviço de DNS escalável e altamente disponível?",
        "options": [
            "Amazon CloudFront",
            "Amazon Route 53",
            "AWS Direct Connect",
            "Amazon VPC"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual das seguintes opções descreve melhor o conceito de elasticidade na AWS?",
        "options": [
            "A capacidade de aumentar ou diminuir recursos automaticamente com base na demanda",
            "A capacidade de executar aplicações em múltiplas regiões",
            "A capacidade de fazer backup de dados automaticamente",
            "A capacidade de criptografar dados em repouso"
        ],
        "correctAnswers": [0]
    },
    {
        "question": "Qual serviço da AWS você usaria para enviar e-mails transacionais e de marketing em grande escala?",
        "options": [
            "Amazon SNS",
            "Amazon SQS",
            "Amazon SES",
            "Amazon Pinpoint"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual das seguintes opções é uma prática recomendada ao usar o AWS IAM? (Selecione DUAS.)",
        "options": [
            "Usar a conta root para todas as operações diárias",
            "Conceder privilégios mínimos necessários",
            "Compartilhar credenciais entre usuários",
            "Rotacionar credenciais regularmente",
            "Desabilitar MFA para facilitar o acesso"
        ],
        "correctAnswers": [1, 3]
    },
    {
        "question": "Qual serviço da AWS permite criar e gerenciar certificados SSL/TLS para uso com serviços da AWS?",
        "options": [
            "AWS KMS",
            "AWS Certificate Manager",
            "AWS CloudHSM",
            "AWS Secrets Manager"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual das seguintes opções é um benefício do uso de múltiplas Availability Zones?",
        "options": [
            "Redução de custos",
            "Maior disponibilidade e tolerância a falhas",
            "Melhor desempenho de rede",
            "Acesso a mais serviços da AWS"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual serviço da AWS fornece análise de segurança automatizada e avaliação de vulnerabilidades para aplicações implantadas na AWS?",
        "options": [
            "AWS Shield",
            "Amazon Inspector",
            "AWS WAF",
            "Amazon GuardDuty"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual das seguintes opções é um serviço de armazenamento de objetos da AWS?",
        "options": [
            "Amazon EBS",
            "Amazon EFS",
            "Amazon S3",
            "AWS Storage Gateway"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual serviço da AWS permite criar ambientes de desenvolvimento integrados (IDEs) baseados em nuvem?",
        "options": [
            "AWS CodeCommit",
            "AWS CodeBuild",
            "AWS Cloud9",
            "AWS CodeDeploy"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual das seguintes opções descreve o modelo de responsabilidade compartilhada da AWS?",
        "options": [
            "A AWS é responsável por tudo, incluindo dados do cliente",
            "O cliente é responsável por tudo, incluindo hardware físico",
            "A AWS é responsável pela segurança DA nuvem, e o cliente pela segurança NA nuvem",
            "A responsabilidade é dividida igualmente entre AWS e cliente"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual serviço da AWS fornece um data warehouse totalmente gerenciado e escalável?",
        "options": [
            "Amazon RDS",
            "Amazon DynamoDB",
            "Amazon Redshift",
            "Amazon Aurora"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual das seguintes opções é uma característica das instâncias Spot do EC2?",
        "options": [
            "Garantia de disponibilidade contínua",
            "Preço fixo e previsível",
            "Podem ser interrompidas pela AWS com aviso de 2 minutos",
            "Não podem ser usadas para cargas de trabalho de produção"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual serviço da AWS permite processar e analisar dados de streaming em tempo real?",
        "options": [
            "Amazon Kinesis",
            "Amazon SQS",
            "Amazon SNS",
            "AWS Glue"
        ],
        "correctAnswers": [0]
    },
    {
        "question": "Qual das seguintes opções é um serviço de orquestração de contêineres da AWS?",
        "options": [
            "AWS Lambda",
            "Amazon ECS",
            "AWS Batch",
            "AWS Elastic Beanstalk"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual serviço da AWS fornece recomendações de otimização de custos, desempenho, segurança e tolerância a falhas?",
        "options": [
            "AWS Config",
            "AWS CloudTrail",
            "AWS Trusted Advisor",
            "Amazon CloudWatch"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual das seguintes opções é uma vantagem de usar o Amazon RDS em vez de gerenciar seu próprio banco de dados em uma instância EC2?",
        "options": [
            "Acesso root completo ao sistema operacional",
            "Backups automáticos e patches de software",
            "Custos mais baixos",
            "Maior controle sobre a configuração do banco de dados"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual serviço da AWS permite conectar sua rede local à AWS usando uma conexão de rede privada dedicada?",
        "options": [
            "AWS VPN",
            "AWS Direct Connect",
            "Amazon VPC Peering",
            "AWS Transit Gateway"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual das seguintes opções é um serviço de cache em memória da AWS?",
        "options": [
            "Amazon RDS",
            "Amazon ElastiCache",
            "Amazon DynamoDB",
            "Amazon Aurora"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual serviço da AWS permite executar consultas SQL interativas diretamente em dados armazenados no Amazon S3?",
        "options": [
            "Amazon RDS",
            "Amazon Redshift",
            "Amazon Athena",
            "Amazon EMR"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual das seguintes opções é um princípio de design do Well-Architected Framework da AWS?",
        "options": [
            "Usar apenas uma Availability Zone para reduzir custos",
            "Implementar segurança em todas as camadas",
            "Evitar automação para manter controle",
            "Usar sempre os maiores tipos de instância"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual serviço da AWS fornece um serviço de notificação pub/sub totalmente gerenciado?",
        "options": [
            "Amazon SQS",
            "Amazon SNS",
            "Amazon SES",
            "Amazon Kinesis"
        ],
        "correctAnswers": [1]
    },
    {
        "question": "Qual das seguintes opções descreve melhor o Amazon CloudWatch?",
        "options": [
            "Um serviço de monitoramento e observabilidade",
            "Um serviço de armazenamento de objetos",
            "Um serviço de banco de dados",
            "Um serviço de rede de entrega de conteúdo"
        ],
        "correctAnswers": [0]
    },
    {
        "question": "Qual serviço da AWS permite automatizar a criação e gerenciamento de recursos da AWS usando templates?",
        "options": [
            "AWS CloudFormation",
            "AWS Elastic Beanstalk",
            "AWS OpsWorks",
            "AWS Systems Manager"
        ],
        "correctAnswers": [0]
    },
    {
        "question": "Qual das seguintes opções é uma classe de armazenamento do Amazon S3 projetada para dados acessados com pouca frequência?",
        "options": [
            "S3 Standard",
            "S3 Intelligent-Tiering",
            "S3 Standard-IA",
            "S3 One Zone-IA"
        ],
        "correctAnswers": [2]
    },
    {
        "question": "Qual serviço da AWS fornece proteção gerenciada contra ataques DDoS para aplicações executadas na AWS?",
        "options": [
            "AWS WAF",
            "AWS Shield Standard",
            "Amazon GuardDuty",
            "AWS Firewall Manager"
        ],
        "correctAnswers": [1]
    }
]

# Adicionar as novas questões
simulado_6.extend(new_questions)

# Salvar o arquivo atualizado
with open('app/src/main/assets/simulado_6.json', 'w', encoding='utf-8') as f:
    json.dump(simulado_6, f, ensure_ascii=False, indent=2)

print(f"✓ Simulado 6 atualizado com sucesso!")
print(f"✓ Total de questões: {len(simulado_6)}")
print(f"✓ Questões adicionadas: {len(new_questions)}")
