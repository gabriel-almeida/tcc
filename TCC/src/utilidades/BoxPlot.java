package utilidades;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/**Classe usada para desenhar um grafico do tipo box and whisker 
 * dos valores passados pela funcao adicionaEstatistica(). 
 * Usa de forma customizada a JFreeChart */
public class BoxPlot {
	private String eixoX;
	private String eixoY;
	private String titulo;
	
	private DefaultBoxAndWhiskerCategoryDataset box = new DefaultBoxAndWhiskerCategoryDataset();
	
	public BoxPlot(String titulo, String eixoX, String eixoY){
		this.eixoX = eixoX;
		this.eixoY = eixoY;
		this.titulo = titulo;
	}
	
	public void adicionaEstatistica(Map<Double, List<Double>> estatisticasPorLimiar, String nomeEstatistica) {
		estatisticasPorLimiar.keySet().stream().sorted()
		.forEach(limiar -> box.add(estatisticasPorLimiar.get(limiar), nomeEstatistica, limiar));		
	}
	
	/**Escreve o grafico num arquivo PNG, usando seu titulo como nome*/
	public void escreveGrafico(String nomeArq){
		JFreeChart grafico = geraPlot(this.titulo); 
		try {
			OutputStream arquivo = new FileOutputStream(nomeArq + ".png");
			ChartUtilities.writeChartAsPNG(arquivo, grafico, 1040, 800);
			arquivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	/**Metodo auxiliar usado para inserir uma categoria dummy em todos os limiares*/
	private void adicionaDummy(){
		List<Comparable> limiares = box.getColumnKeys();
		for (Comparable l : limiares){
			box.add(Collections.EMPTY_LIST, "DUMMY", l);
		}
	}
	
	/**
	 * Metodo baseado no codigo da funcao 
	 * ChartFactory.createBoxAndWhiskerChart, porem usando algumas configuracoes especiais,
	 * afim de deixar o grafico menos poluido e mais intuitivo
	 * */
	private JFreeChart geraPlot(String titulo){
		//
		CategoryAxis categoryAxis = new CategoryAxis(eixoX);
		
		NumberAxis valueAxis = new NumberAxis(eixoY);
		valueAxis.setRange(0.0, 1.0);

		BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
		renderer.setMeanVisible(false);

		//(Quase) centraliza as categorias usando uma categoria invisivel, 
		//sem isso o gridline fica colado no ultimo item da categoria 
		adicionaDummy();
		renderer.setSeriesVisible(box.getRowCount() - 1, false);
		
		
		CategoryPlot plot = new CategoryPlot(box, categoryAxis, valueAxis, renderer);
		
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePosition(CategoryAnchor.END);
		
		
		JFreeChart chart = new JFreeChart(titulo, JFreeChart.DEFAULT_TITLE_FONT,plot, true);
		return chart;
	}
}
