package utilidades;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import modelo.Elemento;
import aprendizado.Regressao;
import aprendizado.RegressaoLogistica;
import entrada_saida.CategorizacaoPorFaixas;
import entrada_saida.PreenchimentoPrimeiroCampo;
import extracaoFeatures.Extrator;
import extracaoFeatures.ExtratorBinario;
import extracaoFeatures.IgualdadePrimeiroCampo;

public abstract class Constantes {
	public static final double VALOR_POSITIVO = 1.0;
	public static final double VALOR_NEGATIVO = 0.0;
	public static final double LIMIAR_PADRAO = 0.5;
	public static final double PORCENTAGEM_TESTE_PADRAO = 0.2;
	public static final int REPETICOES_TESTE_CONFIANCA = 50;
	public static final double TOLERANCIA_PADRAO_DESDUPLICACAO = 0.05;
	public static final String STRING_IGUAIS = "IGUAIS";
	public static final String STRING_DIFERENTES = "DIFERENTES";
	
	public static final Function<Double, String> CATEGORIZACAO_PADRAO;
	public static final BiFunction<Elemento, Elemento, Elemento> PREENCHIMENTO_PADRAO = new PreenchimentoPrimeiroCampo();
	
	static {
		List<Double> corte = new ArrayList<Double>();
		corte.add(LIMIAR_PADRAO);
		List<String> categorias = new ArrayList<String>();
		categorias.add(STRING_IGUAIS);
		categorias.add(STRING_DIFERENTES);
		CATEGORIZACAO_PADRAO = new CategorizacaoPorFaixas(categorias, corte);
	}
	
	public static final BiPredicate<Elemento, Elemento> COMPARADOR_ELEMENTO_PADRAO = new IgualdadePrimeiroCampo();
	
	//COnstantes relativas ao arquivo de saida
	public static final String NOME_COLUNA_CERTEZA_CLASSIFICACAO = "Certeza";
	public static final String TIPO_COLUNA_CERTEZA_CLASSIFICACAO = "numero";
	public static final String NOME_COLUNA_CATEGORIA_CLASSIFICACAO = "Categoria";
	public static final String TIPO_COLUNA_CATEGORIA_CLASSIFICACAO = "string";
	
	public static final Extrator EXTRATOR_PADRAO = new ExtratorBinario();
	public static final Regressao REGRESSAO_PADRAO = new RegressaoLogistica();
	
	public static final String DELIMITADOR_CAMPOS = "\";\""; //";" -> mais seguro que so usar ;
	public static final String ASPAS = "\"";
}
