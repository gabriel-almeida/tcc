package entrada;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import modelo.Elemento;
import aprendizado.Regressao;

public class SerializacaoRegressao {

	private String descritorColunaPeso = "PesoRegressao";
	private String descritorColunaChave = "Indice";

	public void salvaPesos(Regressao r, String arqSaida) throws IOException{
		SaidaCSV saida = new SaidaCSV(arqSaida, descritorColunaChave);

		List<Double> pesos = r.getPesos();
		List<Elemento> elementosPeso = new ArrayList<Elemento>();

		List<String> descritorPeso = getListaDescritor();

		for (Double pesoAtual: pesos){
			String chave = Integer.toString(elementosPeso.size());
			String pesoString = Double.toString(pesoAtual);

			Elemento e = new Elemento(chave, descritorPeso);
			e.addElemento(descritorColunaPeso, pesoString);
			elementosPeso.add(e);
		}
		saida.salva(elementosPeso);	
	}
	
	/**
	 * Gera lista pedida pela classe de colunas relevantes, pedida pela classe de manipulacao de CSV*/
	private List<String> getListaDescritor(){
		List<String> colunas = new ArrayList<String>();
		colunas.add(this.descritorColunaPeso);
		return colunas;
	}
	
	/**
	 * Carrega um arquivo de pesos, retornando um objeto Regressao. Tal arquivo preferencialmente deve ser salvo pelo metodo desta classe
	 * */
	public Regressao carregaPesos(String arqPesos) throws IOException{
		//PS: Tratamento deste arquivo eh feito de forma similar ao arquivo das bases de dados, por isso a confusao na conversao entre tipos 
		List<String> colunasRelevantes = getListaDescritor();
		List<String> tipoColuna = new ArrayList<String>();
		tipoColuna.add("numero");
		
		EntradaCSV entrada = new EntradaCSV(arqPesos, colunasRelevantes, tipoColuna, this.descritorColunaChave);
		Map<String, Elemento> pesos = entrada.leCsv();
		List<Double> pesosFinais = new ArrayList<Double>(Collections.nCopies(pesos.size(), 0.0));
		
		for (String chave: pesos.keySet()){
			Elemento pesoAtual = pesos.get(chave);
			String pesoString = pesoAtual.getElemento(this.descritorColunaPeso);
			
			Double peso = Double.parseDouble(pesoString);
			int indice = Integer.parseInt(chave);
			
			pesosFinais.set(indice, peso);
		}
		Regressao regressao = new Regressao();
		regressao.setPesos(pesosFinais);
		
		return regressao;
	}

	public void setDescritorColunaPeso(String descritorColunaPeso) {
		this.descritorColunaPeso = descritorColunaPeso;
	}

	public void setDescritorColunaChave(String descritorColunaChave) {
		this.descritorColunaChave = descritorColunaChave;
	}
}
