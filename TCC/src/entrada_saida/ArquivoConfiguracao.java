package entrada_saida;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import processamento.PreProcessamento;
import processamento.Processador;
import processamento.ProcessadorString;
import supervisao.CriterioMaioria;
import supervisao.CriterioVotacao;
import utilidades.Constantes;
import extracaoFeatures.Extrator;
import extracaoFeatures.ExtratorFactory;
import extracaoFeatures.ExtratorFeatures;

public class ArquivoConfiguracao {
	private String arqBase1;
	private String arqBase2;
	private String chaveBase1;
	private String chaveBase2;
	private int tamanhoMaximoChave = -1;
	private List<String> tiposDado = new ArrayList<String>();
	private List<String> colunasBase1 = new ArrayList<String>();
	private List<String> colunasBase2 = new ArrayList<String>();

	//Relativo a supervisao de arquivo
	private String arqRespostas;
	private String colunaChaveRespostas;
	private String colunaRespostaPositiva;
	private String colunaRespostaNegativa;
	

	//Relativo a preprocessamento
	private Map<String, List<String>> stopwordsNomes = new HashMap<String, List<String>>();
	private Map<String, Map<String, String>> tabelaSubstituicaoNomes = new HashMap<String, Map<String,String>>();
	private Map<String, String> regexpPorTipo = new HashMap<String, String>();
	private Map<String, Integer> tamanhoMaximoTipo = new HashMap<String, Integer>();

	//Relativo a extracao de features
	private Map<String, Extrator> extratorPorTipo = new HashMap<String, Extrator>();

	//Relativos a arquivos de saida 
	private String arqSaidaMatching;
	private String arqPesosRegressao;
	private String arqSaidaDuplicatas;


	public static final String SEPARADOR = "[ \t]+";
	public static final String COMENTARIO = "#";
	private static final String TIPO_PADRAO_RESPOSTA = "numero";
	
