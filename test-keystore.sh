#!/bin/bash

# Script para testar senha do keystore

KEYSTORE_PATH="/Users/marceloferreira/KeyStores/AwsQuiz.jks"
ALIAS="release"

echo "🔐 Testador de Senha do Keystore"
echo "================================"
echo ""
echo "Keystore: $KEYSTORE_PATH"
echo "Alias: $ALIAS"
echo ""

read -sp "Digite a senha do keystore para testar: " PASSWORD
echo ""

# Testar a senha
keytool -list -v -keystore "$KEYSTORE_PATH" -storepass "$PASSWORD" -alias "$ALIAS" 2>&1 | head -20

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ SENHA CORRETA!"
    echo ""
    echo "Use esta senha para:"
    echo "- KEYSTORE_PASSWORD: $PASSWORD"
    echo "- KEY_PASSWORD: $PASSWORD (geralmente é a mesma)"
else
    echo ""
    echo "❌ Senha incorreta. Tente novamente."
fi
