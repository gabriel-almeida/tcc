package avaliacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.ConjuntoDados;

import org.jblas.DoubleMatrix;

import utilidades.Matriz;
import aprendizado.Regressao;
import aprendizado.RegressaoLinear;

public class ValidacaoCruzada {
	private Regressao regressao;
	private ConjuntoDados conjDados;
	private List<Integer> indicesTreino;
	private List<Integer> indicesTeste;
	
	public ValidacaoCruzada(Regressao regressao, ConjuntoDados conjDados, double porcentagemTeste) {
		this.indicesTeste = new ArrayList<Integer>();
		this.regressao = regressao;
		this.conjDados = conjDados;
		
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
	
	public Avaliador avalia(){
		regressao.treina(conjDados, indicesTreino);
		List<Double> recebido  = regressao.classifica(conjDados, indicesTeste);
		List<Double> esperado = conjDados.geraListaResposta(indicesTeste); 
		
		return new Avaliador(esperado, recebido);
	}
	
	
}
