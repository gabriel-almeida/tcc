package aprendizado;

import java.util.ArrayList;

import modelo.Elemento;
import extracaoFeatures.ExtratorFeatures;

public class MetricaRegressao {
	private Regressao regressao;
	private ExtratorFeatures extrator;

	public MetricaRegressao(Regressao regressao, ExtratorFeatures extrator) {
		this.regressao = regressao;
		this.extrator = extrator;
	}
	
	public double medidaSimilaridade(Elemento e1, Elemento e2){
		ArrayList<Double> features = extrator.extrai(e1, e2);
		double resultado = regressao.classifica(features);
		return resultado;
	}
}
