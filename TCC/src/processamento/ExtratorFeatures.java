package processamento;

import java.util.ArrayList;
import java.util.List;

import modelo.ConjuntoDados;
import modelo.Elemento;
import utilidades.MetricasSimilaridade;

public class ExtratorFeatures {
	private PreProcessamento pp;
	private ConjuntoDados dataset;
	private List<Elemento> elemento1;
	private List<Elemento> elemento2;
	
	public ExtratorFeatures(PreProcessamento pp){
		this.pp = pp;
		this.dataset = new ConjuntoDados();
		
		this.elemento1 = new ArrayList<Elemento>();
		this.elemento2 = new ArrayList<Elemento>();
	}
	public void extrai(Elemento e1, Elemento e2){
		Elemento e1Processado = pp.processa(e1);
		Elemento e2Processado = pp.processa(e2);
		ArrayList<Double> features = new ArrayList<Double>();
		
		for (int i=0; i < e1Processado.tamanho(); i++){
			String s1 = e1Processado.getElemento(i);
			String s2 = e2Processado.getElemento(i);
			//TODO possivelmente melhorar
			double jw = MetricasSimilaridade.JaroWinkler(s1, s2);
			double me = MetricasSimilaridade.MongeElkan(s1, s2);
			
			features.add(jw);
			features.add(me);
		}
		this.elemento1.add(e1);
		this.elemento1.add(e2);
		int id = this.elemento1.size();
		this.dataset.adicionaAmostra(features);
		
	}
	public ConjuntoDados getConjuntoDados(){
		return this.dataset;
	}
	
}
