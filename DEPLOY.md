# 🚀 Deploy Automático para Google Play

Este projeto usa GitHub Actions para fazer deploy automático no Google Play Console.

## 📋 Pré-requisitos

### 1. Keystore para Assinatura do App

Você precisa de um keystore para assinar o APK/AAB. Se ainda não tem, crie um:

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias simulados-aws
```

Guarde as seguintes informações:
- **Senha do keystore**
- **Alias da chave**
- **Senha da chave**

### 2. Service Account do Google Play

1. Acesse o [Google Play Console](https://play.google.com/console)
2. Vá em **Configurações** → **Acesso à API**
3. Clique em **Criar nova conta de serviço**
4. Siga as instruções para criar uma conta de serviço no Google Cloud Console
5. Baixe o arquivo JSON da conta de serviço
6. No Play Console, conceda permissões à conta de serviço:
   - **Gerenciar versões de produção**
   - **Gerenciar versões de teste**

## 🔐 Configurar Secrets no GitHub

Vá em **Settings** → **Secrets and variables** → **Actions** e adicione:

### 1. KEYSTORE_BASE64
Converta seu keystore para base64:
```bash
base64 -i release-keystore.jks | pbcopy  # macOS
base64 -w 0 release-keystore.jks         # Linux
```
Cole o resultado no secret `KEYSTORE_BASE64`

### 2. KEYSTORE_PASSWORD
A senha do keystore

### 3. KEY_ALIAS
O alias da chave (ex: `simulados-aws`)

### 4. KEY_PASSWORD
A senha da chave

### 5. GOOGLE_PLAY_SERVICE_ACCOUNT_JSON
Cole o conteúdo completo do arquivo JSON da conta de serviço

## 🎯 Como Usar

### Deploy Automático
O deploy acontece automaticamente quando você:
- Faz push na branch `main`
- Cria uma tag com versão (ex: `v1.0.0`)

### Deploy Manual
1. Vá em **Actions** no GitHub
2. Selecione **Android Release to Google Play**
3. Clique em **Run workflow**
4. Escolha a branch e clique em **Run workflow**

## 📦 Tracks de Publicação

O workflow publica no track **internal** por padrão. Você pode alterar para:
- `internal` - Teste interno (até 100 testadores)
- `alpha` - Teste alfa
- `beta` - Teste beta
- `production` - Produção

Para alterar, edite o arquivo `.github/workflows/android-release.yml`:
```yaml
track: internal  # Altere para: alpha, beta ou production
```

## 🔄 Versionamento

Antes de fazer deploy, atualize a versão no `app/build.gradle.kts`:

```kotlin
versionCode = 2  // Incrementar a cada release
versionName = "1.1.0"  // Versão semântica
```

## ✅ Checklist de Deploy

- [ ] Atualizar `versionCode` e `versionName`
- [ ] Testar o app localmente
- [ ] Fazer commit e push das alterações
- [ ] Criar tag de versão (opcional): `git tag v1.0.0 && git push --tags`
- [ ] Verificar o workflow no GitHub Actions
- [ ] Conferir no Google Play Console se o upload foi bem-sucedido

## 🐛 Troubleshooting

### Erro de assinatura
- Verifique se o `KEYSTORE_BASE64` está correto
- Confirme que as senhas estão corretas

### Erro de upload para Google Play
- Verifique se a conta de serviço tem as permissões corretas
- Confirme que o `packageName` está correto
- Certifique-se de que o `versionCode` é maior que a versão anterior

### Build falha
- Verifique se o JDK 17 está sendo usado
- Confirme que não há erros de compilação localmente

## 📚 Recursos

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Google Play Console](https://play.google.com/console)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
