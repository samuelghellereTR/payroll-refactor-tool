# 🔧 Payroll Refactor Tool

Ferramenta automática para refatoração de código PowerBuilder migrado para Java, melhorando drasticamente a legibilidade sem alterar a funcionalidade.

## 📋 Visão Geral

Esta ferramenta foi desenvolvida especificamente para refatorar código Java gerado pela migração automática de PowerBuilder (usando Mobilize WebMAP), transformando nomenclaturas crípticas em padrões Java legíveis.

### ✨ Principais Funcionalidades

- **🏷️ Conversão de Nomenclatura**: Transforma nomes PowerBuilder em padrões Java
- **🔄 Preservação Total da Lógica**: Zero mudanças na funcionalidade
- **📊 Relatórios Detalhados**: Estatísticas completas das transformações
- **🛡️ Backup Automático**: Preserva arquivos originais
- **🧪 Modo Dry-Run**: Simula transformações sem alterar arquivos

## 🎯 Problemas Resolvidos

### Antes da Refatoração
```java
public class a_folha_calculo extends ApplicationModelImpl {
    protected Short giCodSis = 0;
    protected String gsCgcEmp = "";
    
    public Boolean of_calc_payroll(Iuo_argument_parser ao_arg_parser) {
        // Lógica complexa...
    }
    
    public BigDecimal of_get_valor() {
        return this.valor;
    }
}
```

### Após a Refatoração
```java
public class AFolhaCalculation extends ApplicationModelImpl {
    private Short codigoSystem = 0;
    private String cgcCompany = "";
    
    public Boolean calculatePayroll(Iuo_argument_parser argumentParser) {
        // Mesma lógica, nomes legíveis...
    }
    
    public BigDecimal getValue() {
        return this.value;
    }
}
```

## 🚀 Implementações Disponíveis

### 🔵 Java (Recomendada)
- **Tecnologia**: JavaParser + AST manipulation
- **Precisão**: Máxima (entende sintaxe Java nativamente)
- **Validação**: Automática (compila código refatorado)
- **Integração**: Gradle plugin

### 🐍 Python (Prototipagem Rápida)
- **Tecnologia**: Regex + text processing
- **Velocidade**: Desenvolvimento mais rápido
- **Flexibilidade**: Fácil customização de regras
- **Simplicidade**: Menos dependências

## 📦 Instalação e Uso

### Java Implementation

#### Pré-requisitos
- Java 11+
- Gradle 7+

#### Compilação
```bash
cd java-implementation
./gradlew build
```

#### Uso
```bash
# Execução básica
./gradlew run --args="src/main/java/com/dominio"

# Com opções avançadas
./gradlew run --args="src/main/java/com/dominio -o output/ -v --backup"

# Modo dry-run (simula sem alterar)
./gradlew run --args="src/main/java/com/dominio --dry-run -v"
```

### Python Implementation

#### Pré-requisitos
- Python 3.8+

#### Uso
```bash
cd python-implementation

# Execução básica
python payroll_refactor.py src/main/java/com/dominio

# Com opções avançadas
python payroll_refactor.py src/main/java/com/dominio -o output/ -v

# Modo dry-run
python payroll_refactor.py src/main/java/com/dominio --dry-run -v
```

## 🎛️ Opções de Linha de Comando

| Opção | Descrição | Padrão |
|-------|-----------|--------|
| `input_dir` | Diretório com código Java | Obrigatório |
| `-o, --output` | Diretório de saída | Mesmo diretório |
| `-d, --dry-run` | Simula sem alterar arquivos | false |
| `-v, --verbose` | Saída detalhada | false |
| `--backup` | Cria backup (.backup) | true |
| `--preserve-comments` | Preserva comentários | true |

## 🔍 Transformações Aplicadas

### 1. Nomenclatura de Classes
```java
// ANTES → DEPOIS
a_folha_calculo.java → AFolhaCalculation.java
uo_test_executor.java → UoTestExecutor.java
str_dados_calculo.java → StrDadosCalculation.java
s_base.java → SBase.java
```

### 2. Nomenclatura de Métodos
```java
// ANTES → DEPOIS
of_calc_payroll() → calculatePayroll()
of_execute_test() → executeTest()
of_get_valor() → getValue()
of_set_valor() → setValue()
of_is_alterada() → isAlterada()
```

### 3. Nomenclatura de Variáveis
```java
// ANTES → DEPOIS
giCodSis → codigoSystem
glCodiEmp → codigoCompany
gsCgcEmp → cgcCompany
gdcValor → valorValue
ao_arg_parser → argumentParser
as_memoria_calculo → memoriaCalculation
```

### 4. Tradução de Termos
```java
// ANTES → DEPOIS
folha → Payroll
calculo → Calculation
salario → Salary
empresa → Company
funcionario → Employee
parametro → Parameter
```

## 📊 Exemplo de Saída

```
🔧 Payroll Refactor Tool v1.0.0
📁 Analisando: src/main/java/com/dominio
🔍 Processando: a_folha_calculo.java
  📝 Classe: a_folha_calculo → AFolhaCalculation
  🔧 Método: of_calc_payroll → calculatePayroll
  🏷️  Campo: giCodSis → codigoSystem
  ✅ Salvo: a_folha_calculo.java

✅ Refatoração concluída!
📊 Arquivos processados: 45
🔄 Transformações aplicadas: 312
⚠️  Avisos: 0
```

## 🧪 Testes

### Java
```bash
cd java-implementation
./gradlew test
```

### Python
```bash
cd python-implementation
python -m pytest tests/ -v
```

## 📈 Comparação de Tecnologias

| Aspecto | Java (JavaParser) | Python (Regex) |
|---------|-------------------|----------------|
| **Precisão** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Velocidade de Desenvolvimento** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Validação Automática** | ✅ | ❌ |
| **Integração com Build** | ✅ | ⭐⭐⭐ |
| **Manutenibilidade** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Curva de Aprendizado** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

## 🎯 Recomendação

### ✅ Use a Implementação Java Se:
- Precisar de **máxima precisão**
- Quiser **validação automática**
- Planeja **integrar com build**
- Tem **equipe Java experiente**

### ✅ Use a Implementação Python Se:
- Precisa de **prototipagem rápida**
- Quer **customização fácil**
- Tem **regras específicas** adicionais
- Prefere **simplicidade**

## 🛠️ Desenvolvimento

### Estrutura do Projeto
```
payroll-refactor-tool/
├── java-implementation/          # Implementação Java com JavaParser
│   ├── src/main/java/           # Código principal
│   ├── src/test/java/           # Testes unitários
│   └── build.gradle             # Build configuration
├── python-implementation/        # Implementação Python
│   ├── payroll_refactor.py      # Script principal
│   └── tests/                   # Testes
└── examples/                    # Exemplos antes/depois
    ├── before-refactoring/
    └── after-refactoring/
```

### Contribuindo
1. Fork do repositório
2. Crie uma branch para sua feature
3. Adicione testes para novas funcionalidades
4. Execute os testes existentes
5. Submeta um Pull Request

## 📝 Licença

MIT License - veja [LICENSE](LICENSE) para detalhes.

## 🤝 Suporte

Para dúvidas ou problemas:
1. Abra uma [Issue](https://github.com/samuelghellereTR/payroll-refactor-tool/issues)
2. Consulte a [documentação](https://github.com/samuelghellereTR/payroll-refactor-tool/wiki)
3. Entre em contato com a equipe de desenvolvimento

---

**⚡ Transforme seu código PowerBuilder legado em Java legível em minutos, não meses!**