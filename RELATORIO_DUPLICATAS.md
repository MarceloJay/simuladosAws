# Relatório de Análise e Correção de Questões Duplicadas

## Data: 7 de março de 2026

## Resumo Executivo

Foi realizada uma análise completa de todos os 10 simulados do aplicativo AWS Quiz para identificar questões duplicadas. O resultado mostrou que **27 questões estavam duplicadas** entre o Simulado 4 e o Simulado 6.

## Análise Detalhada

### Estado Inicial
- **Total de simulados**: 10
- **Total de questões**: 650 (10 × 65)
- **Questões duplicadas encontradas**: 27
- **Simulados afetados**: Simulado 4 e Simulado 6

### Questões Duplicadas
As questões duplicadas eram as questões de número **#37 a #65** (29 questões no total, mas 2 não eram duplicatas exatas):
- Simulado 4: questões #37-65
- Simulado 6: questões #37-65

### Ação Tomada
Foi executado o script `fix_duplicates.py` que:
1. Criou um backup do arquivo original: `simulado_6.json.backup`
2. Removeu as 27 questões duplicadas do `simulado_6.json`
3. Manteve apenas as 38 questões únicas do Simulado 6

### Estado Atual
- **Total de questões**: 623 (redução de 27 questões)
- **Questões duplicadas**: 0 ✅
- **Simulado 6**: agora possui apenas 38 questões únicas

## Distribuição Atual de Questões

| Simulado | Questões |
|----------|----------|
| Simulado 1 | 65 |
| Simulado 2 | 65 |
| Simulado 3 | 65 |
| Simulado 4 | 65 |
| Simulado 5 | 65 |
| **Simulado 6** | **38** ⚠️ |
| Simulado 7 | 65 |
| Simulado 8 | 65 |
| Simulado 9 | 65 |
| Simulado 10 | 65 |

## Próximos Passos Necessários

### Opção 1: Adicionar 27 Novas Questões ao Simulado 6
Para manter o padrão de 65 questões por simulado, será necessário:
- Criar 27 novas questões únicas sobre AWS Cloud Practitioner
- Adicionar essas questões ao `simulado_6.json`
- Validar que não há duplicatas com outros simulados

### Opção 2: Aceitar Simulado 6 com 38 Questões
Manter o Simulado 6 com apenas 38 questões:
- Usuários terão um simulado mais curto
- Tempo de execução será menor (aproximadamente 52 minutos ao invés de 90)
- Pode ser considerado um "simulado rápido"

### Opção 3: Redistribuir Questões
Redistribuir questões de outros simulados para equilibrar:
- Mover questões de simulados com 65 para o Simulado 6
- Garantir que não haja duplicatas

## Recomendação

**Recomendo a Opção 1**: Adicionar 27 novas questões ao Simulado 6 para manter a consistência do aplicativo. Todos os simulados devem ter 65 questões para proporcionar uma experiência uniforme aos usuários.

## Arquivos Criados

1. `check_duplicates.py` - Script original de verificação
2. `check_all_duplicates.py` - Script aprimorado com análise por pares
3. `fix_duplicates.py` - Script de correção automática
4. `simulado_6.json.backup` - Backup do arquivo original
5. `RELATORIO_DUPLICATAS.md` - Este relatório

## Validação Final

✅ Nenhuma questão duplicada encontrada entre os 10 simulados
✅ Backup do arquivo original criado
✅ Simulado 6 atualizado com questões únicas
⚠️ Simulado 6 precisa de 27 questões adicionais para completar 65 questões

---

**Nota**: O aplicativo atualmente carrega todas as questões de cada arquivo JSON e as embaralha. Com 38 questões, o Simulado 6 funcionará normalmente, mas os usuários verão apenas 38 questões ao invés de 65.
