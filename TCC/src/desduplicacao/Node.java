package desduplicacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import modelo.Elemento;

public class Node {
	private Elemento elemento;
	private Map<Integer, Node> filhos;
	public Node(Elemento elemento) {
		this.elemento = elemento;
		this.filhos = new ConcurrentHashMap<Integer, Node>();
	}

	public Elemento getElemento() {
		return elemento;
	}

	public boolean temFilho(int dist) {
		return filhos.containsKey(dist);
	}

	public Node getFilho(int dist) {
		return filhos.get(dist);
	}

	public void adicionaFilho(int dist, Elemento novoElemento) {
		this.filhos.put(dist, new Node(novoElemento));
	}

	public List<Integer> getChaves() {
		return new ArrayList<Integer>(filhos.keySet());
	}
	public String toString(){
		return this.elemento.toString();
	}
	
}
