#!/usr/bin/env python3
"""
Ferramenta de refatoração automática para código PowerBuilder migrado para Java.

Implementação em Python usando manipulação textual e regex para transformar
nomenclaturas PowerBuilder em padrões Java legíveis.
"""

import argparse
import re
import shutil
from pathlib import Path
from typing import Dict, List, Tuple, Optional
import logging

# Configuração de logging
logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
logger = logging.getLogger(__name__)

class PowerBuilderRefactor:
    """Ferramenta principal de refatoração."""
    
    def __init__(self, input_dir: Path, output_dir: Path = None, 
                 dry_run: bool = False, verbose: bool = False, 
                 create_backup: bool = True):
        self.input_dir = Path(input_dir)
        self.output_dir = Path(output_dir) if output_dir else self.input_dir
        self.dry_run = dry_run
        self.verbose = verbose
        self.create_backup = create_backup
        
        # Estatísticas
        self.processed_files = 0
        self.transformations_applied = 0
        self.warnings = []
        
        # Padrões de conversão
        self.setup_conversion_patterns()
    
    def setup_conversion_patterns(self):
        """Configura os padrões de conversão PowerBuilder -> Java."""
        
        # Padrões de nomenclatura de classes
        self.class_patterns = [
            (r'^([a-z][a-z0-9_]+)\.java$', self._convert_class_name),
        ]
        
        # Padrões de métodos PowerBuilder
        self.method_patterns = [
            (r'\bof_calc_payroll\b', 'calculatePayroll'),
            (r'\bof_execute_test\b', 'executeTest'),
            (r'\bof_get_(\w+)', r'get\1'),
            (r'\bof_set_(\w+)', r'set\1'),
            (r'\bof_is_(\w+)', r'is\1'),
            (r'\bof_(\w+)', r'\1'),
        ]
        
        # Padrões de variáveis PowerBuilder
        self.variable_patterns = [
            (r'\bgi(\w+)', r'codigo\1'),  # global integer
            (r'\bgl(\w+)', r'codigo\1'),  # global long
            (r'\bgs(\w+)', r'\1'),        # global string
            (r'\bgdc(\w+)', r'valor\1'),  # global decimal
            (r'\bao_(\w+)', r'\1'),       # argument object
            (r'\bas_(\w+)', r'\1'),       # argument string
            (r'\badc_(\w+)', r'\1'),      # argument decimal
            (r'\bab_(\w+)', r'\1'),       # argument boolean
        ]
        
        # Tradução de termos de folha de pagamento
        self.payroll_terms = {
            'folha': 'Payroll',
            'calculo': 'Calculation',
            'salario': 'Salary',
            'desconto': 'Discount',
            'imposto': 'Tax',
            'inss': 'Inss',
            'fgts': 'Fgts',
            'irrf': 'Irrf',
            'empresa': 'Company',
            'empregado': 'Employee',
            'funcionario': 'Employee',
            'parametro': 'Parameter',
            'base': 'Base',
            'valor': 'Value',
            'taxa': 'Rate',
            'codigo': 'Code',
            'sistema': 'System',
            'dados': 'Data',
            'teste': 'Test',
            'executor': 'Executor',
        }
    
    def _convert_class_name(self, match) -> str:
        """Converte nome de classe PowerBuilder para Java."""
        filename = match.group(1)
        
        # Remove prefixos PowerBuilder
        if filename.startswith(('uo_', 'str_', 's_', 'n_', 'dfc_')):
            parts = filename.split('_', 1)
            if len(parts) > 1:
                filename = parts[1]
        
        # Converte para PascalCase
        converted = self._underscore_to_camel_case(filename, pascal_case=True)
        
        # Traduz termos de folha de pagamento
        converted = self._translate_payroll_terms(converted)
        
        return converted + '.java'
    
    def _underscore_to_camel_case(self, text: str, pascal_case: bool = False) -> str:
        """Converte texto com underscores para camelCase/PascalCase."""
        parts = text.lower().split('_')
        
        if pascal_case:
            return ''.join(word.capitalize() for word in parts if word)
        else:
            if not parts:
                return text
            return parts[0] + ''.join(word.capitalize() for word in parts[1:] if word)
    
    def _translate_payroll_terms(self, text: str) -> str:
        """Traduz termos específicos de folha de pagamento."""
        result = text
        
        for portuguese, english in self.payroll_terms.items():
            # Substitui termos completos (case insensitive)
            pattern = r'\b' + re.escape(portuguese) + r'\b'
            result = re.sub(pattern, english, result, flags=re.IGNORECASE)
        
        return result
    
    def process_java_file(self, file_path: Path) -> bool:
        """Processa um arquivo Java individual."""
        try:
            # Lê o conteúdo do arquivo
            content = file_path.read_text(encoding='utf-8')
            original_content = content
            
            # Aplica transformações
            content = self._apply_method_transformations(content)
            content = self._apply_variable_transformations(content)
            content = self._apply_payroll_translations(content)
            
            # Verifica se houve mudanças
            if content != original_content:
                self._save_refactored_file(file_path, content)
                self.processed_files += 1
                return True
            
            return False
            
        except Exception as e:
            warning = f"Erro ao processar {file_path}: {str(e)}"
            self.warnings.append(warning)
            if self.verbose:
                logger.warning(warning)
            return False
    
    def _apply_method_transformations(self, content: str) -> str:
        """Aplica transformações nos nomes de métodos."""
        result = content
        
        for pattern, replacement in self.method_patterns:
            if isinstance(replacement, str):
                new_result = re.sub(pattern, replacement, result)
            else:
                new_result = re.sub(pattern, replacement, result)
            
            if new_result != result:
                self.transformations_applied += 1
                result = new_result
        
        return result
    
    def _apply_variable_transformations(self, content: str) -> str:
        """Aplica transformações nos nomes de variáveis."""
        result = content
        
        for pattern, replacement in self.variable_patterns:
            new_result = re.sub(pattern, replacement, result)
            if new_result != result:
                self.transformations_applied += 1
                result = new_result
        
        return result
    
    def _apply_payroll_translations(self, content: str) -> str:
        """Aplica traduções de termos de folha de pagamento."""
        result = content
        
        for portuguese, english in self.payroll_terms.items():
            pattern = r'\b' + re.escape(portuguese) + r'\b'
            new_result = re.sub(pattern, english, result, flags=re.IGNORECASE)
            if new_result != result:
                self.transformations_applied += 1
                result = new_result
        
        return result
    
    def _save_refactored_file(self, original_path: Path, content: str):
        """Salva o arquivo refatorado."""
        # Cria backup se solicitado
        if self.create_backup and not self.dry_run:
            backup_path = original_path.with_suffix(original_path.suffix + '.backup')
            shutil.copy2(original_path, backup_path)
        
        if not self.dry_run:
            # Calcula o caminho de saída
            relative_path = original_path.relative_to(self.input_dir)
            output_path = self.output_dir / relative_path
            
            # Cria diretórios se necessário
            output_path.parent.mkdir(parents=True, exist_ok=True)
            
            # Escreve o arquivo refatorado
            output_path.write_text(content, encoding='utf-8')
        
        if self.verbose:
            logger.info(f"✅ Processado: {original_path}")
    
    def process_directory(self) -> Dict[str, any]:
        """Processa todos os arquivos Java no diretório."""
        logger.info(f"🔧 Iniciando refatoração em: {self.input_dir}")
        
        # Encontra todos os arquivos Java
        java_files = list(self.input_dir.rglob("*.java"))
        
        if not java_files:
            logger.warning("Nenhum arquivo Java encontrado!")
            return self._get_result()
        
        logger.info(f"📁 Encontrados {len(java_files)} arquivos Java")
        
        # Processa cada arquivo
        for java_file in java_files:
            if self.verbose:
                logger.info(f"🔍 Analisando: {java_file}")
            
            self.process_java_file(java_file)
        
        return self._get_result()
    
    def _get_result(self) -> Dict[str, any]:
        """Retorna o resultado da refatoração."""
        return {
            'success': True,
            'processed_files': self.processed_files,
            'transformations_applied': self.transformations_applied,
            'warnings': self.warnings
        }

