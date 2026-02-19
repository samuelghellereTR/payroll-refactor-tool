package com.tr.refactor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * COMPREHENSIVE TESTS for Safe MobilizeWrapperCleaner
 * 
 * Tests the CRITICAL FIX that preserves decimal precision in financial calculations.
 * Ensures the tool no longer breaks payroll calculations by removing precision.
 */
class MobilizeWrapperCleanerTest {
    
    private MobilizeWrapperCleaner cleaner;
    private JavaParser parser;
    
    @BeforeEach
    void setUp() {
        cleaner = new MobilizeWrapperCleaner();
        parser = new JavaParser();
    }
    
    @Test
    void testSafeCreateDecimalReplacement() {
        String code = """
            public class PayrollCalculation {
                // CRITICAL: Different precisions for different financial purposes
                BigDecimal ldcSalario = createDecimal(BigDecimal.ZERO, 2);           // 2 decimals - monetary values
                BigDecimal ldcSalPeriodo = createDecimal(BigDecimal.ZERO, 6);        // 6 decimals - intermediate calculations  
                BigDecimal ldcMaxHorCalc = createDecimal(BigDecimal.ZERO, 6);        // 6 decimals - precise hours
                BigDecimal ldcHorasDiasDiarista = createDecimal(BigDecimal.ZERO, 4); // 4 decimals - hours/days
                
                public void calculate() {
                    BigDecimal temp = createDecimal(new BigDecimal("1000.00"), 2);
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        boolean hasChanges = cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(hasChanges, "Should have detected changes");
        assertEquals(5, cleaner.getTransformationsCount(), "Should have safely replaced 5 createDecimal() calls");
        
        String refactoredCode = cu.toString();
        
        // ✅ VERIFY: createDecimal() removed but precision preserved
        assertFalse(refactoredCode.contains("createDecimal("), "createDecimal() should be removed");
        
        // ✅ VERIFY: Precision preserved with setScale()
        assertTrue(refactoredCode.contains("BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)"), 
                  "2-decimal precision should be preserved");
        assertTrue(refactoredCode.contains("BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP)"), 
                  "6-decimal precision should be preserved");
        assertTrue(refactoredCode.contains("BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP)"), 
                  "4-decimal precision should be preserved");
        
        // ✅ VERIFY: RoundingMode added for safety
        assertTrue(refactoredCode.contains("RoundingMode.HALF_UP"), 
                  "RoundingMode.HALF_UP should be added");
        
        System.out.println("✅ SAFE TRANSFORMATION RESULT:");
        System.out.println(refactoredCode);
    }
    
    @Test
    void testSafeMathOperationsWithRounding() {
        String code = """
            public class FinancialMath {
                public void calculate() {
                    BigDecimal salary = new BigDecimal("3000.00");
                    BigDecimal hours = new BigDecimal("220");
                    
                    // CRITICAL: Division must have RoundingMode to prevent ArithmeticException
                    BigDecimal hourlyRate = divide(salary, hours);
                    BigDecimal bonus = multiply(salary, new BigDecimal("0.10"));
                    BigDecimal deduction = minus(salary, new BigDecimal("100.00"));
                    BigDecimal total = plus(salary, bonus);
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        boolean hasChanges = cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(hasChanges, "Should have detected math operations");
        assertTrue(cleaner.getTransformationsCount() >= 4, "Should have replaced math operations");
        
        String refactoredCode = cu.toString();
        
        // ✅ VERIFY: Math operations converted to BigDecimal methods
        assertFalse(refactoredCode.contains("divide(salary, hours)"), "divide() wrapper should be removed");
        assertFalse(refactoredCode.contains("multiply(salary,"), "multiply() wrapper should be removed");
        assertFalse(refactoredCode.contains("minus(salary,"), "minus() wrapper should be removed");
        assertFalse(refactoredCode.contains("plus(salary,"), "plus() wrapper should be removed");
        
        // ✅ VERIFY: Native BigDecimal methods with proper rounding
        assertTrue(refactoredCode.contains("salary.divide(hours, RoundingMode.HALF_UP)"), 
                  "Division should include RoundingMode");
        assertTrue(refactoredCode.contains("salary.multiply("), "multiply() should be converted");
        assertTrue(refactoredCode.contains("salary.subtract("), "subtract() should be converted");
        assertTrue(refactoredCode.contains("salary.add("), "add() should be converted");
        
        System.out.println("✅ SAFE MATH OPERATIONS:");
        System.out.println(refactoredCode);
    }
    
    @Test
    void testRealPayrollCalculationExample() {
        // Real example from the provided payroll calculation code
        String code = """
            public class PayrollCalculation {
                BigDecimal ldcSalario = createDecimal(BigDecimal.ZERO, 2);
                BigDecimal ldcSalPeriodo = createDecimal(BigDecimal.ZERO, 6);
                BigDecimal ldcValorTemporario = createDecimal(BigDecimal.ZERO, 6);
                
                public Boolean of_calc_generico() {
                    if (isTrue(eq(this.getIdsVarCalc().getItemNumber(getIlRowCalc(), "efetuar_calculo_conforme_cada_dia_competencia"), 1))){
                        this.setIdSalarioMes(setScale(this.getIdSalarioMes(), this.getIdsVarCalc().getItemDecimal(getIlRowCalc(), "salario_mes")));
                        ldcValorTemporario = setScale(ldcValorTemporario, multiply(multiply(ldcSalarioHora, getIdValinfCalc()), (divide(ldcTaxaEve, 100))));
                    }
                    
                    if (isTrue(and(this.getIsDadEpr().isDirigente_sindical(), getApplication().getGoFolFunc().getIuoDadosEventos().of_is_evento_adicional_sindicato(this.getIlEventoCalc())))){
                        ldcSalarioHora = setScale(ldcSalarioHora, this.getIdcSalarioHoraSindicato());
                    }
                    
                    return true;
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        boolean hasChanges = cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(hasChanges, "Should have detected changes in real payroll code");
        assertTrue(cleaner.getTransformationsCount() >= 5, "Should have applied multiple transformations");
        
        String refactoredCode = cu.toString();
        
        // ✅ VERIFY: Critical financial precision preserved
        assertTrue(refactoredCode.contains("BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)"), 
                  "Salary precision (2 decimals) should be preserved");
        assertTrue(refactoredCode.contains("BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP)"), 
                  "Calculation precision (6 decimals) should be preserved");
        
        // ✅ VERIFY: Boolean wrappers removed
        assertFalse(refactoredCode.contains("isTrue("), "isTrue() should be removed");
        
        // ✅ VERIFY: Equality operations converted
        assertTrue(refactoredCode.contains(".equals("), "eq() should be converted to equals()");
        
        // ✅ VERIFY: No unsafe createDecimal() calls remain
        assertFalse(refactoredCode.contains("createDecimal("), "createDecimal() should be completely removed");
        
        System.out.println("🎉 REAL PAYROLL CALCULATION - SAFELY REFACTORED:");
        System.out.println(refactoredCode);
    }
    
    @Test
    void testFinancialPrecisionValidation() {
        // Test that demonstrates the CRITICAL difference between safe and unsafe transformation
        String code = """
            public class PrecisionTest {
                public BigDecimal calculateSalaryPerHour() {
                    BigDecimal monthlyPay = createDecimal(new BigDecimal("3000.00"), 2);
                    BigDecimal hoursPerMonth = new BigDecimal("220");
                    
                    // CRITICAL: This division MUST have proper rounding to avoid ArithmeticException
                    return divide(monthlyPay, hoursPerMonth);
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        cleaner.cleanMobilizeWrappers(cu);
        
        String refactoredCode = cu.toString();
        
        // ✅ VERIFY: Safe transformation that prevents ArithmeticException
        assertTrue(refactoredCode.contains("new BigDecimal(\"3000.00\").setScale(2, RoundingMode.HALF_UP)"), 
                  "Monthly pay precision should be preserved");
        assertTrue(refactoredCode.contains("monthlyPay.divide(hoursPerMonth, RoundingMode.HALF_UP)"), 
                  "Division should include RoundingMode to prevent ArithmeticException");
        
        // ✅ VERIFY: No unsafe operations remain
        assertFalse(refactoredCode.contains("createDecimal("), "createDecimal() should be removed");
        assertFalse(refactoredCode.contains("divide(monthlyPay, hoursPerMonth)"), "Unsafe divide() should be removed");
        
        System.out.println("💰 FINANCIAL PRECISION PRESERVED:");
        System.out.println(refactoredCode);
    }
    
    @Test
    void testRemoveIsTrueWrappers() {
        String code = """
            public class Test {
                public void method() {
                    if (isTrue(condition)) {
                        System.out.println("test");
                    }
                    
                    while (isTrue(otherCondition)) {
                        doSomething();
                    }
                    
                    return isTrue(result);
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        boolean hasChanges = cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(hasChanges, "Should have detected changes");
        assertTrue(cleaner.getTransformationsCount() >= 3, "Should have removed isTrue() wrappers");
        
        String refactoredCode = cu.toString();
        
        // Verify isTrue() was removed
        assertFalse(refactoredCode.contains("isTrue("), "isTrue() should be removed");
        
        // Verify conditions still exist
        assertTrue(refactoredCode.contains("if (condition)"), "if condition should remain");
        assertTrue(refactoredCode.contains("while (otherCondition)"), "while condition should remain");
        assertTrue(refactoredCode.contains("return result"), "return should remain without isTrue");
    }
    
    @Test
    void testRemoveNotWrappers() {
        String code = """
            public class Test {
                public boolean method() {
                    if (not(condition)) {
                        return not(otherCondition);
                    }
                    return not(finalCondition);
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(cleaner.getTransformationsCount() >= 3, "Should have removed not() wrappers");
        
        String refactoredCode = cu.toString();
        
        // Verify not() was removed and ! was added
        assertFalse(refactoredCode.contains("not("), "not() should be removed");
        assertTrue(refactoredCode.contains("!condition"), "!condition should be added");
        assertTrue(refactoredCode.contains("!otherCondition"), "!otherCondition should be added");
        assertTrue(refactoredCode.contains("!finalCondition"), "!finalCondition should be added");
    }
    
    @Test
    void testSimplifyTypeWrappers() {
        String code = """
            public class Test {
                private WebMapAtomicReference<String> ref1;
                private WebMapAtomicReference<BigDecimal> ref2;
                
                public void method() {
                    WebMapAtomicReference<Integer> localRef = new WebMapAtomicReference<>();
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(cleaner.getTransformationsCount() >= 3, "Should have simplified type wrappers");
        
        String refactoredCode = cu.toString();
        
        // Verify WebMapAtomicReference was replaced
        assertFalse(refactoredCode.contains("WebMapAtomicReference"), "WebMapAtomicReference should be replaced");
        assertTrue(refactoredCode.contains("AtomicReference"), "AtomicReference should be added");
    }
    
    @Test
    void testResetCounter() {
        String code = """
            public class Test {
                BigDecimal value = createDecimal(BigDecimal.ZERO, 2);
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(cleaner.getTransformationsCount() > 0, "Should have transformations");
        
        cleaner.resetCounter();
        assertEquals(0, cleaner.getTransformationsCount(), "Counter should be reset");
    }
    
    @Test
    void testCompleteIntegrationExample() {
        // Complete example showing all safe transformations working together
        String code = """
            public class CompletePayrollExample {
                BigDecimal ldcSalario = createDecimal(BigDecimal.ZERO, 2);
                BigDecimal ldcValorTemporario = createDecimal(BigDecimal.ZERO, 6);
                
                public void calculatePayroll() {
                    WebMapAtomicReference<BigDecimal> salaryRef = new WebMapAtomicReference<>(ldcSalario);
                    
                    if (isTrue(not(eq(ldcSalario, BigDecimal.ZERO)))) {
                        ldcValorTemporario = setScale(ldcValorTemporario, 
                            multiply(multiply(ldcSalario, getHours()), divide(getTaxRate(), 100)));
                    }
                    
                    salaryRef.set(plus(ldcSalario, ldcValorTemporario));
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        boolean hasChanges = cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(hasChanges, "Should have comprehensive changes");
        assertTrue(cleaner.getTransformationsCount() >= 8, "Should have multiple transformations");
        
        String refactoredCode = cu.toString();
        
        // Verify all transformations applied safely
        assertTrue(refactoredCode.contains("setScale(2, RoundingMode.HALF_UP)"), "Decimal precision preserved");
        assertTrue(refactoredCode.contains("setScale(6, RoundingMode.HALF_UP)"), "Calculation precision preserved");
        assertTrue(refactoredCode.contains("AtomicReference"), "Type wrapper simplified");
        assertTrue(refactoredCode.contains("RoundingMode.HALF_UP"), "Rounding mode added for safety");
        
        assertFalse(refactoredCode.contains("createDecimal("), "createDecimal() removed");
        assertFalse(refactoredCode.contains("isTrue("), "isTrue() removed");
        assertFalse(refactoredCode.contains("not("), "not() removed");
        assertFalse(refactoredCode.contains("WebMapAtomicReference"), "WebMapAtomicReference removed");
        
        System.out.println("🚀 COMPLETE INTEGRATION - ALL SAFE TRANSFORMATIONS:");
        System.out.println(refactoredCode);
    }
}