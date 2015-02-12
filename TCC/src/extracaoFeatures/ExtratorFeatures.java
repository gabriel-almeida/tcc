package extracaoFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import modelo.Elemento;
import processamento.PreProcessamento;

public class ExtratorFeatures {
	private PreProcessamento pp;
	private Map<String, Extrator> extratorPorTipo;
	
	
	public ExtratorFeatures(PreProcessamento pp){
		this.pp = pp;
		this.extratorPorTipo = new HashMap<String, Extrator>();
		
		//Setup padrao do extrator
		Extrator extratorNome = new ExtratorMetricasSimilaridade();
		Extrator extratorData= new ExtratorData();
		Extrator extratorString= new ExtratorJaroWinkler();
		setExtratorPorTipo("string", extratorNome);
		setExtratorPorTipo("nome", extratorNome);
		setExtratorPorTipo("data", extratorData);
	}
	public void setExtratorPorTipo(String tipo, Extrator extrator){
		extratorPorTipo.put(tipo, extrator);
	}
	
	public ArrayList<Double> extrai(Elemento e1, Elemento e2){
		Elemento e1Processado = pp.processa(e1);
		Elemento e2Processado = pp.processa(e2);
		ArrayList<Double> features = new ArrayList<Double>();
		
		for (int i=0; i < e1Processado.tamanho(); i++){
			String s1 = e1Processado.getElemento(i);
			String s2 = e2Processado.getElemento(i);
			String tipoAtual = e1Processado.getTipoDado(i);
			
			Extrator extrator = this.extratorPorTipo.get(tipoAtual);
			if (extrator == null){
				throw new RuntimeException("Tipo de dado nao esperado no extrator: " + tipoAtual);
			}
			ArrayList<Double> featuresNovas = extrator.extrai(s1, s2);
			features.addAll(featuresNovas);
		}
		return features;
	}
}
