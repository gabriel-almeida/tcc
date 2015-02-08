package processamento;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import modelo.Elemento;

public class PreProcessamento {
	private Map<String, Processador> tratamentoPorTipo;
	
	public void setTratamento(String tipo, Processador p){
		this.tratamentoPorTipo.put(tipo, p);
	}
	public PreProcessamento() {
		tratamentoPorTipo = new HashMap<String, Processador>();
		tratamentoPorTipo.put("string", new ProcessadorString());
		tratamentoPorTipo.put("data", new ProcessadorData());
		tratamentoPorTipo.put("nome", new ProcessadorNome());
	}
	
	public String normaliza(String s){
		String resultado = Normalizer.normalize(s, Normalizer.Form.NFD);
		resultado = resultado.toLowerCase();
		resultado = resultado.replaceAll("[ ]+", " ");
		resultado = resultado.trim();
		return resultado;
	}
	
	public Elemento processa(Elemento e){
		Elemento novo = (Elemento) e.clone();
		for (int i=0; i < e.tamanho(); i++){
			String elem = e.getElemento(i);
			String tipo = e.getTipoDado(i);
			
			String novoElem = normaliza(elem);
			
			Processador p = this.tratamentoPorTipo.get(tipo);
			if (p == null){
				throw new RuntimeException("Nao foi especificado como se trata o tipo " + tipo);
			}
			novoElem = p.processa(novoElem);
			
			novo.addElemento(i, novoElem);
		}
		return novo;
	}
}
