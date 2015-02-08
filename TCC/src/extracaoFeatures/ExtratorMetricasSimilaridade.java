package extracaoFeatures;

import java.util.ArrayList;

import utilidades.MetricasSimilaridade;

public class ExtratorMetricasSimilaridade implements Extrator {

	@Override
	public ArrayList<Double> extrai(String conteudo1, String conteudo2) {
		ArrayList<Double> resultado = new ArrayList<Double>();
		
		if (conteudo1.equals("") || conteudo2.equals("")){
			resultado.add(0.0);
			resultado.add(0.0);
		}
		else{
			double jw = MetricasSimilaridade.JaroWinkler(conteudo1, conteudo2);
			double me = MetricasSimilaridade.MongeElkan(conteudo1, conteudo2);
			resultado.add(jw);
			resultado.add(me);
		}
		return resultado;
	}

}
