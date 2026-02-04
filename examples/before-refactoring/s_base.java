package com.dominio.a_folha_calculo.calculo;

import com.mobilize.jwebmap.models.Structure;
import static com.mobilize.jwebmap.datatypes.BigDecimalHelper.createDecimal;
import java.math.BigDecimal;

/**
 * EXEMPLO DE CÓDIGO ANTES DA REFATORAÇÃO
 * 
 * Este é um exemplo típico do código PowerBuilder migrado automaticamente
 * que seria encontrado no projeto real.
 */
public class s_base extends Structure {
    
    // Variáveis com nomenclatura PowerBuilder
    protected BigDecimal base = createDecimal(BigDecimal.ZERO, 2);
    protected BigDecimal taxa = createDecimal(BigDecimal.ZERO, 4);
    protected BigDecimal valor = createDecimal(BigDecimal.ZERO, 2);
    protected String gsCgcEmp = "";
    protected Integer glCodiEmp = 0;
    protected Short giCodSis = 0;
    
    // Métodos com nomenclatura PowerBuilder
    public BigDecimal of_get_valor() {
        return this.valor;
    }
    
    public void of_set_valor(BigDecimal adc_valor) {
        this.valor = adc_valor;
    }
    
    public Boolean of_is_alterada() {
        return this.valor.compareTo(BigDecimal.ZERO) != 0;
    }
    
    public void of_calc_base(BigDecimal adc_salario, BigDecimal adc_taxa) {
        this.base = adc_salario;
        this.taxa = adc_taxa;
        this.valor = this.base.multiply(this.taxa);
    }
    
    public String of_get_cgc_empresa() {
        return this.gsCgcEmp;
    }
    
    public void of_set_cgc_empresa(String as_cgc) {
        this.gsCgcEmp = as_cgc;
    }
}