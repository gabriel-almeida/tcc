package extracaoFeatures;

import java.util.ArrayList;

import utilidades.MetricasSimilaridade;

public class ExtratorData implements Extrator{

	@Override
	public ArrayList<Double> extrai(String conteudo1, String conteudo2) {
		ArrayList<Double> resultado = new ArrayList<Double>();
//		double valor = 0.0;
		
//		if (!conteudo1.equals("") && !conteudo2.equals("")){
//			valor = MetricasSimilaridade.Levenstein(conteudo1, conteudo2);
//		}
		double valor = conteudo1.equals(conteudo2)? 1.0: 0.0;
		resultado.add(valor);

		return resultado;
	}

}
