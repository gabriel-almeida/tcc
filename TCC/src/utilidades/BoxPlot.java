package utilidades;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;

public class BoxPlot {
	public String eixoX = "limiar";
	public String eixoY = "valor";
	
	private DefaultBoxAndWhiskerCategoryDataset box = new DefaultBoxAndWhiskerCategoryDataset();
	public void adicionaEstatistica(Map<Double, List<Double>> estatisticasPorLimiar, String nomeEstatistica) {
		estatisticasPorLimiar.keySet().stream().sorted()
			.forEach(limiar -> box.add(estatisticasPorLimiar.get(limiar), nomeEstatistica, limiar));		
	}
	public void escreveGrafico(String titulo){
		JFreeChart grafico = ChartFactory.createBoxAndWhiskerChart(titulo, eixoX, eixoY, box, true);
		try {
			String nomeArq = titulo.replaceAll("[ ]+", "_");
			OutputStream arquivo = new FileOutputStream(nomeArq + ".png");
			ChartUtilities.writeChartAsPNG(arquivo, grafico, 1040, 800);
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
