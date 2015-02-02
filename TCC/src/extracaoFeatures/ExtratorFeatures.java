package extracaoFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processamento.PreProcessamento;
import modelo.ConjuntoDados;
import modelo.Elemento;
import utilidades.MetricasSimilaridade;

public class ExtratorFeatures {
	private PreProcessamento pp;
	private ConjuntoDados dataset;
	private List<Elemento> elemento1;
	private List<Elemento> elemento2;
	private Map<String, Integer> indicesChave;
	
	public ExtratorFeatures(PreProcessamento pp){
		this.pp = pp;
		this.dataset = new ConjuntoDados();
		
		this.elemento1 = new ArrayList<Elemento>();
		this.elemento2 = new ArrayList<Elemento>();
		this.indicesChave = new HashMap<String, Integer>();
	}
	public int getIndice(String chave){
		return indicesChave.get(chave);
	}
	public Elemento getElemento1(int indice){
		return elemento1.get(indice);
	}
	public Elemento getElemento2(int indice){
		return elemento2.get(indice);
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
		this.elemento2.add(e2);
		this.dataset.adicionaAmostra(features);
		this.indicesChave.put(e1.getChave(), this.elemento1.size());
	}
	public ConjuntoDados getConjuntoDados(){
		return this.dataset;
	}
	
}
