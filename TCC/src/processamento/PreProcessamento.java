package processamento;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import modelo.Elemento;

public class PreProcessamento {
	private Map<String, String> mapaSubstituicao;
	private Collection<String> stopwords;
	
	public Map<String, String> getMapaSubstituicao() {
		return mapaSubstituicao;
	}

	public void setMapaSubstituicao(Map<String, String> mapaSubstituicao) {
		this.mapaSubstituicao = mapaSubstituicao;
	}

	public Collection<String> getStopwords() {
		return stopwords;
	}

	public void setStopwords(Collection<String> stopwords) {
		this.stopwords = stopwords;
	}

	public PreProcessamento() {
		mapaSubstituicao = new HashMap<String, String>();
		mapaSubstituicao.put("jr", "junior");
		
		stopwords = new ArrayList<String>();
		stopwords.addAll(Arrays.asList(new String[]{"de", "do", "dos", "da", "das"}));
	}
	
	public String substituicao(String s, Map<String, String> mapa){
		String resultado = s;
		for (String palavra: mapa.keySet()){
			String substituicao = mapa.get(palavra);
			resultado = resultado.replaceAll("\\b"+ palavra +"\\b", substituicao);
		}
		return resultado;
	}
	
	public String removeStopWords(String s, Collection<String> stopWords){
		String resultado = s;
		for (String palavra: stopWords){
			resultado = resultado.replaceAll("\\b"+ palavra +"\\b", "");
		}
		return resultado;
	}
	
	public String processaNome(String s){
		String resultado = s.replaceAll("[^a-z ]", "");
		return resultado;
	}
	public String processaData(String s){
		//TODO tentar melhorar e talvez inferir data
		String resultado = s.replaceAll("[^0-9]", "");
		return resultado;
	}
	public String processaString(String s){
		String resultado = s.replaceAll("[^a-z0-9 ]", "");
		return resultado;
	}
	
	public String normaliza(String s){
		//TODO remover numeros (ou nao)
		String resultado = Normalizer.normalize(s, Normalizer.Form.NFD);
		resultado = resultado.toLowerCase();
		resultado = resultado.replaceAll("[ ]+", " ");
		resultado = resultado.trim();
		return resultado;
	}
	public Elemento processa(Elemento e){
		//TODO adicionar mais tipos de elemento
		Elemento novo = (Elemento) e.clone();
		for (int i=0; i < e.tamanho(); i++){
			String elem = e.getElemento(i);
			String tipo = e.getTipoDado(i);
			
			String novoElem = normaliza(elem);
			if (tipo.equals("data")){
				novoElem = processaData(novoElem);
			}
			else if (tipo.equals("nome")){
				novoElem = processaNome(novoElem);
			}
			else{
				novoElem = processaString(novoElem);
			}
			
			novo.addElemento(i, novoElem);
		}
		return novo;
	}
}
