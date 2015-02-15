package aprendizado;

import java.util.ArrayList;
import java.util.function.BiFunction;

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
	
	public BiFunction<Elemento, Elemento,Integer> geraFuncaoMetricaSimilaridade(int quantBlocos){
		BiFunction<Elemento, Elemento, Integer> funcao = new BiFunction<Elemento, Elemento, Integer>() {
			@Override
			public Integer apply(Elemento t, Elemento u) {
				double valor = medidaSimilaridade(t, u);
				valor = valor > 1.0? 1.0: valor;
				valor = valor < 0.0? 0.0: valor;
				valor = (1.0 - valor)*(quantBlocos - 1); //quero que 0 seja o mais similar possivel e 'quantBlocos - 1' o menos similar
				return (int) Math.floor(valor);
			}
		};
		return funcao;
	}
}
