package entrada;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import modelo.Elemento;

public class SaidaCSV{
	private String arqSaida;
	private String nomeColunaChave;
	private String delimitador = ";";
	private String aspasString = "\"";
	
	public SaidaCSV(String arqSaida, String nomeColunaChave) {
		this.arqSaida = arqSaida;
		this.nomeColunaChave = nomeColunaChave;
	}
	
	public void salva(List<Elemento> listaElementos) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(arqSaida));
		int contadorElementos = 0;
		for (Elemento e: listaElementos){
			//descritores no cabecalho
			if (contadorElementos == 0){
				bw.write(aspasString);
				bw.write(nomeColunaChave);
				bw.write(aspasString);
				for (String descritor : e.getDescritores()){
					bw.write(delimitador);
					bw.write(aspasString);
					bw.write(descritor);
					bw.write(aspasString);
				}
				bw.write("\n");
			}
			
			int quantElementos = e.tamanho();
			bw.write(aspasString);
			bw.write(e.getChave());
			bw.write(aspasString);
			for (int i=0; i < quantElementos; i++){
				String elemento = e.getElemento(i);
				bw.write(delimitador);
				bw.write(aspasString);
				bw.write(elemento);
				bw.write(aspasString);
			}
			bw.write("\n");
			contadorElementos++;
		}
		bw.flush();
		bw.close();
	}

	public void setDelimitador(String delimitador) {
		this.delimitador = delimitador;
	}

	public void setAspasString(String aspasString) {
		this.aspasString = aspasString;
	}
	
}
