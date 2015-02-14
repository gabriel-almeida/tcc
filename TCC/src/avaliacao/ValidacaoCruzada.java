package avaliacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.ConjuntoDados;
import aprendizado.Regressao;

public class ValidacaoCruzada {
	private Regressao regressao;
	private ConjuntoDados conjDados;
	private List<Integer> indicesTreino;
	private List<Integer> indicesTeste;
	private double porcentagemTeste;
	
	public ValidacaoCruzada(Regressao regressao, ConjuntoDados conjDados, double porcentagemTeste) {
		this.regressao = regressao;
		this.conjDados = conjDados;
		this.porcentagemTeste = porcentagemTeste;
	}
	/**
	 * Inicializa estruturas necessaria para avaliacao, 
	 * escolhendo aleatoriamente os conjuntos de treino e teste
	 * */
	private void init(){
		this.indicesTeste = new ArrayList<Integer>();
		this.indicesTreino = conjDados.getIndiceRespostasExistentes();
		
		int tamanhoDataset = this.indicesTreino.size();
		int tamanhoTeste = (int) Math.round( tamanhoDataset * porcentagemTeste);
		
		Collections.shuffle(indicesTreino);
		
		for (int i=0; i < tamanhoTeste; i++){
			int indice = this.indicesTreino.get(0);
			this.indicesTreino.remove(0); //equivalente a um .pop()
			
			this.indicesTeste.add(indice);
		}
	}
	/**
	 * A cada chamada dessa funcao, um novo conjunto de teste e treino sao sorteados
	 * e o algoritmo eh treinado a avaliado com eles.
	 * Retorna um objeto Avaliador que contem a matriz de confusao dos resultados 
	 * usando o conjunto de teste. 
	 * */
	public Avaliador avalia(){
		init();
		regressao.treina(conjDados, indicesTreino);
		List<Double> recebido  = regressao.classifica(conjDados, indicesTeste);
		List<Double> esperado = conjDados.geraListaResposta(indicesTeste); 
		
		return new Avaliador(esperado, recebido);
	}
}
