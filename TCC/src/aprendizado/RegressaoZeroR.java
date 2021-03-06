package aprendizado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.ConjuntoDados;
import utilidades.Matriz;

public class RegressaoZeroR implements Regressao {
	private double resposta;
	
	@Override
	public void treina(ConjuntoDados conjDados, List<Integer> indices) {
		this.resposta = Matriz.geraVetorResposta(indices, conjDados).mean();
	}

	@Override
	public List<Double> classifica(ConjuntoDados conjDados,
			List<Integer> indicesTeste) {
		return new ArrayList<Double>(Collections.nCopies(indicesTeste.size(), resposta));
	}
	
	@Override
	public Object clone(){
		RegressaoZeroR novaRegressao = new RegressaoZeroR();
		novaRegressao.resposta = this.resposta;
		return novaRegressao;
	}

	@Override
	public double classifica(List<Double> features) {
		return resposta;
	}
}
