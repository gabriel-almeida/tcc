package utilidades;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

public class ScatterPlot {
	public String titulo = "Dispersao das medidas de desempenho em função do limiar";
	public String eixoX = "limiar";
	public String eixoY = "valor";
	
	private int i = 0;
	private DefaultXYDataset scatter = new DefaultXYDataset();
	
	public void adicionaEstatistica(Map<Double, List<Double>> estatisticasPorLimiar, String nomeEstatistica) {
		int tamTotal = estatisticasPorLimiar.values().stream().mapToInt(List::size).sum();
		double dados[][] = new double[2][tamTotal];
		
		i = 0;
		estatisticasPorLimiar
			.forEach((limiar, valores) -> valores.stream().forEach((valor) -> {
			dados[0][i] = limiar;
			dados[1][i] = valor;
			i++;
		}));
		
		scatter.addSeries(nomeEstatistica, dados);
	}
	public void escreveGrafico(){
		JFreeChart grafico = ChartFactory.createScatterPlot(titulo, eixoX, eixoY, scatter);
		try {
			OutputStream arquivo = new FileOutputStream("grafico.png");
			ChartUtilities.writeChartAsPNG(arquivo, grafico, 1040, 800);
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
