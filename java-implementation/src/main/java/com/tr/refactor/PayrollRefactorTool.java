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
 * VERSÃO ATUALIZADA: Agora remove wrappers Mobilize problemáticos como isTrue().
 * 
 * Esta ferramenta identifica padrões específicos do código gerado pela migração
 * PowerBuilder->Java e aplica transformações para melhorar a legibilidade,
 * preservando completamente a lógica de negócio e arquitetura.
 */
@Command(
    name = "payroll-refactor", 
    mixinStandardHelpOptions = true,
    version = "2.0.0",
    description = "Refatora código PowerBuilder migrado para Java melhorando legibilidade e removendo wrappers Mobilize"
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
        System.out.println("🔧 Payroll Refactor Tool v2.0.0");
        System.out.println("🔥 NOVA VERSÃO: Remove wrappers Mobilize problemáticos!");
        System.out.println("📁 Analisando: " + inputDir);
        
        if (dryRun) {
            System.out.println("🧪 Modo DRY-RUN: Nenhum arquivo será modificado");
        }
        
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
        
        // Usa o novo método getSummary() para output formatado
        System.out.println("\n" + result.getSummary());
        
        if (result.getTransformationsApplied() > 0) {
            System.out.println("🎉 Transformações principais aplicadas:");
            System.out.println("  🔥 Wrappers isTrue() removidos");
            System.out.println("  🧮 Helpers matemáticos simplificados");
            System.out.println("  📦 Tipos Mobilize convertidos para Java padrão");
            System.out.println("  📝 Nomenclatura PowerBuilder convertida");
        }
        
        if (!dryRun && result.getProcessedFiles() > 0) {
            System.out.println("\n💡 Próximos passos:");
            System.out.println("  1. Compile o código para verificar sintaxe");
            System.out.println("  2. Execute os testes existentes");
            System.out.println("  3. Revise as mudanças manualmente");
            if (createBackup) {
                System.out.println("  4. Remova arquivos .backup se tudo estiver OK");
            }
        }
        
        return result.isSuccess() ? 0 : 1;
    }
}