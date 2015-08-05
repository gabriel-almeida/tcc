package extracaoFeatures;

import java.util.ArrayList;

import utilidades.MetricasSimilaridade;

public class ExtratorLevenstein implements Extrator {

	@Override
	public ArrayList<Double> extrai(String conteudo1, String conteudo2) {
		double lev = MetricasSimilaridade.LevensteinNormalizado(conteudo1, conteudo2);
		ArrayList<Double> res = new ArrayList<Double>();
		res.add(lev);
		return res;
	}

}
