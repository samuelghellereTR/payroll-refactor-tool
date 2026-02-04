package com.tr.refactor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Converte nomenclaturas PowerBuilder para padrões Java adequados.
 * 
 * Esta classe implementa as regras de conversão específicas para transformar
 * nomes de classes, métodos e variáveis do padrão PowerBuilder para Java.
 */
public class NameConverter {
    
    // Mapeamentos específicos para termos de folha de pagamento
    private static final Map<String, String> PAYROLL_TERMS = new HashMap<>();
    static {
        PAYROLL_TERMS.put("folha", "Payroll");
        PAYROLL_TERMS.put("calculo", "Calculation");
        PAYROLL_TERMS.put("salario", "Salary");
        PAYROLL_TERMS.put("desconto", "Discount");
        PAYROLL_TERMS.put("imposto", "Tax");
        PAYROLL_TERMS.put("inss", "Inss");
        PAYROLL_TERMS.put("fgts", "Fgts");
        PAYROLL_TERMS.put("irrf", "Irrf");
        PAYROLL_TERMS.put("empresa", "Company");
        PAYROLL_TERMS.put("empregado", "Employee");
        PAYROLL_TERMS.put("funcionario", "Employee");
        PAYROLL_TERMS.put("parametro", "Parameter");
        PAYROLL_TERMS.put("base", "Base");
        PAYROLL_TERMS.put("valor", "Value");
        PAYROLL_TERMS.put("taxa", "Rate");
        PAYROLL_TERMS.put("codigo", "Code");
        PAYROLL_TERMS.put("sistema", "System");
        PAYROLL_TERMS.put("config", "Config");
        PAYROLL_TERMS.put("dados", "Data");
        PAYROLL_TERMS.put("teste", "Test");
        PAYROLL_TERMS.put("executor", "Executor");
        PAYROLL_TERMS.put("memoria", "Memory");
        PAYROLL_TERMS.put("provisao", "Provision");
        PAYROLL_TERMS.put("encargo", "Charge");
    }
    
    // Mapeamentos de métodos comuns
    private static final Map<String, String> METHOD_MAPPINGS = new HashMap<>();
    static {
        METHOD_MAPPINGS.put("of_calc_payroll", "calculatePayroll");
        METHOD_MAPPINGS.put("of_execute_test", "executeTest");
        METHOD_MAPPINGS.put("of_inicializa", "initialize");
        METHOD_MAPPINGS.put("of_finaliza", "finalize");
        METHOD_MAPPINGS.put("of_conectar", "connect");
        METHOD_MAPPINGS.put("of_desconectar", "disconnect");
        METHOD_MAPPINGS.put("of_processar", "process");
        METHOD_MAPPINGS.put("of_validar", "validate");
        METHOD_MAPPINGS.put("of_calcular", "calculate");
    }
    
    private final PowerBuilderPatternMatcher patternMatcher;
    
    public NameConverter() {
        this.patternMatcher = new PowerBuilderPatternMatcher();
    }
    
    /**
     * Converte nome de classe PowerBuilder para padrão Java.
     * 
     * @param className Nome da classe original
     * @return Nome da classe convertido
     */
    public String convertClassName(String className) {
        if (className == null || className.isEmpty()) {
            return className;
        }
        
        // Remove prefixos PowerBuilder e converte para PascalCase
        String converted = className;
        
        // Remove prefixos conhecidos mas preserva o significado
        if (converted.startsWith("uo_")) {
            converted = converted.substring(3); // Remove "uo_"
        } else if (converted.startsWith("str_")) {
            converted = converted.substring(4); // Remove "str_"
        } else if (converted.startsWith("s_")) {
            converted = converted.substring(2); // Remove "s_"
        } else if (converted.startsWith("n_")) {
            converted = converted.substring(2); // Remove "n_"
        } else if (converted.startsWith("dfc_")) {
            converted = converted.substring(4); // Remove "dfc_"
        }
        
        // Converte underscores para CamelCase
        converted = underscoreToCamelCase(converted, true);
        
        // Traduz termos específicos de folha de pagamento
        converted = translatePayrollTerms(converted);
        
        return converted;
    }
    
    /**
     * Converte nome de método PowerBuilder para padrão Java.
     * 
     * @param methodName Nome do método original
     * @return Nome do método convertido
     */
    public String convertMethodName(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return methodName;
        }
        
        // Verifica mapeamentos diretos primeiro
        if (METHOD_MAPPINGS.containsKey(methodName)) {
            return METHOD_MAPPINGS.get(methodName);
        }
        
        String converted = methodName;
        