def main():
    """Função principal CLI."""
    parser = argparse.ArgumentParser(
        description='Refatora código PowerBuilder migrado para Java'
    )
    
    parser.add_argument('input_dir', type=Path,
                       help='Diretório de entrada com código Java')
    parser.add_argument('-o', '--output', type=Path,
                       help='Diretório de saída (padrão: mesmo diretório)')
    parser.add_argument('-d', '--dry-run', action='store_true',
                       help='Executa sem modificar arquivos')
    parser.add_argument('-v', '--verbose', action='store_true',
                       help='Saída detalhada')
    parser.add_argument('--no-backup', action='store_true',
                       help='Não cria backup dos arquivos originais')
    
    args = parser.parse_args()
    
    # Configura logging
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)
    
    # Cria a ferramenta de refatoração
    refactor = PowerBuilderRefactor(
        input_dir=args.input_dir,
        output_dir=args.output,
        dry_run=args.dry_run,
        verbose=args.verbose,
        create_backup=not args.no_backup
    )
    
    # Executa a refatoração
    result = refactor.process_directory()
    
    # Mostra resultados
    print("\n✅ Refatoração concluída!")
    print(f"📊 Arquivos processados: {result['processed_files']}")
    print(f"🔄 Transformações aplicadas: {result['transformations_applied']}")
    print(f"⚠️  Avisos: {len(result['warnings'])}")
    
    if result['warnings']:
        print("\n⚠️  Avisos encontrados:")
        for warning in result['warnings']:
            print(f"  - {warning}")
    
    return 0 if result['success'] else 1

if __name__ == '__main__':
    exit(main())