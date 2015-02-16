package entrada_saida;

import java.util.Arrays;

public enum ComandosConfiguracao {
	//Relativos ao setup padrao das bases
	comandoArquivoBase1("arquivoBase1"), 
	comandoArquivoBase2("arquivoBase2"), 
	comandoChaveBase1("colunaChaveBase1"),
	comandoChaveBase2("colunaChaveBase2"),
	comandoTamanhoMaximoChave("tamanhoMaximoChave"),
	comandoParearColunas("parear"),
	

	
	//Relativos ao arquivo de entrada
	comandoArquivoResposta("arquivoResposta"), 
	comandoColunaChaveResposta("colunaChaveResposta"), 
	comandoColunaRespostaPositiva("respostaPositiva"),
	comandoColunaRespostaNegativa("respostaNegativa"),
	
	//Relativos aos arquivos de saida
	comandoArquivoSaidaMatching("arquivoSaidaMatching"), 
	comandoArquivoSaidaRegressao("arquivoSaidaRegressao"),
	comandoArquivoSaidaPossiveisDuplicatas("arquivoSaidaDuplicatas"),
	
	//Relativos a pre processamento
	comandoStopwords("stopwords"), 
	comandoTabelaSubstituicao("subst"),
	comandoTamanhoMaximo("tamanhoMaximo"),
	comandoRegexpRemocao("regexpRemocao"),
	
	//Relativos a extracao de features
	comandoExtratorPorTipo("extrator");
	
	private String comando;
	private ComandosConfiguracao(String comando) {
		this.comando = comando;
	}
	
	public static ComandosConfiguracao getComandoString(String s){
		return Arrays.stream(ComandosConfiguracao.values()).filter( comando-> comando.comando.equals(s) ).findAny().get();
	}
}
