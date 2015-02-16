package extracaoFeatures;

import java.util.ArrayList;

public class ExtratorBinario implements Extrator{

	/**
	 * Extrator que retorna 1 se os elementos sao iguais ou 
	 * zero caso os elementos sejam diferentes ou um deles seja nulos
	 *  */
	@Override
	public ArrayList<Double> extrai(String conteudo1, String conteudo2){
		double valor = conteudo1.equals("") || conteudo2.equals("") || !conteudo1.equals(conteudo2)? 0.0: 1.0;
		ArrayList<Double> resultado = new ArrayList<Double>();
		resultado.add(valor);

		return resultado;
	}

}
