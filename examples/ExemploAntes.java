// EXEMPLO REAL - ANTES DA REFATORAÇÃO
// Este é o código problemático que você forneceu

public class ExemploAntes {
    
    public void exemploProblematico() {
        if (isTrue(getApplication().getGoFolFunc().getIuoDadosEventos().of_is_calcular_adicional_afastamentos(this.getIlEmpresaEventoCalc(), this.getIlEventoCalc()))){
            WebMapAtomicReference<Iuo_base> luoBaseRef2 = new WebMapAtomicReference<Iuo_base>(luoBase);
            if (isTrue(this.getIuoBasesCalculo().of_base_cad_base(((uo_bases_calculo) this.getIuoBasesCalculo()).HORA_EXTRA, luoBaseRef2))){
                luoBase = luoBaseRef2.get();
                ldcBase = setScale(ldcBase, minus(ldcBase, (luoBase.of_pega_base_afast_total())));
            }
            else {
                luoBase = luoBaseRef2.get();
            }
        }
    }
    
    // Outros exemplos problemáticos
    protected Short giCodSis = 0;
    protected Integer glCodiEmp = 0;
    protected String gsCgcEmp = "";
    protected BigDecimal gdcValor = createDecimal(BigDecimal.ZERO, 2);
    
    public Boolean of_calc_payroll(Iuo_argument_parser ao_arg_parser) {
        if (isTrue(not(this.of_is_valid()))) {
            return false;
        }
        
        BigDecimal valor = setScale(gdcValor, plus(gdcValor, createDecimal(100, 2)));
        return isTrue(this.of_process_calculation(ao_arg_parser));
    }
    
    public Boolean of_is_valid() {
        return isTrue(this.gsCgcEmp != null && !this.gsCgcEmp.isEmpty());
    }
}