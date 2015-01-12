package processamento;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import modelo.Elemento;

public class PreProcessamento {
	public static Map<String, String> mapaSubstituicao;
	public static Collection<String> stopwords;
	
	static {
		//TODO melhorar
		mapaSubstituicao = new HashMap<String, String>();
		mapaSubstituicao.put("jr", "junior");
		
		stopwords = new ArrayList<String>();
		stopwords.addAll(Arrays.asList(new String[]{"de", "do", "dos", "da", "das"}));
	}
	
	public static String substituicao(String s, Map<String, String> mapa){
		String resultado = s;
		for (String palavra: mapa.keySet()){
			String substituicao = mapa.get(palavra);
			resultado = resultado.replaceAll("\\b"+ palavra +"\\b", substituicao);
		}
		return resultado;
	}
	
	public static String removeStopWords(String s, Collection<String> stopWords){
		String resultado = s;
		for (String palavra: stopWords){
			resultado = resultado.replaceAll("\\b"+ palavra +"\\b", "");
		}
		return resultado;
	}
	
	public static String normaliza(String s){
		//TODO remover numeros (ou nao)
		String resultado = Normalizer.normalize(s, Normalizer.Form.NFD);
		resultado = resultado.toLowerCase();
		resultado = resultado.replaceAll("[^a-z0-9]", "");
		resultado = resultado.replaceAll("[ ]+", " ");
		resultado = resultado.trim();
		return resultado;
	}
	public Elemento processa(Elemento e){
		//TODO fazer
		return null;
	}
}
