package avaliacao;

import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import utilidades.AnalisePerformace;

public class TesteConfianca {
	private List<Avaliador> resultados;
	private int numTestes; 
	private ValidacaoCruzada validacao;
	private double limiar;
	public static final String formatacaoIntervaloConfianca= "%f (+- %f)";
	public static final double coef = 1.96;

	public TesteConfianca(int numTestes, ValidacaoCruzada validacao, double limiar){
		this.validacao = validacao;
		this.numTestes = numTestes;
		this.limiar = limiar;
	}
	
	public void setLimiar(double limiar){
		resultados.stream().forEach( a -> a.avalia(limiar));
	}
	
	public void calculaConfianca(){
		AnalisePerformace tempo = new AnalisePerformace();
		
		resultados = IntStream.range(0, numTestes).parallel().mapToObj(i-> {
			ValidacaoCruzada novaValidacao = (ValidacaoCruzada) validacao.clone();
			Avaliador a = novaValidacao.avalia();
			a.avalia(limiar);
			return a;
		}).collect(Collectors.toList());
		
		tempo.capturaTempo(numTestes);
		tempo.imprimeEstatistica("Teste de Confianca");
	}
	

	/**
	 * Retorna a media e o intervalo de confianca com 95% de uma dada metrica da classe Avaliador
	 * Os resultados sao retornados na forma de um array com a media no primeiro elemento e o intervalo no segundo
	 * */
	public double[] intervaloConfianca(ToDoubleFunction<Avaliador> metrica){
		//Tentativa de usar a classe stream do java 8

		double mediaAmostral = resultados.stream().mapToDouble(metrica).average().orElse(0.0);
		double varianciaAmostral = resultados.stream().mapToDouble(metrica).map(i -> Math.pow((i - mediaAmostral), 2)).sum() / (resultados.size() - 1);
		double intervalo = coef*Math.sqrt(varianciaAmostral/resultados.size());
		return new double[]{mediaAmostral, intervalo};
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();

		sb.append("\nAcuracia          = ");
		double acuracia[] = intervaloConfianca(Avaliador::acuracia);
		sb.append(String.format(formatacaoIntervaloConfianca, acuracia[0], acuracia[1]));

		sb.append("\nPrecisao positiva = ");
		double precisao[] = intervaloConfianca(Avaliador::precisaoPositiva);
		sb.append(String.format(formatacaoIntervaloConfianca, precisao[0], precisao[1]));

		sb.append("\nRecall positivo   = ");
		double recall[] = intervaloConfianca(Avaliador::recallPositiva);
		sb.append(String.format(formatacaoIntervaloConfianca, recall[0], recall[1]));

		sb.append("\nF1 positiva       = ");
		double f1[] = intervaloConfianca(Avaliador::f1MeasurePositiva);
		sb.append(String.format(formatacaoIntervaloConfianca, f1[0], f1[1]));

		return sb.toString();
	}
}
