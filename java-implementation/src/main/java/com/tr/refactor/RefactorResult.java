package com.tr.refactor;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado da execução da refatoração.
 * 
 * Contém estatísticas e informações sobre o processo de refatoração.
 */
public class RefactorResult {
    
    private int processedFiles = 0;
    private int transformationsApplied = 0;
    private List<String> warnings = new ArrayList<>();
    private boolean success = true;
    
    public void incrementProcessedFiles() {
        this.processedFiles++;
    }
    
    public void incrementTransformations() {
        this.transformationsApplied++;
    }
    
    /**
     * NOVO: Adiciona múltiplas transformações de uma vez.
     * Usado quando o MobilizeWrapperCleaner aplica várias transformações.
     */
    public void addTransformations(int count) {
        this.transformationsApplied += count;
    }
    
    public void addWarning(String warning) {
        this.warnings.add(warning);
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    // Getters
    public int getProcessedFiles() {
        return processedFiles;
    }
    
    public int getTransformationsApplied() {
        return transformationsApplied;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Retorna um resumo formatado dos resultados.
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("📊 RESUMO DA REFATORAÇÃO:\n");
        sb.append("  • Arquivos processados: ").append(processedFiles).append("\n");
        sb.append("  • Transformações aplicadas: ").append(transformationsApplied).append("\n");
        sb.append("  • Warnings: ").append(warnings.size()).append("\n");
        sb.append("  • Status: ").append(success ? "✅ Sucesso" : "❌ Falha").append("\n");
        
        if (!warnings.isEmpty()) {
            sb.append("\n⚠️  WARNINGS:\n");
            warnings.forEach(warning -> sb.append("  • ").append(warning).append("\n"));
        }
        
        return sb.toString();
    }
}