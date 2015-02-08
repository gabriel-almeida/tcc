package avaliacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.ConjuntoDados;

import org.jblas.DoubleMatrix;

import aprendizado.Regressao;

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
		DoubleMatrix matrizTreino = geraMatriz(indicesTreino);
		DoubleMatrix matrizTargetTreino = geraVetorResposta(indicesTreino);		
		DoubleMatrix matrizTeste = geraMatriz(indicesTeste);
		
		regressao.treina(matrizTreino, matrizTargetTreino);
		DoubleMatrix resultado = regressao.classifica(matrizTeste);
		
		List<Double> esperado = geraListaResposta(indicesTeste);
		List<Double> recebido = resultado.elementsAsList(); 
		
		return new Avaliador(esperado, recebido);
	}
	
	private DoubleMatrix geraMatriz(List<Integer> indices){
		DoubleMatrix matriz = new DoubleMatrix();
		for (int i : indices){
			DoubleMatrix linha = new DoubleMatrix(this.conjDados.getAmostra(i)).transpose();
			if (matriz.columns == 0){
				matriz = linha;
			}
			else{
				matriz = DoubleMatrix.concatVertically(matriz, linha);
			}
		}
		return matriz;
	}
	private List<Double> geraListaResposta(List<Integer> indices){
		List<Double> respostas = new ArrayList<Double>();
		for (int i : indices){
			double target = this.conjDados.getRespostaEsperada(i);
			respostas.add(target);
		}
		return respostas;
	}
	private DoubleMatrix geraVetorResposta(List<Integer> indices){
		List<Double> respostas = geraListaResposta(indices);
		return new DoubleMatrix(respostas);
	}

}
