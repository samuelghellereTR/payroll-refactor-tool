package com.tr.refactor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para o NameConverter.
 */
public class NameConverterTest {
    
    private NameConverter converter;
    
    @BeforeEach
    void setUp() {
        converter = new NameConverter();
    }
    
    @Test
    void shouldConvertClassNames() {
        // Conversões de nomes de classes
        assertThat(converter.convertClassName("a_folha_calculo"))
            .isEqualTo("APayrollCalculation");
        assertThat(converter.convertClassName("uo_test_executor"))
            .isEqualTo("TestExecutor");
        assertThat(converter.convertClassName("str_dados_calculo"))
            .isEqualTo("DataCalculation");
        assertThat(converter.convertClassName("s_base"))
            .isEqualTo("SBase");
    }
    
    @Test
    void shouldConvertMethodNames() {
        // Conversões de métodos
        assertThat(converter.convertMethodName("of_calc_payroll"))
            .isEqualTo("calculatePayroll");
        assertThat(converter.convertMethodName("of_execute_test"))
            .isEqualTo("executeTest");
        assertThat(converter.convertMethodName("of_get_valor"))
            .isEqualTo("getValue");
        assertThat(converter.convertMethodName("of_set_valor"))
            .isEqualTo("setValue");
        assertThat(converter.convertMethodName("of_is_alterada"))
            .isEqualTo("isAlterada");
    }
    
    @Test
    void shouldConvertVariableNames() {
        // Conversões de variáveis
        assertThat(converter.convertVariableName("giCodSis"))
            .isEqualTo("codigoSis");
        assertThat(converter.convertVariableName("glCodiEmp"))
            .isEqualTo("codigoEmp");
        assertThat(converter.convertVariableName("gsCgcEmp"))
            .isEqualTo("cgcEmp");
        assertThat(converter.convertVariableName("gdcValor"))
            .isEqualTo("valorValor");
        assertThat(converter.convertVariableName("ao_arg_parser"))
            .isEqualTo("argParser");
        assertThat(converter.convertVariableName("as_memoria_calculo_base"))
            .isEqualTo("memoriaCalculoBase");
    }
    
    @Test
    void shouldTranslatePayrollTerms() {
        // Traduções de termos específicos
        assertThat(converter.convertClassName("a_folha_pagamento"))
            .isEqualTo("APayrollPayment");
        assertThat(converter.convertMethodName("of_calcular_salario"))
            .isEqualTo("calculateSalary");
        assertThat(converter.convertVariableName("gs_nome_empresa"))
            .isEqualTo("nomeCompany");
    }
    
    @Test
    void shouldPreserveJavaConventions() {
        // Não deve alterar nomes já em padrão Java
        assertThat(converter.convertClassName("PayrollCalculation"))
            .isEqualTo("PayrollCalculation");
        assertThat(converter.convertMethodName("calculatePayroll"))
            .isEqualTo("calculatePayroll");
        assertThat(converter.convertVariableName("argumentParser"))
            .isEqualTo("argumentParser");
    }
    
    @Test
    void shouldHandleEdgeCases() {
        // Casos extremos
        assertThat(converter.convertClassName("")).isEmpty();
        assertThat(converter.convertClassName(null)).isNull();
        assertThat(converter.convertMethodName("of_")).isEqualTo("");
        assertThat(converter.convertVariableName("gi")).isEqualTo("codigo");
    }
}