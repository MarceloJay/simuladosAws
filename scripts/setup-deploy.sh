#!/bin/bash

echo "🚀 Setup de Deploy para Google Play"
echo "===================================="
echo ""

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Função para criar keystore
create_keystore() {
    echo -e "${YELLOW}📝 Criando novo keystore...${NC}"
    echo ""
    
    read -p "Nome do alias (ex: simulados-aws): " ALIAS
    
    keytool -genkey -v \
        -keystore release-keystore.jks \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -alias "$ALIAS"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Keystore criado com sucesso!${NC}"
        echo ""
        echo -e "${YELLOW}⚠️  IMPORTANTE: Guarde as senhas em local seguro!${NC}"
        echo ""
    else
        echo -e "${RED}❌ Erro ao criar keystore${NC}"
        exit 1
    fi
}

# Função para converter keystore para base64
convert_to_base64() {
    echo -e "${YELLOW}🔐 Convertendo keystore para base64...${NC}"
    echo ""
    
    if [ ! -f "release-keystore.jks" ]; then
        echo -e "${RED}❌ Arquivo release-keystore.jks não encontrado!${NC}"
        exit 1
    fi
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        base64 -i release-keystore.jks | pbcopy
        echo -e "${GREEN}✅ Base64 copiado para clipboard (macOS)${NC}"
    else
        # Linux
        base64 -w 0 release-keystore.jks > keystore-base64.txt
        echo -e "${GREEN}✅ Base64 salvo em keystore-base64.txt${NC}"
    fi
    
    echo ""
    echo -e "${YELLOW}📋 Adicione este valor no GitHub Secret: KEYSTORE_BASE64${NC}"
    echo ""
}

# Menu principal
echo "Escolha uma opção:"
echo "1) Criar novo keystore"
echo "2) Converter keystore existente para base64"
echo "3) Ambos (criar e converter)"
echo ""
read -p "Opção: " option

case $option in
    1)
        create_keystore
        ;;
    2)
        convert_to_base64
        ;;
    3)
        create_keystore
        convert_to_base64
        ;;
    *)
        echo -e "${RED}❌ Opção inválida${NC}"
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}✨ Próximos passos:${NC}"
echo "1. Configure os secrets no GitHub (veja DEPLOY.md)"
echo "2. Crie uma Service Account no Google Play Console"
echo "3. Faça push para a branch main ou crie uma tag"
echo ""
echo -e "${YELLOW}📖 Leia DEPLOY.md para instruções completas${NC}"
