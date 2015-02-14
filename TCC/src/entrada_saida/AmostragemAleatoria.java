package entrada_saida;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import modelo.ConjuntoDados;

public class AmostragemAleatoria {
	private Random rand;
	
	public AmostragemAleatoria(Random rand) {
		this.rand = rand;
	}
	
	public AmostragemAleatoria(){
		this.rand = new Random();
	}
	
	/** 
	 * Retorna uma lista contendo indices aleatorios do conjunto de dados (passado via construtor), que ainda nao possuem respostas. 
	 * Complexidade: O(tamanho amostra).
	 * */
	public List<Integer> amostra(int quantidade, ConjuntoDados conjuntoDados){
		List<Integer> indicesAmostra = new ArrayList<Integer>();
		int indiceMax= conjuntoDados.tamanho();
		
		List<Integer> candidatos = new ArrayList<Integer>();
		for (int i=0; i< indiceMax; i++){
			if (conjuntoDados.getRespostaEsperada(i) == null){
				candidatos.add(i);
			}
		}
		
		if (quantidade > candidatos.size()){
			throw new RuntimeException("Amostragem nao possivel: " + candidatos.size() + " candidatos possiveis e " + quantidade + " amostras pedidas." );
		}
		
		for (int i=0; i< quantidade; i++){
			int quantCandidatos = candidatos.size();
			int indiceEscolhido = this.rand.nextInt(quantCandidatos);
			int candidatoEscolhido = candidatos.get(indiceEscolhido);
			candidatos.remove(indiceEscolhido);
			indicesAmostra.add(candidatoEscolhido);
		}
		return indicesAmostra;
	}
}
