package utilidades;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;

public class CurvaAprendizado {
	public static double[][] tabelaPontos(List<Double> curva){
		double[] indices = IntStream.rangeClosed(2, curva.size() - 1).asDoubleStream().toArray();
		double[] valores = new double[curva.size() - 2];
		for (int i=1; i < curva.size() - 1; i++){
			valores[i-1] = (curva.get(i+1) - curva.get(i-1))/2 ; 
		}
		valores = curva.stream().mapToDouble(i -> i).limit(500).toArray();
		indices = IntStream.rangeClosed(1, curva.size()).limit(500).asDoubleStream().toArray();
		return new double[][]{indices, valores};
	}
	public static void plotaCurva(String nomeArquivo, List<Double> curvaTreino, List<Double> curvaValidacao){
		DefaultXYDataset ds = new DefaultXYDataset();
		ds.addSeries("Treinamento", tabelaPontos(curvaTreino));
		ds.addSeries("Teste", tabelaPontos(curvaValidacao));
		
		JFreeChart grafico = ChartFactory.createXYLineChart("Curva de aprendizado", "Epoch", 
			    "MSE", ds, PlotOrientation.VERTICAL, true, true, false);
		
		//XYPlot xyPlot = grafico.getXYPlot();
		//ValueAxis domainAxis = xyPlot.getDomainAxis();
		//ValueAxis rangeAxis = xyPlot.getRangeAxis();

		//domainAxis.setRange(0.0, 1.0);
		//rangeAxis.setRange(0.0, 0.1);
		
		try {
			OutputStream arquivo = new FileOutputStream(nomeArquivo);
			ChartUtilities.writeChartAsPNG(arquivo, grafico, 1040, 800);
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChartPanel panel = new ChartPanel(grafico);
		panel.setVisible(true);
		//System.out.println("Treino " + curvaTreino);
		//System.out.println("Val " + curvaValidacao);
	}
}
