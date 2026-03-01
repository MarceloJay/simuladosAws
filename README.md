# 📚 Simulados AWS - Quiz App

Aplicativo Android para prática de questões de certificação AWS (Amazon Web Services) através de simulados interativos.

## 🎯 Sobre o Projeto

Este aplicativo foi desenvolvido para ajudar estudantes e profissionais que estão se preparando para certificações AWS. Oferece uma interface intuitiva para realizar simulados com questões de múltipla escolha, acompanhar o desempenho e revisar as respostas.

## ✨ Funcionalidades

- 📝 **10 Simulados Completos** com questões sobre serviços AWS
- ⏱️ **Cronômetro** para acompanhar o tempo de cada simulado
- 🎲 **Seleção de Quantidade** de questões (5, 10, 15, 20, 30, 40, 50, 65)
- ✅ **Múltipla Escolha** com suporte para questões de resposta única e múltipla
- 📊 **Tela de Resultados** com estatísticas detalhadas
- 🔍 **Revisão de Respostas** com indicação de acertos e erros
- 🎨 **Interface Moderna** com Material Design

## 📱 Screenshots

### Tela Principal
Seleção de simulados e quantidade de questões

### Tela de Quiz
Interface limpa para responder as questões com cronômetro

### Tela de Resultados
Visualização do desempenho com opção de revisar respostas

### Tela de Revisão
Análise detalhada das respostas corretas e incorretas

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java
- **IDE**: Android Studio
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Build System**: Gradle (Kotlin DSL)
- **UI Components**: 
  - RecyclerView para listas
  - CardView para cards
  - Material Design Components

## 📋 Pré-requisitos

- Android Studio Arctic Fox ou superior
- JDK 11 ou superior
- Android SDK API 24+
- Gradle 8.0+

## 🚀 Como Executar

1. Clone o repositório:
```bash
git clone git@github.com:MarceloJay/simuladosAws.git
```

2. Abra o projeto no Android Studio

3. Sincronize o projeto com os arquivos Gradle

4. Execute o app em um emulador ou dispositivo físico:
```bash
./gradlew installDebug
```

## 📦 Deploy Automático

Este projeto possui CI/CD configurado com GitHub Actions para publicação automática no Google Play.

Para configurar o deploy automático, consulte o arquivo [DEPLOY.md](DEPLOY.md) com instruções detalhadas.

**Quick start:**
```bash
./scripts/setup-deploy.sh
```

## 📂 Estrutura do Projeto

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/jaydev/awsquiz/
│   │   │   ├── MainActivity.java          # Tela principal
│   │   │   ├── QuizActivity.java          # Tela do quiz
│   │   │   ├── ResultActivity.java        # Tela de resultados
│   │   │   ├── ReviewActivity.java        # Tela de revisão
│   │   │   ├── ReviewAdapter.java         # Adapter para revisão
│   │   │   ├── data/
│   │   │   │   └── QuestionBank.java      # Gerenciador de questões
│   │   │   └── models/
│   │   │       └── Question.java          # Modelo de questão
│   │   ├── assets/
│   │   │   ├── simulado_1.json            # Simulado 1 (65 questões)
│   │   │   ├── simulado_2.json            # Simulado 2 (20 questões)
│   │   │   ├── simulado_3.json            # Simulado 3 (65 questões)
│   │   │   ├── simulado_4.json            # Simulado 4 (65 questões)
│   │   │   ├── simulado_5.json            # Simulado 5 (65 questões)
│   │   │   ├── simulado_6.json            # Simulado 6 (65 questões)
│   │   │   ├── simulado_7.json            # Simulado 7 (65 questões)
│   │   │   ├── simulado_8.json            # Simulado 8 (65 questões)
│   │   │   ├── simulado_9.json            # Simulado 9 (65 questões)
│   │   │   └── simulado_10.json           # Simulado 10 (65 questões)
│   │   └── res/
│   │       ├── layout/                     # Layouts XML
│   │       ├── drawable/                   # Recursos gráficos
│   │       └── values/                     # Cores, strings, temas
│   └── test/                               # Testes unitários
└── scripts/                                # Scripts Python auxiliares
```

## 📝 Formato das Questões

As questões são armazenadas em formato JSON:

```json
[
  {
    "question": "Qual serviço AWS...",
    "options": [
      "Opção 1",
      "Opção 2",
      "Opção 3",
      "Opção 4"
    ],
    "correctAnswers": [2]
  }
]
```

- `question`: Texto da pergunta
- `options`: Array com as opções de resposta
- `correctAnswers`: Array com os índices das respostas corretas (base 0)

## 🎓 Tópicos Cobertos

Os simulados cobrem diversos serviços e conceitos AWS:

- ☁️ Computação (EC2, Lambda, ECS, Fargate)
- 💾 Armazenamento (S3, EBS, EFS, Glacier)
- 🗄️ Banco de Dados (RDS, DynamoDB, Aurora, Redshift)
- 🌐 Rede (VPC, CloudFront, Route 53, Direct Connect)
- 🔒 Segurança (IAM, KMS, Secrets Manager, WAF, Shield)
- 📊 Monitoramento (CloudWatch, CloudTrail, Config)
- 🚀 DevOps (CodeCommit, CodeBuild, CodeDeploy, CodePipeline)
- 🤖 Machine Learning (SageMaker, Rekognition, Comprehend)
- 📦 Migração (Snow Family, DMS, DataSync)
- 💰 Gerenciamento de Custos (Cost Explorer, Budgets)

## 🔧 Scripts Auxiliares

O projeto inclui scripts Python para processamento de questões:

- `convert_simulados.py`: Converte questões para o formato correto
- `extract_questions.py`: Extrai questões de documentos
- `refine_questions.py`: Refina e valida questões
- `clean_questions.py`: Limpa e formata questões

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autor

**Marcelo Jay**

- GitHub: [@MarceloJay](https://github.com/MarceloJay)

## 🙏 Agradecimentos

- Comunidade AWS pela documentação
- Todos que contribuíram com questões e feedback

## 📞 Suporte

Se você tiver alguma dúvida ou sugestão, sinta-se à vontade para abrir uma issue no GitHub.

---

⭐ Se este projeto te ajudou, considere dar uma estrela no repositório!
