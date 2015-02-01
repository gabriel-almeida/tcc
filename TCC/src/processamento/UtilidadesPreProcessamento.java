package processamento;

import java.util.Collection;
import java.util.Map;

public abstract class UtilidadesPreProcessamento {
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
}
