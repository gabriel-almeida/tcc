package desduplicacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import utilidades.AnalisePerformace;
import modelo.Elemento;
import aprendizado.MetricaRegressao;

public class ArvoreBK {
	private Node raiz;
	private BiFunction<Elemento, Elemento, Integer> metrica;
	private int i = 0;
	
	public ArvoreBK(MetricaRegressao metrica, int quantBlocos) {
		this.metrica = (a,b) -> (int) Math.floor(quantBlocos*(1.0 - Math.max(Math.min(metrica.medidaSimilaridade(a, b), 1.0),0.0)));
	}
	
	public void adicionaElemento(Elemento novoElemento)
	{
		i++;
		
		if (raiz == null)
		{
			raiz = new Node(novoElemento);
			return;
		}

		Node noAtual = raiz;

		int dist = metrica.apply(novoElemento, noAtual.getElemento());
		while (noAtual.temFilho(dist))
		{
			//if (dist == 0) return;

			noAtual = noAtual.getFilho(dist);
			dist = metrica.apply(noAtual.getElemento(), novoElemento);
		}

		noAtual.adicionaFilho(dist,novoElemento);
		
		
		if (i % 100 == 0){
			AnalisePerformace.capturaTempo(i);
			AnalisePerformace.imprimeEstatistica("Arvore BK");
		}
	}

	public List<Elemento> busca(Elemento elementoBuscado, int margemTolerancia)
	{
		List<Elemento> resultado = Collections.synchronizedList(new ArrayList<Elemento>());
		buscaRecursiva(this.raiz, resultado, elementoBuscado, margemTolerancia);

		return resultado;
	}

	private void buscaRecursiva(Node noAtual, List<Elemento> resultado, Elemento elementoBuscado, int margemTolerancia )
	{
		int distanciaAtual = this.metrica.apply(noAtual.getElemento(), elementoBuscado);
		int minDist = distanciaAtual - margemTolerancia;
		int maxDist = distanciaAtual + margemTolerancia;

		if (distanciaAtual <= margemTolerancia){
			resultado.add(noAtual.getElemento());
		}
		List<Integer> chavesExistentes =  noAtual.getChaves();
		chavesExistentes.stream().parallel().filter(i -> minDist <= i && i <= maxDist).forEach(chave -> buscaRecursiva(noAtual.getFilho(chave), resultado, elementoBuscado, margemTolerancia));
	}
}
