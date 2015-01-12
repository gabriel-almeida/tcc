package processamento;

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
		
	}
	public ConjuntoDados getConjuntoDados(){
		return this.dataset;
	}
	
}