        // Trata getters PowerBuilder
        if (patternMatcher.isPowerBuilderGetter(methodName)) {
            converted = converted.substring(7); // Remove "of_get_"
            converted = "get" + underscoreToCamelCase(converted, true);
        }
        // Trata setters PowerBuilder
        else if (patternMatcher.isPowerBuilderSetter(methodName)) {
            converted = converted.substring(7); // Remove "of_set_"
            converted = "set" + underscoreToCamelCase(converted, true);
        }
        // Trata métodos boolean PowerBuilder
        else if (patternMatcher.isPowerBuilderBooleanMethod(methodName)) {
            converted = converted.substring(6); // Remove "of_is_"
            converted = "is" + underscoreToCamelCase(converted, true);
        }
        // Trata métodos gerais com prefixo "of_"
        else if (methodName.startsWith("of_")) {
            converted = converted.substring(3); // Remove "of_"
            converted = underscoreToCamelCase(converted, false);
        }
        
        // Traduz termos específicos de folha de pagamento
        converted = translatePayrollTerms(converted);
        
        return converted;
    }
    
    /**
     * Converte nome de variável PowerBuilder para padrão Java.
     * 
     * @param variableName Nome da variável original
     * @return Nome da variável convertido
     */
    public String convertVariableName(String variableName) {
        if (variableName == null || variableName.isEmpty()) {
            return variableName;
        }
        
        PowerBuilderPrefixType prefixType = patternMatcher.identifyPrefixType(variableName);
        String converted = variableName;
        
        if (prefixType != null) {
            // Remove o prefixo PowerBuilder
            converted = converted.substring(prefixType.getPrefix().length());
            
            // Para alguns prefixos, adiciona um nome mais descritivo
            switch (prefixType) {
                case GLOBAL_INTEGER:
                case GLOBAL_LONG:
                    if (!prefixType.getSuggestedReplacement().isEmpty()) {
                        converted = prefixType.getSuggestedReplacement() + 
                                   underscoreToCamelCase(converted, true);
                    }
                    break;
                case GLOBAL_DECIMAL:
                    if (!prefixType.getSuggestedReplacement().isEmpty() && 
                        !converted.toLowerCase().contains("valor") && 
                        !converted.toLowerCase().contains("taxa")) {
                        converted = prefixType.getSuggestedReplacement() + 
                                   underscoreToCamelCase(converted, true);
                    }
                    break;
                case ARGUMENT_OBJECT:
                case ARGUMENT_STRING:
                case ARGUMENT_DECIMAL:
                case ARGUMENT_BOOLEAN:
                case ARGUMENT_LONG:
                case ARGUMENT_INTEGER:
                    // Para argumentos, apenas remove o prefixo
                    break;
            }
        }
        
        // Converte underscores para camelCase
        converted = underscoreToCamelCase(converted, false);
        
        // Traduz termos específicos de folha de pagamento
        converted = translatePayrollTerms(converted);
        
        return converted;
    }
    
    /**
     * Converte string com underscores para CamelCase.
     * 
     * @param input String de entrada
     * @param pascalCase true para PascalCase, false para camelCase
     * @return String convertida
     */
    private String underscoreToCamelCase(String input, boolean pascalCase) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        StringBuilder result = new StringBuilder();
        String[] parts = input.split("_");
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].toLowerCase();
            
            if (i == 0 && !pascalCase) {
                // Primeira parte em minúscula para camelCase
                result.append(part);
            } else {
                // Capitaliza primeira letra das demais partes
                if (!part.isEmpty()) {
                    result.append(Character.toUpperCase(part.charAt(0)));
                    if (part.length() > 1) {
                        result.append(part.substring(1));
                    }
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * Traduz termos específicos de folha de pagamento.
     * 
     * @param input String de entrada
     * @return String com termos traduzidos
     */
    private String translatePayrollTerms(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String result = input;
        
        // Aplica traduções de termos específicos
        for (Map.Entry<String, String> entry : PAYROLL_TERMS.entrySet()) {
            String portuguese = entry.getKey();
            String english = entry.getValue();
            
            // Substitui termos completos (case insensitive)
            result = result.replaceAll("(?i)\\b" + portuguese + "\\b", english);
            
            // Substitui no início da string
            if (result.toLowerCase().startsWith(portuguese.toLowerCase())) {
                result = english + result.substring(portuguese.length());
            }
            
            // Substitui no final da string
            if (result.toLowerCase().endsWith(portuguese.toLowerCase())) {
                result = result.substring(0, result.length() - portuguese.length()) + english;
            }
        }
        
        return result;
    }
}