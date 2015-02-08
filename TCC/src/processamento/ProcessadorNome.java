package processamento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessadorNome extends ProcessadorString {
	public static final Map<String, String> mapaSubstituicaoPadrao;
	public static final List<String> stopwordsPadrao;
	
	static {
		mapaSubstituicaoPadrao = new HashMap<String, String>();
		mapaSubstituicaoPadrao.put("jr", "junior");
		
		stopwordsPadrao = new ArrayList<String>();
		stopwordsPadrao.addAll(Arrays.asList(new String[]{"de", "do", "dos", "da", "das"}));
	}
	
	public ProcessadorNome() {
		super(mapaSubstituicaoPadrao, stopwordsPadrao);
	}
	public ProcessadorNome(Map<String, String> mapaSubstituicao,
			List<String> stopwords) {
		super(mapaSubstituicao, stopwords);
	}
	
	@Override
	public String processa(String s) {
		String resultado = super.processa(s);
		resultado = resultado.replaceAll("[^a-z ]", "");
		return resultado;
	}
}
