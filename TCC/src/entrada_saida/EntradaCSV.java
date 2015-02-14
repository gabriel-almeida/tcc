package entrada_saida;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilidades.AnalisePerformace;
import modelo.Elemento;

public class EntradaCSV {
	private String delimitador = "\";\"";
	private List<String> colunasRelevantes;
	private List<String> tipoColunas;
	private String arqCsv;
	
	private String colunaChave;
	
	public EntradaCSV(String arqCsv, List<String> colunasRelevantes, List<String> tipoColunas, String colunaChave) {
		this.colunasRelevantes = colunasRelevantes;
		this.colunaChave = colunaChave;
		this.tipoColunas = tipoColunas;
		this.arqCsv = arqCsv;
		if (colunasRelevantes.size() != tipoColunas.size()){
			throw new RuntimeException("Tamanhos inconsistentes de coluna.");
		}
	}
	
	public String getDelimitador() {
		return delimitador;
	}
	public void setDelimitador(String delimitador) {
		this.delimitador = delimitador;
	}

	public List<String> getColunasRelevantes() {
		return colunasRelevantes;
	}

	public List<String> getTipoColunas() {
		return tipoColunas;
	}

	public String getColunaChave() {
		return colunaChave;
	}
	public String getNomeArquivo() {
		return arqCsv;
	}
	public Map<String, Elemento> leCsv() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(arqCsv));
		Map<String, Elemento> resultado = new HashMap<String, Elemento>();
		List<Integer> colunasValidas = new ArrayList<Integer>();
		int indiceChave = -1;

		//inicializo essa lista com indices -1
		for (int i=0; i< colunasRelevantes.size(); i++){
			colunasValidas.add(-1);
		}
		//LOG tempo
		int numeroLinhaAtual=0;
		AnalisePerformace.zera();
		AnalisePerformace.capturaTempo(numeroLinhaAtual);
		
		while(br.ready()){
			String linha = br.readLine();
			String campos[] = linha.split(delimitador);//TODO pensar em algo melhor

			//Localizo os cabecalhos relevantes e boto seus indices na ordem da lista de nomes
			if (numeroLinhaAtual == 0){
				for (int i = 0; i< campos.length; i++){
					String nomeColunaAtual = campos[i].replaceAll("\"", "");

					if (nomeColunaAtual.equals(colunaChave)){ //caso chave
						indiceChave = i;
						continue;
					}

					int indiceColuna = colunasRelevantes.indexOf(nomeColunaAtual);
					if (indiceColuna != -1){
						colunasValidas.set(indiceColuna, i);
					}
				}
			}
			else{
				//TODO pensar em algo melhor do que essa remocao de aspas
				String chave = campos[indiceChave].replaceAll("\"",""); 
				chave = chave.substring(0, 9); //TODO substring gambiarra, pensar numa solucao melhor

				Elemento e = new Elemento(chave, colunasRelevantes, tipoColunas);
				int contador = 0;
				for (int i: colunasValidas){
					e.addElemento(contador, campos[i].replaceAll("\"", ""));
					contador++;
				}
				resultado.put(chave, e);

			}
			//Log Tempo
			numeroLinhaAtual++;
			if (numeroLinhaAtual% 10000 == 0 ){
				AnalisePerformace.capturaTempo(numeroLinhaAtual);
			}
			
		}
		AnalisePerformace.imprimeEstatistica("Lendo " + arqCsv);
		br.close();
		return resultado;
	}
}
