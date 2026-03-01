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
echo "Digite 'sair' para encerrar"
echo ""

while true; do
    read -sp "Digite a senha para testar: " PASSWORD
    echo ""
    
    if [ "$PASSWORD" = "sair" ]; then
        echo "👋 Encerrando..."
        exit 0
    fi
    
    if [ -z "$PASSWORD" ]; then
        echo "❌ Senha vazia. Tente novamente."
        echo ""
        continue
    fi
    
    # Testar a senha
    RESULT=$(keytool -list -v -keystore "$KEYSTORE_PATH" -storepass "$PASSWORD" -alias "$ALIAS" 2>&1)
    
    if echo "$RESULT" | grep -q "Alias name: $ALIAS"; then
        echo ""
        echo "✅ ✅ ✅ SENHA CORRETA! ✅ ✅ ✅"
        echo ""
        echo "═══════════════════════════════════════"
        echo "GUARDE ESTAS INFORMAÇÕES:"
        echo "═══════════════════════════════════════"
        echo ""
        echo "KEYSTORE_PASSWORD: $PASSWORD"
        echo "KEY_PASSWORD: $PASSWORD"
        echo ""
        echo "═══════════════════════════════════════"
        echo ""
        
        # Salvar em arquivo temporário
        echo "KEYSTORE_PASSWORD=$PASSWORD" > .senha-encontrada.txt
        echo "KEY_PASSWORD=$PASSWORD" >> .senha-encontrada.txt
        echo ""
        echo "✅ Senha salva em: .senha-encontrada.txt"
        echo ""
        
        # Mostrar informações do certificado
        echo "📋 Informações do Certificado:"
        echo "$RESULT" | grep -A 5 "Alias name:"
        echo ""
        
        exit 0
    else
        echo "❌ Senha incorreta. Tente outra."
        echo ""
    fi
done
