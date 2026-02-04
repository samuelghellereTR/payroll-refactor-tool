package com.tr.refactor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import java.util.List;
import java.util.Optional;

/**
 * Remove wrappers desnecessários do framework Mobilize que tornam o código ilegível.
 * 
 * Esta classe é responsável por:
 * - Remover wrappers isTrue() 
 * - Substituir helpers matemáticos por código Java nativo
 * - Simplificar wrappers de tipos (WebMapAtomicReference, etc.)
 * - Limpar imports estáticos desnecessários
 */
public class MobilizeWrapperCleaner {
    
    private int transformationsCount = 0;
    
    /**
     * Remove todos os wrappers Mobilize problemáticos do código.
     * 
     * @param cu CompilationUnit a ser processada
     * @return true se houve mudanças
     */
    public boolean cleanMobilizeWrappers(CompilationUnit cu) {
        transformationsCount = 0;
        
        // Remove wrappers isTrue()
        removeIsTrueWrappers(cu);
        
        // Substitui helpers matemáticos
        replaceMathHelpers(cu);
        
        // Simplifica wrappers de tipos
        simplifyTypeWrappers(cu);
        
        // Remove createDecimal wrappers
        replaceCreateDecimalWrappers(cu);
        
        return transformationsCount > 0;
    }
    
    /**
     * Remove wrappers isTrue() que são a principal dor do código.
     * 
     * Transforma:
     * if (isTrue(expression)) → if (expression)
     * while (isTrue(condition)) → while (condition)
     * return isTrue(value) → return value
     */
    private void removeIsTrueWrappers(CompilationUnit cu) {
        cu.findAll(MethodCallExpr.class)
          .stream()
          .filter(call -> "isTrue".equals(call.getNameAsString()))
          .filter(call -> call.getArguments().size() == 1)
          .forEach(call -> {
              Expression argument = call.getArgument(0);
              
              // Substitui isTrue(expression) por expression
              call.replace(argument);
              transformationsCount++;
              
              System.out.println("  🔥 Removido isTrue(): " + call + " → " + argument);
          });
    }
    
    /**
     * Substitui helpers matemáticos do Mobilize por código BigDecimal nativo.
     * 
     * Transforma:
     * setScale(a, minus(a, b)) → a = a.subtract(b)
     * setScale(a, plus(a, b)) → a = a.add(b)
     * setScale(a, multiply(a, b)) → a = a.multiply(b)
     */
    private void replaceMathHelpers(CompilationUnit cu) {
        // Procura por padrões setScale(var, operation(var, value))
        cu.findAll(MethodCallExpr.class)
          .stream()
          .filter(call -> "setScale".equals(call.getNameAsString()))
          .filter(call -> call.getArguments().size() == 2)
          .forEach(call -> {
              Expression firstArg = call.getArgument(0);
              Expression secondArg = call.getArgument(1);
              
              // Verifica se o segundo argumento é uma operação matemática
              if (secondArg instanceof MethodCallExpr mathOp) {
                  String replacement = convertMathOperation(firstArg, mathOp);
                  if (replacement != null) {
                      // TODO: Implementar substituição completa da expressão
                      // Requer análise do contexto (assignment, etc.)
                      transformationsCount++;
                      System.out.println("  🧮 Math helper: " + call + " → " + replacement);
                  }
              }
          });
    }
    
    /**
     * Converte operações matemáticas do Mobilize para BigDecimal nativo.
     */
    private String convertMathOperation(Expression target, MethodCallExpr mathOp) {
        String operationName = mathOp.getNameAsString();
        
        if (mathOp.getArguments().size() != 2) {
            return null;
        }
        
        Expression arg1 = mathOp.getArgument(0);
        Expression arg2 = mathOp.getArgument(1);
        
        // Verifica se o primeiro argumento é o mesmo que o target
        if (!arg1.toString().equals(target.toString())) {
            return null;
        }
        
        return switch (operationName) {
            case "minus" -> target + " = " + target + ".subtract(" + arg2 + ")";
            case "plus" -> target + " = " + target + ".add(" + arg2 + ")";
            case "multiply" -> target + " = " + target + ".multiply(" + arg2 + ")";
            case "divide" -> target + " = " + target + ".divide(" + arg2 + ")";
            default -> null;
        };
    }
    
    /**
     * Simplifica wrappers de tipos específicos do Mobilize.
     * 
     * Transforma:
     * WebMapAtomicReference<Type> → AtomicReference<Type>
     */
    private void simplifyTypeWrappers(CompilationUnit cu) {
        cu.findAll(ClassOrInterfaceType.class)
          .stream()
          .filter(type -> "WebMapAtomicReference".equals(type.getNameAsString()))
          .forEach(type -> {
              type.setName("AtomicReference");
              transformationsCount++;
              System.out.println("  📦 Tipo simplificado: WebMapAtomicReference → AtomicReference");
          });
    }
    
    /**
     * Remove wrappers createDecimal desnecessários.
     * 
     * Transforma:
     * createDecimal(BigDecimal.ZERO, 2) → BigDecimal.ZERO
     * createDecimal(value, scale) → value
     */
    private void replaceCreateDecimalWrappers(CompilationUnit cu) {
        cu.findAll(MethodCallExpr.class)
          .stream()
          .filter(call -> "createDecimal".equals(call.getNameAsString()))
          .forEach(call -> {
              if (call.getArguments().size() >= 1) {
                  Expression firstArg = call.getArgument(0);
                  
                  // Se o primeiro argumento é BigDecimal.ZERO, substitui diretamente
                  if (firstArg.toString().contains("BigDecimal.ZERO")) {
                      call.replace(new NameExpr("BigDecimal.ZERO"));
                  } else {
                      // Caso contrário, usa apenas o primeiro argumento
                      call.replace(firstArg);
                  }
                  
                  transformationsCount++;
                  System.out.println("  💰 createDecimal removido: " + call + " → " + firstArg);
              }
          });
    }
    
    /**
     * Remove wrappers not() desnecessários.
     * 
     * Transforma:
     * not(expression) → !expression
     */
    public void removeNotWrappers(CompilationUnit cu) {
        cu.findAll(MethodCallExpr.class)
          .stream()
          .filter(call -> "not".equals(call.getNameAsString()))
          .filter(call -> call.getArguments().size() == 1)
          .forEach(call -> {
              Expression argument = call.getArgument(0);
              UnaryExpr negation = new UnaryExpr(argument, UnaryExpr.Operator.LOGICAL_COMPLEMENT);
              
              call.replace(negation);
              transformationsCount++;
              
              System.out.println("  ❗ Removido not(): " + call + " → !" + argument);
          });
    }
    
    /**
     * Retorna o número de transformações aplicadas.
     */
    public int getTransformationsCount() {
        return transformationsCount;
    }
    
    /**
     * Reseta o contador de transformações.
     */
    public void resetCounter() {
        transformationsCount = 0;
    }
}