package desduplicacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConjuntoDisjunto<T> {
	private Map<T, T> parente = new HashMap<T, T>();
	private Map<T, Integer> rank = new HashMap<T, Integer>();

	public void adicionaElemento(T x){
		if (!parente.containsKey(x)){
			parente.put(x, x);
			rank.put(x, 0);
		}
	}
	public void uniao(T x, T y){
		T raizX = busca(x);
		T raizY = busca(y);

		if (raizX.equals(raizY)){
			return;
		}
		Integer rankX = rank.get(raizX);
		Integer rankY = rank.get(raizY);
		if (rankX < rankY){
			parente.put(raizX, raizY);
		} else if ( rankX > rankY ){
			parente.put(raizY, raizX);
		} else {
			parente.put(raizY, raizX);
			rank.put(raizX, rankX + 1);
		}
	}
	public T busca(T x){
		T pai = parente.get(x);
		if ( !pai.equals(x)){
			pai = busca(pai);
			parente.put(x, pai);
		}
		return pai;
	}
	void adicionaConjuntos(Collection<List<T>> elementos){
		for ( Collection<T> lista : elementos){
			T primeiro = null;
			for (T el : lista){
				adicionaElemento(el);
				if (primeiro == null){
					primeiro = el;
				}
				else{
					uniao(primeiro, el);
				}
			}
		}
	}
	public Collection<Set<T>> getConjuntos(){
		Map<T, Set<T>> conjuntos = new HashMap<T, Set<T>>();
		for ( T x : this.parente.keySet()){
			T raizX = busca(x);
			Set<T> conjAtual = conjuntos.getOrDefault(raizX, new HashSet<T>());
			conjAtual.add(x);
			conjuntos.put(raizX, conjAtual);
		}
		return conjuntos.values();
	}
}
