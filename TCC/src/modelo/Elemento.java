package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Elemento {
	private List<String> elementos;
	private List<String> descritores;
	private List<String> tiposDado;
	private String chave;

	public Elemento(String chave, List<String> descritores, List<String> tipoDados) {
		if (descritores.size() != tipoDados.size()){
			throw new RuntimeException("Descritores e tipos de dados devem ter o mesmo tamanho");
		}

		this.chave = chave;
		this.descritores = descritores;
		this.tiposDado = tipoDados;

		List<String> listaNull = Collections.nCopies(descritores.size(), null);
		this.elementos = new ArrayList<String>(listaNull);
	}
	/**
	 * Considero que o tipo padrao de todas as colunas eh String
	 * */
	public Elemento(String chave, List<String> descritores) {
		this(chave, descritores, Collections.nCopies(descritores.size(), "string"));
	}

	@Override
	public Object clone(){
		Elemento clone =  new Elemento(chave, descritores, tiposDado);
		Collections.copy(clone.elementos, this.elementos);
		return clone;
	}
	public String getChave() {
		return chave;
	}

	//Get Elementos	
	public String getElemento(int i){
		return this.elementos.get(i);
	}
	public String getElemento(String coluna){
		int i = getIndice(coluna);
		return getElemento(i);
	}
	//Adicao Elementos
	public void addElemento(int indice, String s){
		this.elementos.set(indice, s);
	}
	public void addElemento(String coluna, String s){
		int i = getIndice(coluna);
		addElemento(i, s);
	}

	//Get tipos
	public String getTipoDado(String coluna) {
		int i = getIndice(coluna);
		return getTipoDado(i);
	}
	public String getTipoDado(int indice) {
		return this.tiposDado.get(indice);
	}

	public int tamanho(){
		return this.elementos.size();
	}
	public List<String> getDescritores(){
		return this.descritores;
	}

	/**
	 * Este metodo auxiliar pega a coluna relativa na lista de descritores e retornar o indice.
	 * Complexidade O(numero de elementos)
	 * Gera uma excessao caso necessario 
	 * */
	private int getIndice(String coluna){
		int i = this.descritores.indexOf(coluna);
		if (i == -1){
			throw new RuntimeException("Coluna " + coluna + " inexistente.");
		}
		return i;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("chave=");
		sb.append(this.chave);
		sb.append(", ");
		for (int i=0; i < this.tamanho(); i++){
			String colunaAtual = this.descritores.get(i);
			sb.append(colunaAtual);
			sb.append("=");
			sb.append(this.getElemento(colunaAtual).trim());
			sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}
}
