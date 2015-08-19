package utilidades;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

public class LinePlot {
	private String eixoX;
	private String eixoY;
	private String titulo;

	private DefaultXYDataset ds = new DefaultXYDataset();
	
	public LinePlot(String titulo, String eixoX, String eixoY){
		this.eixoX = eixoX;
		this.eixoY = eixoY;
		this.titulo = titulo;
	}
	
	public void adicionaMedias(Map<Double, List<Double>> dados, String nomeSerie){
		int tam = dados.size();
		double [][] data = new double[2][tam];
		int i = 0;
		for (Double x: dados.keySet().stream().sorted().collect(Collectors.toList())){
			double y = dados.get(x).stream().mapToDouble(d->d).average().getAsDouble();
			data[0][i] = x;
			data[1][i] = y;
			i++;
		}
		
		ds.addSeries(nomeSerie, data);
	}
	
	private static double[][] tabelaPontos(List<Double> curva){
		double[] indices = IntStream.rangeClosed(1, curva.size()).asDoubleStream().toArray();
		double[] valores = curva.stream().mapToDouble(i -> i).toArray();
		return new double[][]{indices, valores};
	}	
	
	public static void plotaCurvaAprendizado(List<Double> curvaTreino, List<Double> curvaValidacao){
		LinePlot grafico = new LinePlot("Curva de aprendizado", "Número de iterações", "Erro Médio Quadrático");
		
		grafico.ds.addSeries("Treinamento", tabelaPontos(curvaTreino));
		grafico.ds.addSeries("Teste", tabelaPontos(curvaValidacao));
		
		grafico.plotaCurva("curva_aprendizado");
	}
	
	/** Retorna o grafico da JFreeChart */
	public JFreeChart plotaCurva(String nomeArquivo){
		 NumberAxis xAxis = new NumberAxis(eixoX);
		 xAxis.setRange(0.0, 1.0);
		 
		 NumberAxis yAxis = new NumberAxis(eixoY);
		 
		 XYItemRenderer renderer = new XYLineAndShapeRenderer(true, true);
		 //renderer.setBaseItemLabelsVisible(true);
		 renderer.setBaseSeriesVisible(true);
		 
		 XYPlot plot = new XYPlot(this.ds, xAxis, yAxis, renderer);
		 plot.setOrientation(PlotOrientation.VERTICAL);
		 
		 JFreeChart chart = new JFreeChart(titulo, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		 try {
			OutputStream arquivo = new FileOutputStream(nomeArquivo + ".png");
			ChartUtilities.writeChartAsPNG(arquivo, chart, 1040, 800);
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return chart;
	}
}
