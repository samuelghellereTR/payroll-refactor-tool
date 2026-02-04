package com.tr.refactor;

import java.util.regex.Pattern;

/**
 * Identifica padrões específicos do código PowerBuilder migrado para Java.
 * 
 * Esta classe contém a lógica para reconhecer nomenclaturas e estruturas
 * típicas da migração automática PowerBuilder -> Java.
 */
public class PowerBuilderPatternMatcher {
    
    // Padrões de nomenclatura PowerBuilder
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("^[a-z][a-z0-9_]*$");
    private static final Pattern METHOD_PREFIX_PATTERN = Pattern.compile("^of_.*");
    private static final Pattern VARIABLE_PREFIX_PATTERN = Pattern.compile("^(gi|gl|gs|gdc|ao_|as_|adc_|ab_|al_|ai_).*");
    
    // Prefixos específicos do PowerBuilder
    private static final Pattern POWERBUILDER_PREFIXES = Pattern.compile("^(uo_|str_|s_|n_|dfc_|In_|Iuo_).*");
    
    /**
     * Verifica se o nome da classe segue padrões PowerBuilder.
     * 
     * @param className Nome da classe a ser verificado
     * @return true se for um nome de classe PowerBuilder
     */
    public boolean isPowerBuilderClassName(String className) {
        if (className == null || className.isEmpty()) {
            return false;
        }
        
        // Classes PowerBuilder migradas começam com minúscula ou têm underscores
        return CLASS_NAME_PATTERN.matcher(className).matches() || 
               POWERBUILDER_PREFIXES.matcher(className).matches();
    }
    
    /**
     * Verifica se o nome do método segue padrões PowerBuilder.
     * 
     * @param methodName Nome do método a ser verificado
     * @return true se for um nome de método PowerBuilder
     */
    public boolean isPowerBuilderMethodName(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return false;
        }
        
        // Métodos PowerBuilder começam com "of_"
        return METHOD_PREFIX_PATTERN.matcher(methodName).matches();
    }
    
    /**
     * Verifica se o nome da variável segue padrões PowerBuilder.
     * 
     * @param variableName Nome da variável a ser verificado
     * @return true se for um nome de variável PowerBuilder
     */
    public boolean isPowerBuilderVariableName(String variableName) {
        if (variableName == null || variableName.isEmpty()) {
            return false;
        }
        
        // Variáveis PowerBuilder têm prefixos específicos
        return VARIABLE_PREFIX_PATTERN.matcher(variableName).matches();
    }
    
    /**
     * Identifica o tipo de prefixo PowerBuilder.
     * 
     * @param name Nome a ser analisado
     * @return Tipo do prefixo ou null se não for PowerBuilder
     */
    public PowerBuilderPrefixType identifyPrefixType(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        // Prefixos de variáveis globais
        if (name.startsWith("gi")) return PowerBuilderPrefixType.GLOBAL_INTEGER;
        if (name.startsWith("gl")) return PowerBuilderPrefixType.GLOBAL_LONG;
        if (name.startsWith("gs")) return PowerBuilderPrefixType.GLOBAL_STRING;
        if (name.startsWith("gdc")) return PowerBuilderPrefixType.GLOBAL_DECIMAL;
        
        // Prefixos de argumentos
        if (name.startsWith("ao_")) return PowerBuilderPrefixType.ARGUMENT_OBJECT;
        if (name.startsWith("as_")) return PowerBuilderPrefixType.ARGUMENT_STRING;
        if (name.startsWith("adc_")) return PowerBuilderPrefixType.ARGUMENT_DECIMAL;
        if (name.startsWith("ab_")) return PowerBuilderPrefixType.ARGUMENT_BOOLEAN;
        if (name.startsWith("al_")) return PowerBuilderPrefixType.ARGUMENT_LONG;
        if (name.startsWith("ai_")) return PowerBuilderPrefixType.ARGUMENT_INTEGER;
        
        // Prefixos de classes/objetos
        if (name.startsWith("uo_")) return PowerBuilderPrefixType.USER_OBJECT;
        if (name.startsWith("str_")) return PowerBuilderPrefixType.STRUCTURE;
        if (name.startsWith("s_")) return PowerBuilderPrefixType.STRUCTURE_SHORT;
        if (name.startsWith("n_")) return PowerBuilderPrefixType.NON_VISUAL_OBJECT;
        if (name.startsWith("dfc_")) return PowerBuilderPrefixType.DATAWINDOW_FUNCTION;
        if (name.startsWith("In_")) return PowerBuilderPrefixType.INTERFACE;
        if (name.startsWith("Iuo_")) return PowerBuilderPrefixType.INTERFACE_USER_OBJECT;
        
        return null;
    }
    
    /**
     * Verifica se um nome contém underscores (padrão PowerBuilder).
     * 
     * @param name Nome a ser verificado
     * @return true se contém underscores
     */
    public boolean hasUnderscores(String name) {
        return name != null && name.contains("_");
    }
    
    /**
     * Verifica se é um método getter PowerBuilder.
     * 
     * @param methodName Nome do método
     * @return true se for um getter PowerBuilder
     */
    public boolean isPowerBuilderGetter(String methodName) {
        return methodName != null && methodName.startsWith("of_get_");
    }
    
    /**
     * Verifica se é um método setter PowerBuilder.
     * 
     * @param methodName Nome do método
     * @return true se for um setter PowerBuilder
     */
    public boolean isPowerBuilderSetter(String methodName) {
        return methodName != null && methodName.startsWith("of_set_");
    }
    
    /**
     * Verifica se é um método boolean PowerBuilder.
     * 
     * @param methodName Nome do método
     * @return true se for um método boolean PowerBuilder
     */
    public boolean isPowerBuilderBooleanMethod(String methodName) {
        return methodName != null && methodName.startsWith("of_is_");
    }
}

/**
 * Enumeration dos tipos de prefixos PowerBuilder identificados.
 */
enum PowerBuilderPrefixType {
    // Variáveis globais
    GLOBAL_INTEGER("gi", "codigo", "id"),
    GLOBAL_LONG("gl", "codigo", "id"),
    GLOBAL_STRING("gs", "", ""),
    GLOBAL_DECIMAL("gdc", "valor", "taxa"),
    
    // Argumentos
    ARGUMENT_OBJECT("ao_", "", ""),
    ARGUMENT_STRING("as_", "", ""),
    ARGUMENT_DECIMAL("adc_", "valor", ""),
    ARGUMENT_BOOLEAN("ab_", "", ""),
    ARGUMENT_LONG("al_", "codigo", "id"),
    ARGUMENT_INTEGER("ai_", "codigo", "id"),
    
    // Classes/Objetos
    USER_OBJECT("uo_", "", ""),
    STRUCTURE("str_", "", ""),
    STRUCTURE_SHORT("s_", "", ""),
    NON_VISUAL_OBJECT("n_", "", ""),
    DATAWINDOW_FUNCTION("dfc_", "", ""),
    INTERFACE("In_", "", ""),
    INTERFACE_USER_OBJECT("Iuo_", "", "");
    
    private final String prefix;
    private final String suggestedReplacement;
    private final String alternativeReplacement;
    
    PowerBuilderPrefixType(String prefix, String suggestedReplacement, String alternativeReplacement) {
        this.prefix = prefix;
        this.suggestedReplacement = suggestedReplacement;
        this.alternativeReplacement = alternativeReplacement;
    }
    
    public String getPrefix() { return prefix; }
    public String getSuggestedReplacement() { return suggestedReplacement; }
    public String getAlternativeReplacement() { return alternativeReplacement; }
}