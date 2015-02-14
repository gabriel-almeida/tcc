package avaliacao;

import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TesteConfianca {
	private int numTestes;
	private List<Avaliador> resultados;
	public static final String formatacaoIntervaloConfianca= "%f (+- %f)";
	public static final double coef = 1.96;
	
	public void testa(int numTestes, ValidacaoCruzada validacao){
		resultados = IntStream.range(0, numTestes).mapToObj(i-> validacao.avalia()).collect(Collectors.toList());
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
		sb.append("Acuracia = ");
		sb.append(String.format(formatacaoIntervaloConfianca, intervaloConfianca(Avaliador::acuracia)));
		sb.append("\nPrecisao positiva = ");
		sb.append(String.format(formatacaoIntervaloConfianca, intervaloConfianca(Avaliador::precisaoPositiva)));
		sb.append("\nRecall positivo = ");
		sb.append(String.format(formatacaoIntervaloConfianca, intervaloConfianca(Avaliador::recallPositiva)));
		sb.append("\nF1 positiva = ");
		sb.append(String.format(formatacaoIntervaloConfianca, intervaloConfianca(Avaliador::f1MeasurePositiva)));
		
		return sb.toString();
	}
	public static void main(String args[]){
		System.out.println();
	}
}
