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
		this.regressao = regressao;
		this.conjDados = conjDados;
		int tamanhoDataset = conjDados.tamanho();
		int tamanhoTeste = (int) Math.round( tamanhoDataset * porcentagemTeste);
		
		this.indicesTreino = conjDados.getIndiceRespostasExistentes();
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
		regressao.treina(matrizTreino, matrizTargetTreino);
		
		DoubleMatrix matrizTeste = geraMatriz(indicesTeste);
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
			respostas.add(1.0 * i);
		}
		return respostas;
	}
	private DoubleMatrix geraVetorResposta(List<Integer> indices){
		List<Double> respostas = geraListaResposta(indices);
		return new DoubleMatrix(respostas).transpose();
	}

}
