package entrada;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.Elemento;

public class EntradaCSV {
	public static String delimitador = "\";\"";

	public static Map<String, Elemento> leCsv(String arqCsv, List<String> colunasRelevantes, String colunaChave) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(arqCsv));
		Map<String, Elemento> resultado = new HashMap<String, Elemento>();
		List<Integer> colunasValidas = new ArrayList<Integer>();
		int indiceChave = -1;

		//inicializo essa lista com indices -1
		for (int i=0; i< colunasRelevantes.size(); i++){
			colunasValidas.add(-1);
		}

		int numeroLinhaAtual=0;
		while(br.ready()){
			String linha = br.readLine();
			String campos[] = linha.split(delimitador);

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

				String chave = campos[indiceChave].replaceAll("\"",""); 
				chave = chave.substring(0, 9); //TODO substring gambiarra, pensar numa solucao melhor

				Elemento e = new Elemento(chave, colunasRelevantes);
				int contador = 0;
				for (int i: colunasValidas){
					e.addElemento(contador, campos[i]);
					contador++;
				}
				resultado.put(chave, e);

			}

			numeroLinhaAtual++;
		}
		br.close();
		return resultado;
	}
}
