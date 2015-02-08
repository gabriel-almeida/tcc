package entrada;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processamento.PreProcessamento;
import processamento.Processador;
import processamento.ProcessadorNome;

public class ArquivoConfiguracao {
	private String arqBase1;
	private String arqBase2;
	private String chaveBase1;
	private String chaveBase2;
	private List<String> tiposDado;
	private List<String> colunasBase1;
	private List<String> colunasBase2;

	private String arqRespostas;
	private String colunaChaveRespostas;
	private List<String> tipoDadosResposta;
	private List<String> colunasResposta;

	private List<String> stopwordsNomes;
	private Map<String, String> tabelaSubstituicaoNomes;

	public static final String separador = "[ \t]";
	public static final String marcadorComentario = "#";
	public static final String descritorArquivoBase1 = "arquivoBase1";
	public static final String descritorArquivoBase2 = "arquivoBase2";
	public static final String descritorChaveBase1 = "colunaChaveBase1";
	public static final String descritorChaveBase2 = "colunaChaveBase2";
	public static final String descritorColuna = "coluna";

	public static final String descritorArquivoResposta = "arquivoResposta";
	public static final String descritorColunaChaveResposta = "colunaChaveResposta";
	public static final String descritorColunaResposta = "resposta";

	public static final String descritorStopwords = "stopwordsNome";
	public static final String descritorTabelaSubstituicao = "abreviacaoNome";

	public ArquivoConfiguracao(String arqConfiguracao) throws IOException{
		tiposDado = new ArrayList<String>();
		colunasBase1 = new ArrayList<String>();
		colunasBase2 = new ArrayList<String>();
		stopwordsNomes = new ArrayList<String>();
		tabelaSubstituicaoNomes = new HashMap<String, String>();

		BufferedReader br = new BufferedReader(new FileReader(arqConfiguracao));

		while(br.ready()){
			String linha = br.readLine();

			if (linha.startsWith(marcadorComentario)){
				continue;
			}

			String campos[] = linha.split(separador);

			if (campos[0].equals(descritorArquivoBase1)){
				this.arqBase1 = campos[1];
			}
			else if (campos[0].equals(descritorArquivoBase2)){
				this.arqBase2 = campos[1];
			}
			else if (campos[0].equals(descritorChaveBase1)){
				this.chaveBase1 = campos[1];
			}
			else if (campos[0].equals(descritorChaveBase2)){
				this.chaveBase2 = campos[1];
			}
			else if (campos[0].equals(descritorColuna)){
				if (campos.length < 4){
					br.close();
					throw new RuntimeException("Esperado 3 campos na configuracao do descritor \"" + descritorColuna + "\", encontrado " + campos.length);
				}
				String tipoDadoColuna = campos[1];
				String colunaBase1 = campos[2];
				String colunaBase2 = campos[3];
				this.tiposDado.add(tipoDadoColuna);
				this.colunasBase1.add(colunaBase1);
				this.colunasBase2.add(colunaBase2);
			}
			else if (campos[0].equals(descritorArquivoResposta)){
				this.arqRespostas = campos[1];
			}
			else if (campos[0].equals(descritorColunaChaveResposta)){
				this.arqRespostas = campos[1];
			}
			else if (campos[0].equals(descritorStopwords)){
				for (int i=1; i < campos.length; i++){
					String stopword = campos[i];
					this.stopwordsNomes.add(stopword);
				}
			}
			else if (campos[0].equals(descritorTabelaSubstituicao)){
				String abreviacao = campos[1]; 
				String expansao = campos[2];
				this.tabelaSubstituicaoNomes.put(abreviacao, expansao);
			}
			else if (campos[0].equals(descritorColunaResposta)){
				String tipoDadoResposta = campos[1];
				String nomeColunaResposta = campos[2];
				this.tipoDadosResposta.add(tipoDadoResposta);
				this.colunasResposta.add(nomeColunaResposta);
			}
			else{
				br.close();
				throw new RuntimeException("Descritor desconhecido: " + campos[0]);
			}
		}
		br.close();
	}
	public EntradaCSV getCSVBase1(){
		return new EntradaCSV(arqBase1, colunasBase1, tiposDado, chaveBase1);
	}
	public EntradaCSV getCSVBase2(){
		return new EntradaCSV(arqBase2, colunasBase2, tiposDado, chaveBase2);
	}
	public EntradaCSV getCSVResposta(){
		if (this.arqRespostas == null){
			return null;
		} 
		else{
			return new EntradaCSV(arqRespostas, colunasResposta, tipoDadosResposta, colunaChaveRespostas);
		}
	}
	public PreProcessamento getPreprocessamento(){
		PreProcessamento processamento = new PreProcessamento();
		Processador p = new ProcessadorNome(this.tabelaSubstituicaoNomes, this.stopwordsNomes);

		processamento.setTratamento("nome", p);

		return processamento;
	}

}
