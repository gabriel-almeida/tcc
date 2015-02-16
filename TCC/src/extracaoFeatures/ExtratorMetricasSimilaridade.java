package extracaoFeatures;

import java.util.ArrayList;

import utilidades.MetricasSimilaridade;

public class ExtratorMetricasSimilaridade implements Extrator {

	/**
	 * Extrator que retorna os valores das metricas de jaro winkler e de monge elkan para
	 * as strings. Retorna zero se um deles for vazio.
	 * */
	@Override
	public ArrayList<Double> extrai(String conteudo1, String conteudo2) {
		ArrayList<Double> resultado = new ArrayList<Double>();
		double jw = 0.0;
		double me = 0.0;
		if (!conteudo1.equals("") && !conteudo2.equals("")){
			jw = MetricasSimilaridade.JaroWinkler(conteudo1, conteudo2);
			me = MetricasSimilaridade.MongeElkan(conteudo1, conteudo2);	
		}
		resultado.add(jw);
		resultado.add(me);
		return resultado;
	}
}
