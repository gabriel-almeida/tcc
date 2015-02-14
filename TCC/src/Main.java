import java.io.IOException;
import java.util.List;

import modelo.ConjuntoDados;
import modelo.Elemento;
import processamento.PreProcessamento;
import supervisao.Supervisao;
import supervisao.SupervisaoArquivo;
import supervisao.SupervisaoHumana;
import utilidades.Constantes;
import aprendizado.Regressao;
import aprendizado.RegressaoLinear;
import aprendizado.RegressaoLogistica;
import aprendizado.RegressaoZeroR;
import avaliacao.Avaliador;
import avaliacao.ValidacaoCruzada;
import entrada_saida.AmostragemAleatoria;
import entrada_saida.ArquivoConfiguracao;
import entrada_saida.EntradaCSV;
import entrada_saida.GerenciadorBases;
import entrada_saida.SaidaCSV;
import entrada_saida.SerializacaoRegressao;
import extracaoFeatures.ExtratorFeatures;
import extracaoFeatures.IgualdadeNome;

public class Main {
	public static String arqConfiguracao = "configuracao.txt";
	public static double porcentagemTeste = 0.30;
	public static final String parametroPorcentagemTeste = "-teste";
	public static final String parametroArquivoConfiguracao = "-configuracao";
	public static final String parametroSupervisaoHumana = "-supervisao-humana";
	public static final String parametroApenasDataMatching = "-apenas-matching";

	public static double extraiPorcentagem(String s){

		double d = Double.parseDouble(s);
		if (d > 100 || d < 0){
			throw new RuntimeException("Esperado porcentagem entre 0 e 100, recebido " + d);
		}
		return d;
	} 
	public static void main(String[] args) {
		boolean supervisaoHumana = false;
		boolean apenasMatching	= false;

		SerializacaoRegressao sr = new SerializacaoRegressao();

		//Parseia argumentos de ARGS
		for (int i = 0; i< args.length; i++){
			String argumento = args[i];
			if (argumento.equals(parametroPorcentagemTeste)){
				String s = args[++i];
				porcentagemTeste = extraiPorcentagem(s);
			}
			else if (argumento.equals(parametroArquivoConfiguracao)){
				arqConfiguracao = args[++i];
			}
			else if (argumento.equals(parametroSupervisaoHumana)){
				supervisaoHumana = true;
			}
			else if (argumento.equals(parametroApenasDataMatching)){
				apenasMatching = true;
			}
			else {
				throw new RuntimeException("Parametro desconhecido: " + argumento);
			}
		}
		try {
			ArquivoConfiguracao config = new ArquivoConfiguracao(arqConfiguracao);

			EntradaCSV entradaBase1 = config.getCSVBase1();
			EntradaCSV entradaBase2 = config.getCSVBase2();
			EntradaCSV entradaResposta = config.getCSVResposta();

			PreProcessamento preprocessamento = config.getPreprocessamento();
			ExtratorFeatures extratorFeatures = new ExtratorFeatures(preprocessamento);

			IgualdadeNome igualdade = new IgualdadeNome(); //TODO pegar do arq configuracao
			GerenciadorBases gerenciador = new GerenciadorBases(entradaBase1, entradaBase2, igualdade, extratorFeatures);
			gerenciador.pareiaBasesParalelo();
			
			if (!apenasMatching){
				Supervisao sup;
				if (supervisaoHumana || entradaResposta == null){
					AmostragemAleatoria amostragem = new AmostragemAleatoria();
					sup = new SupervisaoHumana(amostragem, gerenciador);
				} 
				else{
					sup = new SupervisaoArquivo(gerenciador, config.getCSVResposta(), config.getVotacaoMaioria());
				}

				ConjuntoDados conjDados = sup.geraConjuntoTreino();
				System.out.println(conjDados.getIndiceRespostasExistentes().size() + " respostas existentes no treino.");
				
				Regressao regressao = new RegressaoLogistica();
				ValidacaoCruzada vc = new ValidacaoCruzada(regressao, conjDados, porcentagemTeste);
				Avaliador a = vc.avalia();
				a.avalia(Constantes.LimiarPadrao);
				System.out.println(a);
				
				List<Elemento> baseClassificada = gerenciador.classificaBase(regressao, Constantes.LimiarPadrao, Constantes.nomeColunaClassificacao);
				SaidaCSV saida = config.getCSVClassificao();
				saida.salva(baseClassificada);
				
				geraGrafico(a, 0.01);
				//sr.salvaPesos(regressao, config.getArquivoRegressao());
			}
			else{
				RegressaoLinear regressao = sr.carregaPesos(config.getArquivoRegressao());

			}
			//TODO salvar base
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void analiseAlgoritmo(Regressao regressao, ConjuntoDados conjDados){
		System.out.println(regressao.getClass().getName());
		ValidacaoCruzada vc = new ValidacaoCruzada(regressao, conjDados, porcentagemTeste);
		Avaliador a = vc.avalia();
		a.avalia(0.5);
		System.out.println();
		System.out.println(a);
		
	}
	static String separador = "\t";
	static void geraGrafico(Avaliador a, double passo){
		System.out.println("Limiar"+separador+"Precisao"+separador+"Recall"+separador+"F1");
		for (double i = 0.0; i <= 1; i += passo){
			a.avalia(i);
			System.out.println(i + separador + a.precisaoPositiva() + separador + a.recallPositiva() + separador + a.f1MeasurePositiva());
		}
	}
}
