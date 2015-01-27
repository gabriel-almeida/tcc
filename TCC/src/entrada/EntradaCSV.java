package entrada;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import modelo.Elemento;

public class EntradaCSV {
	public static String delimitador = ";";
	
	public static Map<String, Elemento> leCsv(String arqCsv) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(arqCsv));
		Map<String, Elemento> resultado = new HashMap<String, Elemento>();
		
		while(br.ready()){
			String linha = br.readLine();
			String campos[] = linha.split(delimitador);
			String chave = campos[0]; 
			Elemento e = new Elemento(chave);
			for (int i=1; i < campos.length; i++){
				e.setElemento(i, campos[i]);
			}
			
			resultado.put(chave, e);
		}
		br.close();
		return resultado;
	}
}
