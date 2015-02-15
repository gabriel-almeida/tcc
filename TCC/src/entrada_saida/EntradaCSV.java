package entrada_saida;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import modelo.Elemento;
import utilidades.AnalisePerformace;

public class EntradaCSV {
	private String delimitador = "\";\"";
	private List<String> colunasRelevantes;
	private List<String> tipoColunas;
	private String arqCsv;

	private String colunaChave;
	private int indiceChave;
	
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
	/**
	 * Implementacao concorrente da leitura de CSV. 
	 * O mapa retornado eh thread-safe
	 * */
	public Map<String, Elemento> leCsv() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(arqCsv));

		Map<String, Elemento> resultado = new ConcurrentHashMap<String, Elemento>();
		List<Integer> colunasValidas = new ArrayList<Integer>();
		
		indiceChave = -1;

		//inicializo essa lista com indices -1
		for (int i=0; i< colunasRelevantes.size(); i++){
			colunasValidas.add(-1);
		}
		
		//LOG tempo
		AnalisePerformace tempo = new AnalisePerformace();

		//Localizo os cabecalhos relevantes e boto seus indices na ordem da lista de nomes
		String cabecalho[] = br.readLine().split(delimitador);
		for (int i = 0; i< cabecalho.length; i++){
			String nomeColunaAtual = cabecalho[i].replaceAll("\"", "");

			if (nomeColunaAtual.equals(colunaChave)){ //caso chave
				indiceChave = i;
				continue;
			}

			int indiceColuna = colunasRelevantes.indexOf(nomeColunaAtual);
			if (indiceColuna != -1){
				colunasValidas.set(indiceColuna, i);
			}
		}

		br.lines().parallel().forEach( (String linha) -> {
			String campos[] = linha.split(delimitador);//TODO pensar em algo melhor
			
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

		});
		
		//Log Tempo
		tempo.capturaTempo(resultado.size());
		tempo.imprimeEstatistica("Lendo " + arqCsv);
		
		br.close();
		return resultado;
	}

}
