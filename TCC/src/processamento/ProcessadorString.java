package processamento;

import java.util.List;
import java.util.Map;

public class ProcessadorString implements Processador {
	private Map<String, String> mapaSubstituicao;
	private List<String> stopwords;
	private String regexpRemocao;
	private int tamanhoMaximo;
	
	public void setTamanhoMaximo(int tamanhoMaximo) {
		this.tamanhoMaximo = tamanhoMaximo;
	}

	public ProcessadorString(Map<String, String> mapaSubstituicao,
			List<String> stopwords, String regexpRemocao) {
		this.mapaSubstituicao = mapaSubstituicao;
		this.stopwords = stopwords;
		this.regexpRemocao = regexpRemocao;
		this.tamanhoMaximo = 0;
	}
	
	/**
	 * Ordem do processamento padrao: Remocao de caracteres invalidos com a regexp, reducao da string, substituicoes de palavras chave e remocao de stopwords.
	 * */
	@Override
	public String processa(String s) {
		String resultado = s.replaceAll(regexpRemocao, "");
		
		if (this.tamanhoMaximo > 0 && resultado.length() > tamanhoMaximo){
			resultado = resultado.substring(0, tamanhoMaximo);
		}
		resultado = UtilidadesPreProcessamento.substituicao(resultado, mapaSubstituicao);
		resultado = UtilidadesPreProcessamento.removeStopWords(resultado, stopwords);
		
		return resultado;
	}
}
