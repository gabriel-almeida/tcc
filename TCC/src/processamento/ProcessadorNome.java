package processamento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessadorNome extends ProcessadorString {
	private static Map<String, String> mapaSubstituicao;
	private static List<String> stopwords;
	
	//TODO parece feio, melhorar
	static {
		mapaSubstituicao = new HashMap<String, String>();
		mapaSubstituicao.put("jr", "junior");
		
		stopwords = new ArrayList<String>();
		stopwords.addAll(Arrays.asList(new String[]{"de", "do", "dos", "da", "das"}));
	}
	
	public ProcessadorNome() {
		super(mapaSubstituicao, stopwords);
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
