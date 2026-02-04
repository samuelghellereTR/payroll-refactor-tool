package com.tr.refactor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o MobilizeWrapperCleaner - verifica se remove corretamente
 * os wrappers problemáticos do framework Mobilize.
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
        
        assertTrue(hasChanges, "Deveria ter detectado mudanças");
        assertEquals(3, cleaner.getTransformationsCount(), "Deveria ter removido 3 wrappers isTrue()");
        
        String refactoredCode = cu.toString();
        
        // Verifica se isTrue() foi removido
        assertFalse(refactoredCode.contains("isTrue("), "isTrue() deveria ter sido removido");
        
        // Verifica se as condições ainda existem
        assertTrue(refactoredCode.contains("if (condition)"), "Condição do if deveria permanecer");
        assertTrue(refactoredCode.contains("while (otherCondition)"), "Condição do while deveria permanecer");
        assertTrue(refactoredCode.contains("return result"), "Return deveria permanecer sem isTrue");
    }
    
    @Test
    void testReplaceCreateDecimalWrappers() {
        String code = """
            public class Test {
                private BigDecimal valor1 = createDecimal(BigDecimal.ZERO, 2);
                private BigDecimal valor2 = createDecimal(new BigDecimal("100"), 4);
                
                public void method() {
                    BigDecimal temp = createDecimal(BigDecimal.ONE, 2);
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        boolean hasChanges = cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(hasChanges, "Deveria ter detectado mudanças");
        assertTrue(cleaner.getTransformationsCount() >= 3, "Deveria ter removido pelo menos 3 wrappers createDecimal()");
        
        String refactoredCode = cu.toString();
        
        // Verifica se createDecimal() foi removido
        assertFalse(refactoredCode.contains("createDecimal("), "createDecimal() deveria ter sido removido");
        
        // Verifica se BigDecimal.ZERO foi preservado
        assertTrue(refactoredCode.contains("BigDecimal.ZERO"), "BigDecimal.ZERO deveria ter sido preservado");
    }
    
    @Test
    void testSimplifyTypeWrappers() {
        String code = """
            public class Test {
                private WebMapAtomicReference<String> ref1;
                private WebMapAtomicReference<IuoBase> ref2;
                
                public void method() {
                    WebMapAtomicReference<Integer> localRef = new WebMapAtomicReference<>();
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        boolean hasChanges = cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(hasChanges, "Deveria ter detectado mudanças");
        assertTrue(cleaner.getTransformationsCount() >= 3, "Deveria ter simplificado pelo menos 3 tipos");
        
        String refactoredCode = cu.toString();
        
        // Verifica se WebMapAtomicReference foi substituído
        assertFalse(refactoredCode.contains("WebMapAtomicReference"), "WebMapAtomicReference deveria ter sido substituído");
        
        // Verifica se AtomicReference foi adicionado
        assertTrue(refactoredCode.contains("AtomicReference"), "AtomicReference deveria ter sido adicionado");
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
        cleaner.removeNotWrappers(cu);
        
        assertTrue(cleaner.getTransformationsCount() >= 3, "Deveria ter removido pelo menos 3 wrappers not()");
        
        String refactoredCode = cu.toString();
        
        // Verifica se not() foi removido
        assertFalse(refactoredCode.contains("not("), "not() deveria ter sido removido");
        
        // Verifica se ! foi adicionado
        assertTrue(refactoredCode.contains("!condition"), "!condition deveria ter sido adicionado");
        assertTrue(refactoredCode.contains("!otherCondition"), "!otherCondition deveria ter sido adicionado");
        assertTrue(refactoredCode.contains("!finalCondition"), "!finalCondition deveria ter sido adicionado");
    }
    
    @Test
    void testExemploRealCompleto() {
        // Este é o exemplo real que você forneceu
        String code = """
            public class ExemploReal {
                protected Short giCodSis = 0;
                protected BigDecimal gdcValor = createDecimal(BigDecimal.ZERO, 2);
                
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
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        boolean hasChanges = cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(hasChanges, "Deveria ter detectado mudanças no exemplo real");
        assertTrue(cleaner.getTransformationsCount() >= 4, "Deveria ter aplicado múltiplas transformações");
        
        String refactoredCode = cu.toString();
        
        // Verifica principais melhorias
        assertFalse(refactoredCode.contains("isTrue("), "isTrue() deveria ter sido removido");
        assertFalse(refactoredCode.contains("WebMapAtomicReference"), "WebMapAtomicReference deveria ter sido substituído");
        assertFalse(refactoredCode.contains("createDecimal("), "createDecimal() deveria ter sido removido");
        
        assertTrue(refactoredCode.contains("AtomicReference"), "AtomicReference deveria ter sido adicionado");
        assertTrue(refactoredCode.contains("BigDecimal.ZERO"), "BigDecimal.ZERO deveria ter sido preservado");
        
        System.out.println("🎉 EXEMPLO REAL REFATORADO:");
        System.out.println(refactoredCode);
    }
    
    @Test
    void testResetCounter() {
        String code = """
            public class Test {
                public boolean method() {
                    return isTrue(condition);
                }
            }
            """;
        
        CompilationUnit cu = parser.parse(code).getResult().get();
        cleaner.cleanMobilizeWrappers(cu);
        
        assertTrue(cleaner.getTransformationsCount() > 0, "Deveria ter transformações");
        
        cleaner.resetCounter();
        assertEquals(0, cleaner.getTransformationsCount(), "Counter deveria ter sido resetado");
    }
}