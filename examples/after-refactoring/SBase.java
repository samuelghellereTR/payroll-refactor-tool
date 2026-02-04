package com.dominio.a_folha_calculo.calculo;

import com.mobilize.jwebmap.models.Structure;
import static com.mobilize.jwebmap.datatypes.BigDecimalHelper.createDecimal;
import java.math.BigDecimal;

/**
 * EXEMPLO DE CÓDIGO APÓS A REFATORAÇÃO AUTOMÁTICA
 * 
 * Este é o mesmo código após a aplicação da ferramenta de refatoração.
 * Note como a legibilidade melhorou drasticamente mantendo a funcionalidade.
 */
public class SBase extends Structure {
    
    // Variáveis com nomenclatura Java padrão
    private BigDecimal base = BigDecimal.ZERO;
    private BigDecimal rate = BigDecimal.ZERO;
    private BigDecimal value = BigDecimal.ZERO;
    private String cgcCompany = "";
    private Integer codigoCompany = 0;
    private Short codigoSystem = 0;
    
    // Métodos com nomenclatura Java padrão
    public BigDecimal getValue() {
        return this.value;
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    public Boolean isAlterada() {
        return this.value.compareTo(BigDecimal.ZERO) != 0;
    }
    
    public void calculateBase(BigDecimal salary, BigDecimal rate) {
        this.base = salary;
        this.rate = rate;
        this.value = this.base.multiply(this.rate);
    }
    
    public String getCgcCompany() {
        return this.cgcCompany;
    }
    
    public void setCgcCompany(String cgc) {
        this.cgcCompany = cgc;
    }
}