package com.tr.refactor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para o PowerBuilderPatternMatcher.
 */
public class PowerBuilderPatternMatcherTest {
    
    private PowerBuilderPatternMatcher matcher;
    
    @BeforeEach
    void setUp() {
        matcher = new PowerBuilderPatternMatcher();
    }
    
    @Test
    void shouldIdentifyPowerBuilderClassNames() {
        // Classes com nomenclatura PowerBuilder
        assertThat(matcher.isPowerBuilderClassName("a_folha_calculo")).isTrue();
        assertThat(matcher.isPowerBuilderClassName("uo_test_executor")).isTrue();
        assertThat(matcher.isPowerBuilderClassName("str_dados_calculo")).isTrue();
        assertThat(matcher.isPowerBuilderClassName("s_base")).isTrue();
        
        // Classes com nomenclatura Java padrão
        assertThat(matcher.isPowerBuilderClassName("PayrollCalculation")).isFalse();
        assertThat(matcher.isPowerBuilderClassName("TestExecutor")).isFalse();
    }
    
    @Test
    void shouldIdentifyPowerBuilderMethodNames() {
        // Métodos PowerBuilder
        assertThat(matcher.isPowerBuilderMethodName("of_calc_payroll")).isTrue();
        assertThat(matcher.isPowerBuilderMethodName("of_execute_test")).isTrue();
        assertThat(matcher.isPowerBuilderMethodName("of_get_valor")).isTrue();
        assertThat(matcher.isPowerBuilderMethodName("of_set_valor")).isTrue();
        
        // Métodos Java padrão
        assertThat(matcher.isPowerBuilderMethodName("calculatePayroll")).isFalse();
        assertThat(matcher.isPowerBuilderMethodName("getValor")).isFalse();
    }
    
    @Test
    void shouldIdentifyPowerBuilderVariableNames() {
        // Variáveis PowerBuilder
        assertThat(matcher.isPowerBuilderVariableName("giCodSis")).isTrue();
        assertThat(matcher.isPowerBuilderVariableName("glCodiEmp")).isTrue();
        assertThat(matcher.isPowerBuilderVariableName("gsCgcEmp")).isTrue();
        assertThat(matcher.isPowerBuilderVariableName("ao_arg_parser")).isTrue();
        assertThat(matcher.isPowerBuilderVariableName("as_memoria_calculo")).isTrue();
        
        // Variáveis Java padrão
        assertThat(matcher.isPowerBuilderVariableName("codigoSistema")).isFalse();
        assertThat(matcher.isPowerBuilderVariableName("argumentParser")).isFalse();
    }
    
    @Test
    void shouldIdentifyPrefixTypes() {
        assertThat(matcher.identifyPrefixType("giCodSis"))
            .isEqualTo(PowerBuilderPrefixType.GLOBAL_INTEGER);
        assertThat(matcher.identifyPrefixType("glCodiEmp"))
            .isEqualTo(PowerBuilderPrefixType.GLOBAL_LONG);
        assertThat(matcher.identifyPrefixType("gsCgcEmp"))
            .isEqualTo(PowerBuilderPrefixType.GLOBAL_STRING);
        assertThat(matcher.identifyPrefixType("ao_arg_parser"))
            .isEqualTo(PowerBuilderPrefixType.ARGUMENT_OBJECT);
    }
    
    @Test
    void shouldIdentifyGettersAndSetters() {
        assertThat(matcher.isPowerBuilderGetter("of_get_valor")).isTrue();
        assertThat(matcher.isPowerBuilderSetter("of_set_valor")).isTrue();
        assertThat(matcher.isPowerBuilderBooleanMethod("of_is_alterada")).isTrue();
        
        assertThat(matcher.isPowerBuilderGetter("getValor")).isFalse();
        assertThat(matcher.isPowerBuilderSetter("setValor")).isFalse();
    }
}