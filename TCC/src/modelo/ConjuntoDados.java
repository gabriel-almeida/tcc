package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConjuntoDados {
	List<ArrayList<Double>> matriz = new ArrayList<ArrayList<Double>>();
	Map<Integer, Double> respostas = new HashMap<Integer, Double>();

	public void adicionaAmostra(ArrayList<Double> amostra){
		this.matriz.add(amostra);
	}

	public ArrayList<Double> getAmostra(int i){
		return this.matriz.get(i);

	}
	public Double getRespostaEsperada(int i){
		return this.respostas.get(i);
	}
	public void setResposta(int i, double resposta){
		this.respostas.put(i, resposta);
	}
	
	public int tamanho(){
		return this.matriz.size();
	}
	public List<Integer> getIndiceRespostasExistentes(){
		return new ArrayList<Integer>(respostas.keySet());
	}
}
