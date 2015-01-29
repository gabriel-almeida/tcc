package modelo;

import java.util.ArrayList;
import java.util.List;

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
	public List<Integer> getIndiceRespostasExistentes(){
		ArrayList<Integer> indices = new ArrayList<Integer>();
		int i = 0;
		
		for (Double resposta: this.respostas){
			if (resposta != null){
				indices.add(i);
			}
			i++;
		}
		return indices;
	}
	
	public int tamanho(){
		return this.matriz.size();
	}
}
