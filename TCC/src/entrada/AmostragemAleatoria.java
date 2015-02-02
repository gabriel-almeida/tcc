package entrada;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import modelo.ConjuntoDados;

public class AmostragemAleatoria {
	private ConjuntoDados conjuntoDados;
	private Random rand;
	
	public AmostragemAleatoria(ConjuntoDados conjuntoDados, Random rand) {
		super();
		this.conjuntoDados = conjuntoDados;
		this.rand = rand;
	}
	public AmostragemAleatoria(ConjuntoDados conjuntoDados) {
		super();
		this.conjuntoDados = conjuntoDados;
		this.rand = new Random();
	}
	
	public List<Integer> amostra(int quantidade){
		//TODO pode ser necessario melhorias em eficiencia
		List<Integer> indicesAmostra = new ArrayList<Integer>();
		int indiceMax = conjuntoDados.tamanho();
		for (int i=0; i< quantidade; i++){
			int amostra = rand.nextInt(indiceMax);
			if (indicesAmostra.contains(amostra)){
				i--;
			}
			else{
				indicesAmostra.add(amostra);	
			}
			
		}
		return indicesAmostra;
	}
}
