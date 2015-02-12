package extracaoFeatures;

import java.util.ArrayList;

import utilidades.MetricasSimilaridade;

public class ExtratorMetricasSimilaridade implements Extrator {

	@Override
	public ArrayList<Double> extrai(String conteudo1, String conteudo2) {
		ArrayList<Double> resultado = new ArrayList<Double>();
		double jw = 0.0;
		double me = 0.0;
		double l = 0.0;
		if (!conteudo1.equals("") && !conteudo2.equals("")){
			jw = MetricasSimilaridade.JaroWinkler(conteudo1, conteudo2);
			me = MetricasSimilaridade.MongeElkan(conteudo1, conteudo2);
			l = MetricasSimilaridade.Levenstein(conteudo1, conteudo2);	
		}
		resultado.add(jw);
		resultado.add(me);
		resultado.add(l);
		return resultado;
	}
}
