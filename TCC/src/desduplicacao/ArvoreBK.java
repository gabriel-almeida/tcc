package desduplicacao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import utilidades.AnalisePerformace;

public class ArvoreBK<T> {
	private Node<T> raiz;
	private BiFunction<T, T, Integer> metrica;
	private boolean permitirDuplicata;
	
	//Estatisticas
	private int profundidade;
	private int numeroNos;
	
	public ArvoreBK(BiFunction<T, T, Integer> metrica){
		this(metrica, true);
	}
	/**
	 * Arvore BK eh uma arvore usada durante o processo de fuzzy matching, retornando objetos similares de acordo com uma metrica e uma
	 * margem de tolerancia durante sua busca. Sua estrutura interna eh similar a de uma Arvore B porem a busca eh diferenciada.
	 * A metrica deve seguir a inequacao do triangulo para ser efetiva. Elementos com distancia iguais a zero sao considerados iguais.
	 * Note que a estrutura provavelmente sofrera de desbalaceamento, podendo se tornar lenta. Essa estrura nao eh sincronizada.
	 * */
	public ArvoreBK(BiFunction<T, T, Integer> metrica, boolean permitirDuplicata){
		this.metrica = metrica;
		this.permitirDuplicata = permitirDuplicata;
	}
	
	/**
	 * Varre a estrutura no formato "Pre Ordem", executando a funcao consumidora 
	 * */
	public void preOrdem(Consumer<T> funcao){
		preOrdemF(funcao, raiz);
	}
	private void preOrdemF(Consumer<T> funcao, Node<T> elemento){
		for (int i: elemento.getChaves()){
			funcao.accept(elemento.getElemento());
			preOrdemF(funcao, elemento.getFilho(i));
		}
	}
	/**
	 * Varre a estrutura no formato "Em Ordem", executando a funcao consumidora 
	 * */
	public void emOrdem(Consumer<T> funcao){
		emOrdemF(funcao, raiz);
	}
	private void emOrdemF(Consumer<T> funcao, Node<T> elemento){
		for (int i: elemento.getChaves()){
			emOrdemF(funcao, elemento.getFilho(i));
			funcao.accept(elemento.getElemento());
		}
	}
	/**
	 * Varre a estrutura no formato "Pos Ordem", executando a funcao consumidora 
	 * */
	public void posOrdem(Consumer<T> funcao){
		posOrdemF(funcao, raiz);
	}
	private void posOrdemF(Consumer<T> funcao, Node<T> elemento){
		for (int i: elemento.getChaves()){
			posOrdemF(funcao, elemento.getFilho(i));
		}
		funcao.accept(elemento.getElemento());
	}
	
	public int getProfundidade(){
		return this.profundidade;
	}
	public int getNumeroNos(){
		return this.numeroNos;
	}
	
	private double media;
	public double media(){
		media = 0.0;
		AnalisePerformace tempo = new AnalisePerformace();
		
		transversal(node -> media += node.getChaves().size(), raiz);
		
		tempo.capturaTempo(getNumeroNos());
		tempo.imprimeEstatistica("Transversal Arvore B");
		
		return media/numeroNos;
	}
	private void transversal(Consumer<Node<T>> funcao, Node<T> elemento){
		
		for (int i: elemento.getChaves()){
			funcao.accept(elemento);
			transversal(funcao, elemento.getFilho(i));
		}
	}
	/**
	 * Adiciona elemento individuais na estrutura. Nao eh sincronizado
	 * */
	public void adicionaElemento(T novoElemento)
	{
		if (raiz == null)
		{
			raiz = new Node<T>(novoElemento);
			return;
		}

		Node<T> noAtual = raiz;

		int dist = metrica.apply(novoElemento, noAtual.getElemento());
		int profundidadeAtual = 0;
		while (noAtual.temFilho(dist))
		{
			if (dist == 0 && !permitirDuplicata) return;

			noAtual = noAtual.getFilho(dist);
			dist = metrica.apply(noAtual.getElemento(), novoElemento);
			profundidadeAtual++;
		}
		noAtual.adicionaFilho(dist,novoElemento);
		
		this.profundidade = Math.max(profundidadeAtual, this.profundidade);
		this.numeroNos++;
	}
	/**
	 * Adiciona uma colecao de elemento a arvore em paralelo, onde o primeiro elemento vira a raiz da arvore, caso necessario.
	 * Retorna a profundidade maxima obtida durante a insercao
	 * */
	public void adicionaElementos (Collection<T> novos){
		//Log Tempo
		AnalisePerformace tempo = new AnalisePerformace();
		
		int haRaiz = 0;
		if (raiz == null){
			haRaiz = 1;
		}
		this.numeroNos += novos.size();
		novos.stream().limit(haRaiz).forEach(e -> raiz = new Node<T>(e));		
		int profundidadeAtual = novos.stream().skip(haRaiz).parallel().mapToInt( e -> this.adicionaElementoParalelo(e)).max().orElse(0);
		this.profundidade = Math.max(profundidadeAtual, profundidade);
		
		//LOG tempo
		tempo.capturaTempo(novos.size());
		tempo.imprimeEstatistica("Insersao arvore BK");
	}
	private int adicionaElementoParalelo(T novoElemento)
	{	
		Node<T> noAtual = raiz;
		int profundidade = 0;
		while(true){
			int dist = this.metrica.apply(noAtual.getElemento(), novoElemento);
			synchronized (noAtual) {
				if ( !noAtual.temFilho(dist) ){
					noAtual.adicionaFilho(dist,novoElemento);
					break;
				} 
				else{
					noAtual = noAtual.getFilho(dist);
				}
			}
			profundidade++;
		}
		return profundidade;
	}
	
	/**
	 * Realiza uma busca nos elementos da arvore que deve retornar 
	 * uma lista com todos os elementos dentro da margem de tolerancia indicada. 
	 * A busca eh feita de forma paralela.
	 * */
	
	public List<T> busca(T elementoBuscado, int margemTolerancia)
	{
		List<T> resultado = Collections.synchronizedList(new ArrayList<T>());
		buscaRecursiva(this.raiz, resultado, elementoBuscado, margemTolerancia);

		return resultado;
	}

	private void buscaRecursiva(Node<T> noAtual, List<T> resultado, T elementoBuscado, int margemTolerancia )
	{
		int distanciaAtual = this.metrica.apply(noAtual.getElemento(), elementoBuscado);
		int minDist = distanciaAtual - margemTolerancia;
		int maxDist = distanciaAtual + margemTolerancia;

		if (distanciaAtual <= margemTolerancia){
			resultado.add(noAtual.getElemento());
		}

		noAtual.getChaves().parallelStream().filter(i -> minDist <= i && i <= maxDist).forEach(chave -> buscaRecursiva(noAtual.getFilho(chave), resultado, elementoBuscado, margemTolerancia));
	}
}
