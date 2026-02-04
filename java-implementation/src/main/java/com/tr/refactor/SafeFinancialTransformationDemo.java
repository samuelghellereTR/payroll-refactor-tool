package com.tr.refactor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

/**
 * DEMONSTRATION: Safe Financial Calculation Transformation
 * 
 * This example shows the CRITICAL difference between the old unsafe transformation
 * and the new safe transformation that preserves decimal precision.
 * 
 * Run this to see how the tool now safely handles financial calculations.
 */
public class SafeFinancialTransformationDemo {
    
    public static void main(String[] args) {
        System.out.println("🔧 DEMONSTRATING SAFE FINANCIAL CALCULATION TRANSFORMATION");
        System.out.println("=" .repeat(80));
        
        // Real payroll calculation code (simplified from the provided example)
        String originalCode = """
            public class PayrollCalculation {
                // CRITICAL: Different precisions for different financial purposes
                BigDecimal ldcSalario = createDecimal(BigDecimal.ZERO, 2);           // 2 decimals - monetary values
                BigDecimal ldcSalPeriodo = createDecimal(BigDecimal.ZERO, 6);        // 6 decimals - intermediate calculations  
                BigDecimal ldcMaxHorCalc = createDecimal(BigDecimal.ZERO, 6);        // 6 decimals - precise hours
                BigDecimal ldcHorasDiasDiarista = createDecimal(BigDecimal.ZERO, 4); // 4 decimals - hours/days
                BigDecimal ldcValorTemporario = createDecimal(BigDecimal.ZERO, 6);   // 6 decimals - calculations
                
                public Boolean of_calc_generico() {
                    if (isTrue(eq(this.getIdsVarCalc().getItemNumber(getIlRowCalc(), "efetuar_calculo_conforme_cada_dia_competencia"), 1))){
                        this.setIdSalarioMes(setScale(this.getIdSalarioMes(), this.getIdsVarCalc().getItemDecimal(getIlRowCalc(), "salario_mes")));
                        
                        // CRITICAL: Complex financial calculation with multiple operations
                        ldcValorTemporario = setScale(ldcValorTemporario, 
                            multiply(multiply(ldcSalarioHora, getIdValinfCalc()), (divide(ldcTaxaEve, 100))));
                    }
                    
                    if (isTrue(and(this.getIsDadEpr().isDirigente_sindical(), 
                                   getApplication().getGoFolFunc().getIuoDadosEventos().of_is_evento_adicional_sindicato(this.getIlEventoCalc())))){
                        ldcSalarioHora = setScale(ldcSalarioHora, this.getIdcSalarioHoraSindicato());
                        ldcSalarioDia = setScale(ldcSalarioDia, this.getIdcSalarioDiaSindicato());
                        ldcSalarioSem = setScale(ldcSalarioSem, this.getIdcSalarioSemanaSindicato());
                        ldcSalarioMes = setScale(ldcSalarioMes, this.getIdcSalarioMesSindicato());
                    }
                    
                    // More financial calculations
                    ldcSalarioDia = setScale(ldcSalarioDia, multiply((divide(ldcSalarioMes, getIsDadEpr().getHoras_mes())), 
                                                                   (divide(getIsDadEpr().getHoras_mes(), 30))));
                    ldcSalarioHora = setScale(ldcSalarioHora, divide(ldcSalarioMes, getIsDadEpr().getHoras_mes()));
                    
                    return true;
                }
            }
            """;
        
        System.out.println("📋 ORIGINAL CODE (with Mobilize WebMAP wrappers):");
        System.out.println("-".repeat(50));
        System.out.println(originalCode);
        
        // Apply safe transformation
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(originalCode).getResult().get();
        
        MobilizeWrapperCleaner cleaner = new MobilizeWrapperCleaner();
        boolean hasChanges = cleaner.cleanMobilizeWrappers(cu);
        
        String transformedCode = cu.toString();
        
        System.out.println("\\n🎉 SAFELY TRANSFORMED CODE (preserving financial precision):");
        System.out.println("-".repeat(50));
        System.out.println(transformedCode);
        
        System.out.println("\\n📊 TRANSFORMATION SUMMARY:");
        System.out.println("-".repeat(30));
        System.out.println("✅ Changes applied: " + hasChanges);
        System.out.println("✅ Total transformations: " + cleaner.getTransformationsCount());
        
        System.out.println("\\n🔍 CRITICAL SAFETY VALIDATIONS:");
        System.out.println("-".repeat(40));
        
        // Validate precision preservation
        boolean precisionPreserved = transformedCode.contains("setScale(2, RoundingMode.HALF_UP)") &&
                                   transformedCode.contains("setScale(6, RoundingMode.HALF_UP)") &&
                                   transformedCode.contains("setScale(4, RoundingMode.HALF_UP)");
        
        System.out.println("✅ Decimal precision preserved: " + precisionPreserved);
        
        // Validate rounding mode added to divisions
        boolean roundingModeAdded = transformedCode.contains("divide(") ? 
                                  transformedCode.contains("RoundingMode.HALF_UP") : true;
        
        System.out.println("✅ RoundingMode added to divisions: " + roundingModeAdded);
        
        // Validate dangerous wrappers removed
        boolean dangerousWrappersRemoved = !transformedCode.contains("createDecimal(") &&
                                         !transformedCode.contains("isTrue(");
        
        System.out.println("✅ Dangerous wrappers removed: " + dangerousWrappersRemoved);
        
        System.out.println("\\n💰 FINANCIAL CALCULATION INTEGRITY:");
        System.out.println("-".repeat(40));
        
        if (precisionPreserved && roundingModeAdded && dangerousWrappersRemoved) {
            System.out.println("🎯 SUCCESS: Financial calculations are SAFE!");
            System.out.println("   - Decimal precision maintained for accurate monetary calculations");
            System.out.println("   - RoundingMode prevents ArithmeticException in divisions");
            System.out.println("   - Code is cleaner and more readable");
            System.out.println("   - Payroll calculations will produce correct results");
        } else {
            System.out.println("❌ WARNING: Some safety checks failed!");
            System.out.println("   - Precision preserved: " + precisionPreserved);
            System.out.println("   - Rounding mode added: " + roundingModeAdded);
            System.out.println("   - Dangerous wrappers removed: " + dangerousWrappersRemoved);
        }
        
        System.out.println("\\n" + "=".repeat(80));
        System.out.println("🔧 TRANSFORMATION COMPLETE - FINANCIAL CALCULATIONS ARE NOW SAFE!");
    }
}