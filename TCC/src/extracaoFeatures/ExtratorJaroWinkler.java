package extracaoFeatures;

import java.util.ArrayList;

import utilidades.MetricasSimilaridade;

public class ExtratorJaroWinkler implements Extrator {

	@Override
	public ArrayList<Double> extrai(String conteudo1, String conteudo2) {
		ArrayList<Double> resultado = new ArrayList<Double>();
		double valor = MetricasSimilaridade.JaroWinkler(conteudo1, conteudo2);
		resultado.add(valor);
		return resultado;
	}

}
