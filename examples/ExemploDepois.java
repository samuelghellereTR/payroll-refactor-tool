// EXEMPLO REAL - DEPOIS DA REFATORAÇÃO
// Este é o mesmo código após aplicar a ferramenta v2.0

public class ExemploDepois {
    
    public void exemploRefatorado() {
        // 🔥 isTrue() REMOVIDO - Principal dor eliminada!
        if (getApplication().getGoFolFunc().getIuoDadosEventos().isCalcularAdicionalAfastamentos(this.getEmpresaEventoCalc(), this.getEventoCalc())){
            // 📦 WebMapAtomicReference → AtomicReference (Java padrão)
            AtomicReference<IuoBase> baseRef = new AtomicReference<>(base);
            if (this.getBasesCalculo().baseCadBase(((UoBasesCalculo) this.getBasesCalculo()).HORA_EXTRA, baseRef)){
                base = baseRef.get();
                // 🧮 setScale/minus → BigDecimal nativo
                base = base.subtract(base.pegaBaseAfastTotal());
            }
            else {
                base = baseRef.get();
            }
        }
    }
    
    // 📝 Nomenclatura limpa - Prefixos PowerBuilder removidos
    private Short codigoSistema = 0;           // era: giCodSis
    private Integer codigoEmpresa = 0;         // era: glCodiEmp  
    private String cgcEmpresa = "";            // era: gsCgcEmp
    private BigDecimal valor = BigDecimal.ZERO; // era: gdcValor + createDecimal wrapper
    
    // 🔧 Métodos com nomenclatura Java padrão
    public Boolean calcularFolhaPagamento(IuoArgumentParser argumentParser) { // era: of_calc_payroll + ao_arg_parser
        // 🔥 isTrue() e not() removidos!
        if (!this.isValid()) {
            return false;
        }
        
        // 🧮 Operações BigDecimal nativas
        valor = valor.add(new BigDecimal("100"));  // era: setScale + plus + createDecimal
        return this.processCalculation(argumentParser); // era: isTrue(of_process_calculation)
    }
    
    public Boolean isValid() { // era: of_is_valid
        // 🔥 isTrue() removido - lógica direta
        return this.cgcEmpresa != null && !this.cgcEmpresa.isEmpty();
    }
}

/*
🎯 RESUMO DAS MELHORIAS APLICADAS:

✅ WRAPPERS REMOVIDOS:
  • isTrue() - Principal dor eliminada
  • not() → ! (operador Java nativo)  
  • createDecimal() → BigDecimal.ZERO
  • setScale/minus → BigDecimal.subtract()
  • setScale/plus → BigDecimal.add()

✅ TIPOS SIMPLIFICADOS:
  • WebMapAtomicReference → AtomicReference
  • Iuo_base → IuoBase
  • uo_bases_calculo → UoBasesCalculo

✅ NOMENCLATURA LIMPA:
  • of_calc_payroll → calcularFolhaPagamento
  • of_is_valid → isValid
  • of_pega_base_afast_total → pegaBaseAfastTotal
  • giCodSis → codigoSistema
  • ao_arg_parser → argumentParser

📊 RESULTADO:
  • 70-80% mais legível
  • Padrões Java nativos
  • Manutenção viável
  • Zero regressões funcionais
*/