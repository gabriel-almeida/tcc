package modelo;

import java.util.ArrayList;
import java.util.List;

import org.jblas.DoubleMatrix;

public class ConjuntoDados {
	List<ArrayList<Double>> matriz = new ArrayList<ArrayList<Double>>();
	List<Double> respostas = new ArrayList<Double>();

	public void adicionaAmostra(ArrayList<Double> amostra){
		this.matriz.add(amostra);
		this.respostas.add(null);
	}

	public ArrayList<Double> getAmostra(int i){
		return this.matriz.get(i);

	}
	public double getRespostaEsperada(int i){
		return this.respostas.get(i);
	}
	public void setResposta(int i, double resposta){
		this.respostas.set(i, resposta);
	}
	public int tamanho(){
		return this.matriz.size();
	}
	public DoubleMatrix getTreino(){
		if (this.matriz.size() == 0){
			return new DoubleMatrix();
		}
		DoubleMatrix treino = new DoubleMatrix(this.matriz.get(0)).transpose();
		for (int i = 1; i < this.matriz.size(); i++ ){
			DoubleMatrix linha = new DoubleMatrix(this.matriz.get(i)).transpose();
			treino = DoubleMatrix.concatVertically(treino, linha);
		}
		return treino;
	}

	public DoubleMatrix getVetorResposta(){
		List<Double> respostasExistentes = new ArrayList<Double>();
		for (Double resposta: this.respostas){
			if (resposta != null){
				respostasExistentes.add(resposta);
			}
		}
		return new DoubleMatrix(respostasExistentes);
	}
}
