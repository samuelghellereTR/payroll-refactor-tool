package com.tr.refactor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Engine principal de refatoração que aplica transformações específicas
 * para código PowerBuilder migrado para Java.
 */
public class RefactorEngine {
    
    private final Path inputDir;
    private final Path outputDir;
    private final boolean dryRun;
    private final boolean verbose;
    private final boolean preserveComments;
    private final boolean createBackup;
    
    private final JavaParser javaParser;
    private final PowerBuilderPatternMatcher patternMatcher;
    private final NameConverter nameConverter;
    
    public RefactorEngine(Path inputDir, Path outputDir, boolean dryRun, 
                         boolean verbose, boolean preserveComments, boolean createBackup) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.dryRun = dryRun;
        this.verbose = verbose;
        this.preserveComments = preserveComments;
        this.createBackup = createBackup;
        
        this.javaParser = new JavaParser();
        this.patternMatcher = new PowerBuilderPatternMatcher();
        this.nameConverter = new NameConverter();
    }
    
    public RefactorResult execute() throws IOException {
        RefactorResult result = new RefactorResult();
        
        try (Stream<Path> javaFiles = Files.walk(inputDir)
                .filter(path -> path.toString().endsWith(".java"))) {
            
            javaFiles.forEach(javaFile -> {
                try {
                    processJavaFile(javaFile, result);
                } catch (Exception e) {
                    result.addWarning("Erro ao processar " + javaFile + ": " + e.getMessage());
                    if (verbose) {
                        e.printStackTrace();
                    }
                }
            });
        }
        
        return result;
    }
    
    private void processJavaFile(Path javaFile, RefactorResult result) throws IOException {
        if (verbose) {
            System.out.println("🔍 Processando: " + javaFile);
        }
        
        // Lê o arquivo Java
        String content = Files.readString(javaFile);
        
        // Faz o parse do código Java
        ParseResult<CompilationUnit> parseResult = javaParser.parse(content);
        
        if (!parseResult.isSuccessful()) {
            result.addWarning("Falha ao parsear: " + javaFile);
            return;
        }
        
        CompilationUnit cu = parseResult.getResult().get();
        boolean hasChanges = false;
        
        // Aplica transformações
        hasChanges |= refactorClassNames(cu, result);
        hasChanges |= refactorMethodNames(cu, result);
        hasChanges |= refactorFieldNames(cu, result);
        hasChanges |= refactorVariableNames(cu, result);
        
        // Se houve mudanças, salva o arquivo
        if (hasChanges) {
            saveRefactoredFile(javaFile, cu, result);
            result.incrementProcessedFiles();
        }
    }
    
    private boolean refactorClassNames(CompilationUnit cu, RefactorResult result) {
        boolean hasChanges = false;
        
        for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            String oldName = cls.getNameAsString();
            
            if (patternMatcher.isPowerBuilderClassName(oldName)) {
                String newName = nameConverter.convertClassName(oldName);
                
                if (!oldName.equals(newName)) {
                    cls.setName(newName);
                    hasChanges = true;
                    result.incrementTransformations();
                    
                    if (verbose) {
                        System.out.println("  📝 Classe: " + oldName + " → " + newName);
                    }
                }
            }
        }
        
        return hasChanges;
    }
    
    private boolean refactorMethodNames(CompilationUnit cu, RefactorResult result) {
        boolean hasChanges = false;
        
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            String oldName = method.getNameAsString();
            
            if (patternMatcher.isPowerBuilderMethodName(oldName)) {
                String newName = nameConverter.convertMethodName(oldName);
                
                if (!oldName.equals(newName)) {
                    method.setName(newName);
                    hasChanges = true;
                    result.incrementTransformations();
                    
                    if (verbose) {
                        System.out.println("  🔧 Método: " + oldName + " → " + newName);
                    }
                }
            }
            
            // Refatora parâmetros do método
            for (Parameter param : method.getParameters()) {
                String oldParamName = param.getNameAsString();
                
                if (patternMatcher.isPowerBuilderVariableName(oldParamName)) {
                    String newParamName = nameConverter.convertVariableName(oldParamName);
                    
                    if (!oldParamName.equals(newParamName)) {
                        param.setName(newParamName);
                        hasChanges = true;
                        result.incrementTransformations();
                        
                        if (verbose) {
                            System.out.println("  📋 Parâmetro: " + oldParamName + " → " + newParamName);
                        }
                    }
                }
            }
        }
        
        return hasChanges;
    }
    
    private boolean refactorFieldNames(CompilationUnit cu, RefactorResult result) {
        boolean hasChanges = false;
        
        for (FieldDeclaration field : cu.findAll(FieldDeclaration.class)) {
            for (VariableDeclarator var : field.getVariables()) {
                String oldName = var.getNameAsString();
                
                if (patternMatcher.isPowerBuilderVariableName(oldName)) {
                    String newName = nameConverter.convertVariableName(oldName);
                    
                    if (!oldName.equals(newName)) {
                        var.setName(newName);
                        hasChanges = true;
                        result.incrementTransformations();
                        
                        if (verbose) {
                            System.out.println("  🏷️  Campo: " + oldName + " → " + newName);
                        }
                    }
                }
            }
        }
        
        return hasChanges;
    }
    
    private boolean refactorVariableNames(CompilationUnit cu, RefactorResult result) {
        // TODO: Implementar refatoração de variáveis locais
        // Requer análise mais complexa do escopo
        return false;
    }
    
    private void saveRefactoredFile(Path originalFile, CompilationUnit cu, RefactorResult result) 
            throws IOException {
        
        // Cria backup se solicitado
        if (createBackup && !dryRun) {
            Path backupFile = originalFile.resolveSibling(originalFile.getFileName() + ".backup");
            Files.copy(originalFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Gera o código refatorado
        DefaultPrinterConfiguration config = new DefaultPrinterConfiguration();
        config.setOrderImports(true);
        config.setRemoveUnusedImports(true);
        
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter(config);
        String refactoredCode = printer.print(cu);
        
        if (!dryRun) {
            // Calcula o arquivo de saída
            Path relativePath = inputDir.relativize(originalFile);
            Path outputFile = outputDir.resolve(relativePath);
            
            // Cria diretórios se necessário
            Files.createDirectories(outputFile.getParent());
            
            // Escreve o arquivo refatorado
            Files.writeString(outputFile, refactoredCode);
        }
        
        if (verbose) {
            System.out.println("  ✅ Salvo: " + originalFile);
        }
    }
}