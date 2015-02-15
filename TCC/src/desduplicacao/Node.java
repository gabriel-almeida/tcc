package desduplicacao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Node <T>{
	private T elemento;
	private Map<Integer, Node<T>> filhos;
	public Node(T elemento) {
		this.elemento = elemento;
		this.filhos =  new HashMap<Integer, Node<T>>();
	}

	public T getElemento() {
		return elemento;
	}

	public boolean temFilho(int dist) {
		return filhos.containsKey(dist);
	}

	public Node<T> getFilho(int dist) {
		return filhos.get(dist);
	}

	public void adicionaFilho(int dist, T novoElemento) {
		this.filhos.put(dist, new Node<T>(novoElemento));
	}

	public Collection<Integer> getChaves() {
		return filhos.keySet();
	}
	public String toString(){
		return this.elemento.toString();
	}
	
}
