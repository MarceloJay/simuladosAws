# 🚀 Guia Completo de Publicação no Google Play

## ✅ CHECKLIST ANTES DE COMEÇAR

- [ ] Conta no Google Play Console criada
- [ ] App já cadastrado no Play Console (ou pronto para cadastrar)
- [ ] Keystore para assinar o app
- [ ] Service Account do Google Cloud configurada

---

## 📝 PASSO 1: CRIAR KEYSTORE

Execute o comando abaixo para criar o keystore:

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias simulados-aws
```

**Informações que você precisará fornecer:**
- Nome e sobrenome
- Nome da organização
- Nome da unidade organizacional
- Cidade
- Estado
- Código do país (BR)
- **Senha do keystore** (GUARDE BEM!)
- **Senha da chave** (GUARDE BEM!)

**⚠️ IMPORTANTE:** Guarde essas senhas em local seguro! Sem elas você não conseguirá atualizar o app no futuro.

---

## 🔐 PASSO 2: CONVERTER KEYSTORE PARA BASE64

Execute o script auxiliar:

```bash
chmod +x scripts/setup-deploy.sh
./scripts/setup-deploy.sh
```

Escolha a opção 2 (converter keystore existente) ou 3 (criar e converter).

O script vai gerar o valor base64 que você precisará adicionar no GitHub.

---

## ☁️ PASSO 3: CRIAR SERVICE ACCOUNT NO GOOGLE CLOUD

### 3.1 Acessar Google Play Console
1. Acesse: https://play.google.com/console
2. Selecione seu app (ou crie um novo)

### 3.2 Configurar API Access
1. Vá em **Configurações** (Settings) → **Acesso à API** (API access)
2. Clique em **Criar nova conta de serviço** (Create new service account)
3. Você será redirecionado para o Google Cloud Console

### 3.3 No Google Cloud Console
1. Clique em **+ CREATE SERVICE ACCOUNT**
2. Preencha:
   - **Service account name:** `github-actions-deploy`
   - **Service account ID:** (será preenchido automaticamente)
   - **Description:** `Service account para deploy automático via GitHub Actions`
3. Clique em **CREATE AND CONTINUE**
4. Em **Grant this service account access to project**, clique em **CONTINUE** (sem adicionar roles)
5. Clique em **DONE**

### 3.4 Criar Chave JSON
1. Na lista de service accounts, clique na conta que você acabou de criar
2. Vá na aba **KEYS**
3. Clique em **ADD KEY** → **Create new key**
4. Escolha **JSON**
5. Clique em **CREATE**
6. O arquivo JSON será baixado automaticamente
7. **⚠️ GUARDE ESTE ARQUIVO COM SEGURANÇA!**

### 3.5 Voltar ao Play Console
1. Volte para o Google Play Console
2. Clique em **Concluir** (Done) na página de API access
3. A service account aparecerá na lista
4. Clique em **Gerenciar permissões do Play Console** (Manage Play Console permissions)
5. Na aba **Permissões do app** (App permissions):
   - Selecione seu app
   - Marque as permissões:
     - ✅ **Criar e editar versões de rascunho**
     - ✅ **Gerenciar versões de produção**
     - ✅ **Gerenciar versões de teste**
     - ✅ **Ver informações do app**
6. Clique em **Aplicar** (Apply)
7. Na aba **Permissões da conta** (Account permissions):
   - Deixe tudo desmarcado (não precisa de permissões de conta)
8. Clique em **Convidar usuário** (Invite user)

---

## 🔑 PASSO 4: CONFIGURAR SECRETS NO GITHUB

1. Acesse seu repositório no GitHub
2. Vá em **Settings** → **Secrets and variables** → **Actions**
3. Clique em **New repository secret**

Adicione os seguintes secrets:

### 4.1 KEYSTORE_BASE64
- **Name:** `KEYSTORE_BASE64`
- **Value:** Cole o valor base64 gerado pelo script (ou execute: `base64 -i release-keystore.jks | pbcopy`)

### 4.2 KEYSTORE_PASSWORD
- **Name:** `KEYSTORE_PASSWORD`
- **Value:** A senha do keystore que você criou

### 4.3 KEY_ALIAS
- **Name:** `KEY_ALIAS`
- **Value:** `simulados-aws` (ou o alias que você usou)

### 4.4 KEY_PASSWORD
- **Name:** `KEY_PASSWORD`
- **Value:** A senha da chave que você criou

### 4.5 GOOGLE_PLAY_SERVICE_ACCOUNT_JSON
- **Name:** `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON`
- **Value:** Abra o arquivo JSON baixado e cole TODO o conteúdo (incluindo as chaves `{` e `}`)

---

## 📦 PASSO 5: ATUALIZAR VERSÃO DO APP

Antes de fazer o deploy, atualize a versão no arquivo `app/build.gradle.kts`:

```kotlin
versionCode = 7  // Incrementar em 1
versionName = "1.1.0"  // Atualizar conforme necessário
```

**Regras:**
- `versionCode` deve sempre aumentar (nunca diminuir)
- `versionName` é a versão que aparece para os usuários

---

## 🚀 PASSO 6: FAZER DEPLOY

### Opção 1: Deploy Automático (Push para Main)
```bash
git add .
git commit -m "chore: preparar versão 1.1.0 para deploy"
git push origin main
```

### Opção 2: Deploy com Tag
```bash
git tag v1.1.0
git push origin v1.1.0
```

### Opção 3: Deploy Manual
1. Vá em **Actions** no GitHub
2. Selecione **Android Release to Google Play**
3. Clique em **Run workflow**
4. Escolha a branch `main`
5. Clique em **Run workflow**

---

## 👀 PASSO 7: ACOMPANHAR O DEPLOY

1. Vá em **Actions** no GitHub
2. Clique no workflow que está executando
3. Acompanhe cada etapa:
   - ✅ Checkout code
   - ✅ Set up JDK 17
   - ✅ Decode Keystore
   - ✅ Build Release AAB
   - ✅ Upload to Google Play
   - ✅ Upload AAB as artifact

Se tudo der certo, você verá ✅ em todas as etapas!

---

## 🎯 PASSO 8: VERIFICAR NO GOOGLE PLAY CONSOLE

1. Acesse: https://play.google.com/console
2. Selecione seu app
3. Vá em **Versões** → **Teste interno** (Internal testing)
4. Você verá a nova versão na fila de revisão
5. Clique em **Revisar versão** e depois **Iniciar lançamento**

---

## 🐛 TROUBLESHOOTING

### Erro: "Package not found"
- Verifique se o `packageName` no workflow está correto: `com.jaydev.awsquiz`
- Confirme que o app já está cadastrado no Play Console

### Erro: "Version code X has already been used"
- Aumente o `versionCode` no `app/build.gradle.kts`

### Erro: "Unauthorized"
- Verifique se a service account tem as permissões corretas
- Confirme que o JSON está completo no secret

### Erro: "Invalid keystore"
- Verifique se o base64 está correto
- Confirme que as senhas estão corretas

---

## 📚 RECURSOS ÚTEIS

- [Google Play Console](https://play.google.com/console)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)

---

## ✅ CHECKLIST FINAL

- [ ] Keystore criado e guardado com segurança
- [ ] Service Account criada no Google Cloud
- [ ] Permissões configuradas no Play Console
- [ ] Todos os 5 secrets configurados no GitHub
- [ ] versionCode incrementado
- [ ] Commit e push feitos
- [ ] Workflow executado com sucesso
- [ ] Versão apareceu no Play Console

---

**🎉 Parabéns! Seu app está publicado no Google Play!**
