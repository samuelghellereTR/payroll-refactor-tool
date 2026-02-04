package com.tr.refactor;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Ferramenta CLI para refatoração automática de código PowerBuilder migrado para Java.
 * 
 * Esta ferramenta identifica padrões específicos do código gerado pela migração
 * PowerBuilder->Java e aplica transformações para melhorar a legibilidade,
 * preservando completamente a lógica de negócio e arquitetura.
 */
@Command(
    name = "payroll-refactor", 
    mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "Refatora código PowerBuilder migrado para Java melhorando legibilidade"
)
public class PayrollRefactorTool implements Callable<Integer> {

    @Parameters(index = "0", description = "Diretório de entrada com código Java")
    private Path inputDir;

    @Option(names = {"-o", "--output"}, description = "Diretório de saída (padrão: mesmo diretório)")
    private Path outputDir;

    @Option(names = {"-d", "--dry-run"}, description = "Executa sem modificar arquivos")
    private boolean dryRun = false;

    @Option(names = {"-v", "--verbose"}, description = "Saída detalhada")
    private boolean verbose = false;

    @Option(names = {"--preserve-comments"}, description = "Preserva comentários originais")
    private boolean preserveComments = true;

    @Option(names = {"--backup"}, description = "Cria backup dos arquivos originais")
    private boolean createBackup = true;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PayrollRefactorTool()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("🔧 Payroll Refactor Tool v1.0.0");
        System.out.println("📁 Analisando: " + inputDir);
        
        if (outputDir == null) {
            outputDir = inputDir;
        }
        
        RefactorEngine engine = new RefactorEngine(
            inputDir, 
            outputDir, 
            dryRun, 
            verbose, 
            preserveComments, 
            createBackup
        );
        
        RefactorResult result = engine.execute();
        
        System.out.println("\n✅ Refatoração concluída!");
        System.out.println("📊 Arquivos processados: " + result.getProcessedFiles());
        System.out.println("🔄 Transformações aplicadas: " + result.getTransformationsApplied());
        System.out.println("⚠️  Avisos: " + result.getWarnings().size());
        
        if (!result.getWarnings().isEmpty()) {
            System.out.println("\n⚠️  Avisos encontrados:");
            result.getWarnings().forEach(warning -> 
                System.out.println("  - " + warning)
            );
        }
        
        return result.isSuccess() ? 0 : 1;
    }
}