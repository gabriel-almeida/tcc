package utilidades;

import extracaoFeatures.Extrator;
import extracaoFeatures.ExtratorBinario;

public abstract class Constantes {
	public static final double valorPositivo = 1.0;
	public static final double valorNegativo = 0.0;
	public static final double LimiarPadrao = 0.5;
	public static final String stringIguais = "IGUAIS";
	public static final String stringDiferentes = "DIFERENTES";
	public static final String nomeColunaClassificacao = "Veredito";
	public static final Extrator EXTRATOR_PADRAO = new ExtratorBinario();
	
	public static final String DELIMITADOR_CAMPOS = "\";\""; //";" -> mais seguro que so usar ;
	public static final String ASPAS = "\"";
}
