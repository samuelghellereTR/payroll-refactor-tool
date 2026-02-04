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
}