	public ArquivoConfiguracao(String arqConfiguracao) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(arqConfiguracao));

		while(br.ready()){
			String linha = br.readLine();
			if (linha.startsWith(COMENTARIO) || linha.equals("")){
				continue;
			}
			String campos[] = linha.split(SEPARADOR);

			ComandosConfiguracao comando = ComandosConfiguracao.getComandoString(campos[0]);
			switch (comando){
			case comandoArquivoBase1:
				this.arqBase1 = campos[1];
				break;
			case comandoArquivoBase2:
				this.arqBase2 = campos[1];
				break;
			case comandoChaveBase1:
				this.chaveBase1 = campos[1];
				break;
			case comandoChaveBase2:
				this.chaveBase2 = campos[1];
				break;
			case comandoParearColunas:
				String tipoDadoColuna = campos[1];
				String colunaBase1 = campos[2];
				String colunaBase2 = campos[3];
				this.tiposDado.add(tipoDadoColuna);
				this.colunasBase1.add(colunaBase1);
				this.colunasBase2.add(colunaBase2);
				break;
			case comandoArquivoResposta:
				this.arqRespostas = campos[1];
				break;
			case comandoColunaChaveResposta:
				this.colunaChaveRespostas= campos[1];
				break;
			case comandoStopwords:
				String tipoStopword = campos[1];
				this.stopwordsNomes.putIfAbsent(tipoStopword, new ArrayList<String>());
				List<String> listaStopwords = this.stopwordsNomes.get(tipoStopword);

				for (int i=2; i < campos.length; i++){
					String stopword = campos[i];
					listaStopwords.add(stopword);
				}
				break;
			case comandoTabelaSubstituicao:
				String tipoSubst = campos[1];
				String abreviacao = campos[2]; 
				String expansao = campos[3];
				this.tabelaSubstituicaoNomes.putIfAbsent(tipoSubst, new HashMap<String, String>());
				Map<String, String> tabelaAtual = this.tabelaSubstituicaoNomes.get(tipoSubst);
				tabelaAtual.put(abreviacao, expansao);
				break;
			case comandoColunaRespostaPositiva:
				this.colunaRespostaPositiva = campos[1];
				break;
			case comandoColunaRespostaNegativa:
				this.colunaRespostaNegativa = campos[1];
				break;
			case comandoArquivoSaidaMatching:
				this.arqSaidaMatching = campos[1];
				break;
			case comandoArquivoSaidaRegressao:
				this.arqPesosRegressao = campos[1];
				break;
			case comandoArquivoSaidaPossiveisDuplicatas:
				this.arqSaidaDuplicatas = campos[1];
				break;
			case comandoExtratorPorTipo:
				String tipoExtrator = campos[1];
				String nomeExtrator = campos[2];
				Extrator extratorFeature = ExtratorFactory.getExtratorPorNome(nomeExtrator);
				this.extratorPorTipo.put(tipoExtrator, extratorFeature);
				break;
			case comandoRegexpRemocao:
				String tipoRegexp = campos[1];
				int inicioRegexp = linha.indexOf(Constantes.ASPAS);
				int fimRegexp = linha.lastIndexOf(Constantes.ASPAS);
				
				String regexp = linha.substring(inicioRegexp + 1, fimRegexp);
				this.regexpPorTipo.put(tipoRegexp, regexp);
				break;
			case comandoTamanhoMaximo:
				String tipoTamanho = campos[1];
				int tamanho = Integer.parseInt(campos[2]);
				this.tamanhoMaximoTipo.put(tipoTamanho, tamanho);
				break;
			case comandoTamanhoMaximoChave:
				this.tamanhoMaximoChave = Integer.parseInt(campos[1]);
				break;
			default:
				br.close();
				throw new RuntimeException("Descritor desconhecido: " + campos[0]);
			}
		}
		br.close();
	}
	public EntradaCSV getCSVBase1(){
		EntradaCSV base1 = new EntradaCSV(arqBase1, colunasBase1, tiposDado, chaveBase1);
		base1.setTamMaxChave(tamanhoMaximoChave);
		return base1;
	}
	public EntradaCSV getCSVBase2(){
		EntradaCSV base2 = new EntradaCSV(arqBase2, colunasBase2, tiposDado, chaveBase2);
		base2.setTamMaxChave(tamanhoMaximoChave);
		return base2;
	}
	
	public EntradaCSV getCSVResposta(){
		List<String> colunasResposta = new ArrayList<String>();
		colunasResposta.add(colunaRespostaPositiva);
		colunasResposta.add(colunaRespostaNegativa);

		//Necessario criar lista de tipo de dados
		List<String> tipoDadosResposta = new ArrayList<String>();
		tipoDadosResposta.add(TIPO_PADRAO_RESPOSTA);
		tipoDadosResposta.add(TIPO_PADRAO_RESPOSTA);

		EntradaCSV resposta = new EntradaCSV(arqRespostas, colunasResposta, tipoDadosResposta, colunaChaveRespostas);
		resposta.setTamMaxChave(tamanhoMaximoChave);
		return resposta;
	}

	/*public SaidaCSV getCSVClassificao(){
		return new SaidaCSV(arqSaidaMatching, colunaChaveRespostas);
	}*/

	/**
	 * Gera um objto PreProcessamento carregado com varios objetos Processador, especificados pelo arquivo de configuracao.
	 * Se um tipo de dado nao for especificado, o preprocessamento nao tera efeito
	 * */
	public PreProcessamento getPreprocessamento(){
		PreProcessamento processamento = new PreProcessamento();
		for (String tipo: tiposDado){
			ProcessadorString processador = new ProcessadorString(
					tabelaSubstituicaoNomes.getOrDefault(tipo, new HashMap<String, String>()),
					stopwordsNomes.getOrDefault(tipo, new ArrayList<String>()), 
					regexpPorTipo.getOrDefault(tipo, "")
					);
			processador.setTamanhoMaximo(this.tamanhoMaximoTipo.getOrDefault(tipo, -1));
			processamento.setTratamento(tipo, processador);
		}

		return processamento;
	}

	public String getArquivoRegressao(){
		return this.arqPesosRegressao;
	}
	/**
	 * Retornar o extrator de features carregado de acordo com as informacoes do arquivo. 
	 * O preprocessador usado sera o mesmo retornado pelo metodo getPreprocessamento.
	 * Se um tipo nao tiver um extrator definido, ser usado o extrator padrao.
	 * */
	public ExtratorFeatures getExtrator(){
		ExtratorFeatures extrator = new ExtratorFeatures(getPreprocessamento());
		for (String tipo: tiposDado){
			Extrator extratorTipo = this.extratorPorTipo.getOrDefault(tipo, Constantes.EXTRATOR_PADRAO);
			extrator.setExtratorPorTipo(tipo, extratorTipo);
		}
		
		return extrator;
	}
	public CriterioVotacao getVotacaoMaioria(){
		return new CriterioMaioria(this.colunaRespostaPositiva, this.colunaRespostaNegativa);
	}
}
