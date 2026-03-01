# 🚀 PASSO A PASSO - VAMOS PUBLICAR AGORA!

## ✅ O QUE JÁ TEMOS:

- ✅ Keystore: `/Users/marceloferreira/KeyStores/AwsQuiz.jks`
- ✅ Key Alias: `release`
- ✅ Base64 gerado: `keystore-base64.txt`
- ✅ Versão atualizada: 7 (1.1.0)
- ✅ Workflow configurado: `.github/workflows/android-release.yml`

---

## 📋 PASSO 1: CONFIGURAR SECRETS NO GITHUB (5 minutos)

### 1.1 Acessar GitHub Secrets
1. Abra: https://github.com/MarceloJay/simuladosAws/settings/secrets/actions
2. Clique em: **New repository secret**

### 1.2 Adicionar Secret 1: KEYSTORE_BASE64
1. **Name:** `KEYSTORE_BASE64`
2. **Value:** Abra o arquivo `keystore-base64.txt` e copie TODO o conteúdo
3. Clique em **Add secret**

### 1.3 Adicionar Secret 2: KEYSTORE_PASSWORD
1. **Name:** `KEYSTORE_PASSWORD`
2. **Value:** A senha do keystore (os pontinhos do Android Studio)
3. Clique em **Add secret**

### 1.4 Adicionar Secret 3: KEY_ALIAS
1. **Name:** `KEY_ALIAS`
2. **Value:** `release`
3. Clique em **Add secret**

### 1.5 Adicionar Secret 4: KEY_PASSWORD
1. **Name:** `KEY_PASSWORD`
2. **Value:** A senha da chave (os pontinhos de baixo do Android Studio)
3. Clique em **Add secret**

### 1.6 Adicionar Secret 5: GOOGLE_PLAY_SERVICE_ACCOUNT_JSON
⚠️ **AGUARDE!** Vamos criar a Service Account primeiro (Passo 2)

---

## ☁️ PASSO 2: CRIAR SERVICE ACCOUNT NO GOOGLE PLAY (10 minutos)

### 2.1 Acessar Google Play Console
1. Acesse: https://play.google.com/console
2. Selecione seu app: **AWS Quiz** (ou como está cadastrado)

### 2.2 Ir para API Access
1. No menu lateral, vá em: **Configurações** (Settings)
2. Clique em: **Acesso à API** (API access)

### 2.3 Criar Service Account
1. Clique em: **Criar nova conta de serviço** (Create new service account)
2. Você será redirecionado para o **Google Cloud Console**

### 2.4 No Google Cloud Console
1. Clique em: **+ CREATE SERVICE ACCOUNT**
2. Preencha:
   - **Service account name:** `github-actions-awsquiz`
   - **Service account ID:** (preenchido automaticamente)
   - **Description:** `Deploy automático via GitHub Actions`
3. Clique em: **CREATE AND CONTINUE**
4. Pule a parte de roles (clique em **CONTINUE**)
5. Clique em: **DONE**

### 2.5 Criar Chave JSON
1. Na lista de service accounts, clique na conta que você criou
2. Vá na aba: **KEYS**
3. Clique em: **ADD KEY** → **Create new key**
4. Escolha: **JSON**
5. Clique em: **CREATE**
6. O arquivo JSON será baixado automaticamente
7. **⚠️ GUARDE ESTE ARQUIVO COM SEGURANÇA!**

### 2.6 Voltar ao Play Console e Dar Permissões
1. Volte para o Google Play Console
2. Clique em **Concluir** (Done) na página de API access
3. A service account aparecerá na lista
4. Clique em: **Gerenciar permissões do Play Console**
5. Na aba **Permissões do app**:
   - Selecione seu app
   - Marque:
     - ✅ Criar e editar versões de rascunho
     - ✅ Gerenciar versões de produção
     - ✅ Gerenciar versões de teste
     - ✅ Ver informações do app
6. Clique em: **Aplicar** (Apply)
7. Clique em: **Convidar usuário** (Invite user)

### 2.7 Adicionar o JSON no GitHub
1. Abra o arquivo JSON que foi baixado
2. Copie TODO o conteúdo (incluindo `{` e `}`)
3. Volte para: https://github.com/MarceloJay/simuladosAws/settings/secrets/actions
4. Clique em: **New repository secret**
5. **Name:** `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON`
6. **Value:** Cole o conteúdo completo do JSON
7. Clique em: **Add secret**

---

## 🚀 PASSO 3: FAZER O DEPLOY! (2 minutos)

### 3.1 Verificar se todos os secrets estão configurados
Você deve ter 5 secrets:
- ✅ KEYSTORE_BASE64
- ✅ KEYSTORE_PASSWORD
- ✅ KEY_ALIAS
- ✅ KEY_PASSWORD
- ✅ GOOGLE_PLAY_SERVICE_ACCOUNT_JSON

### 3.2 Fazer Commit e Push
Execute no terminal:

```bash
git add .
git commit -m "chore: preparar versão 1.1.0 para deploy"
git push origin main
```

### 3.3 Acompanhar o Deploy
1. Acesse: https://github.com/MarceloJay/simuladosAws/actions
2. Você verá o workflow **Android Release to Google Play** executando
3. Clique nele para ver o progresso
4. Aguarde até ver ✅ em todas as etapas (leva ~5-10 minutos)

---

## 🎯 PASSO 4: VERIFICAR NO GOOGLE PLAY (1 minuto)

1. Acesse: https://play.google.com/console
2. Selecione seu app
3. Vá em: **Versões** → **Teste interno** (Internal testing)
4. Você verá a versão 1.1.0 (7) na fila
5. Clique em: **Revisar versão**
6. Clique em: **Iniciar lançamento**

---

## 🎉 PRONTO! SEU APP FOI PUBLICADO!

A partir de agora, toda vez que você fizer push para `main`, o app será automaticamente publicado no Google Play!

---

## 🐛 SE DER ERRO:

### Erro: "Package not found"
- Verifique se o app já está cadastrado no Play Console
- Confirme que o packageName é: `com.jaydev.awsquiz`

### Erro: "Version code already used"
- Aumente o versionCode no `app/build.gradle.kts`

### Erro: "Unauthorized"
- Verifique se a service account tem as permissões corretas
- Confirme que o JSON está completo no secret

### Erro: "Invalid keystore"
- Verifique se o base64 está correto
- Confirme que as senhas estão corretas

---

## 📞 PRECISA DE AJUDA?

Me avise em qual passo você está e se encontrou algum problema!
