package processamento;

import utilidades.MetricasSimilaridade;
import modelo.ConjuntoDados;
import modelo.Elemento;

public class ExtratorFeatures {
	private PreProcessamento pp;
	private ConjuntoDados dataset;
	public ExtratorFeatures(PreProcessamento pp){
		this.pp = pp;
		this.dataset = new ConjuntoDados();
	}
	public void extrai(Elemento e1, Elemento e2){
		Elemento e1Processado = pp.processa(e1);
		Elemento e2Processado = pp.processa(e2);
		for (int i=0; i < e1Processado.tamanho(); i++){
			String s1 = e1Processado.getElemento(i);
			String s2 = e2Processado.getElemento(i);
			//TODO
			MetricasSimilaridade.JaroWinkler(s1, s2);
			MetricasSimilaridade.MongeElkan(s1, s2);
		}
	}
	public ConjuntoDados getConjuntoDados(){
		return this.dataset;
	}
	
}
