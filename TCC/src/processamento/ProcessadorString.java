package processamento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessadorString implements Processador {
	private Map<String, String> mapaSubstituicao;
	private List<String> stopwords;
	
	public ProcessadorString(Map<String, String> mapaSubstituicao,
			List<String> stopwords) {
		this.mapaSubstituicao = mapaSubstituicao;
		this.stopwords = stopwords;
	}
	
	public ProcessadorString() {
		this(new HashMap<String, String>(), new ArrayList<String>());
	}
	@Override
	public String processa(String s) {
		String resultado = s.replaceAll("[^a-z0-9 ]", "");
		resultado = UtilidadesPreProcessamento.removeStopWords(resultado, stopwords);
		resultado = UtilidadesPreProcessamento.substituicao(resultado, mapaSubstituicao);
		
		return resultado;
	}
}
