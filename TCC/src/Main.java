import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import modelo.ConjuntoDados;
import modelo.Elemento;

import org.jblas.DoubleMatrix;

import supervisao.Supervisao;
import supervisao.SupervisaoArquivo;
import supervisao.SupervisaoHumana;
import utilidades.Constantes;
import utilidades.Matriz;
import aprendizado.MetricaRegressao;
import aprendizado.Regressao;
import aprendizado.RegressaoLogistica2;
import avaliacao.Avaliador;
import avaliacao.TesteConfianca;
import avaliacao.ValidacaoCruzada;
import desduplicacao.Desduplicador;
import entrada_saida.AmostragemAleatoria;
import entrada_saida.ArquivoConfiguracao;
import entrada_saida.EntradaCSV;
import entrada_saida.GerenciadorBases;
import entrada_saida.SaidaCSV;
import entrada_saida.SerializacaoRegressao;
import extracaoFeatures.ExtratorFeatures;

public class Main {
	public static String arqConfiguracao = "configuracao.txt";
	public static double porcentagemTeste = Constantes.PORCENTAGEM_TESTE_PADRAO;
	public static double limiarClassificacao = Constantes.LIMIAR_PADRAO;
	public static int repeticoesTesteConfianca = Constantes.REPETICOES_TESTE_CONFIANCA;
	public static double toleranciaDesduplicacao = Constantes.TOLERANCIA_PADRAO_DESDUPLICACAO;
	
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
		boolean supervisaoHumana  = false;
		boolean carregarRegressao = false; 
		boolean supervisaoArquivo = true;

		boolean matching = false;
		boolean localizaDuplicatas = false;
		boolean testeConfianca = true;
		boolean localizaEquivalencia = false;

		SerializacaoRegressao sr = new SerializacaoRegressao();

		//Parseia argumentos de ARGS
		/*for (int i = 0; i< args.length; i++){
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
		}*/
		try {
			ArquivoConfiguracao config = new ArquivoConfiguracao(arqConfiguracao);

			EntradaCSV entradaBase1 = config.getCSVBase1();
			EntradaCSV entradaBase2 = config.getCSVBase2();

			ExtratorFeatures extratorFeatures = config.getExtrator();

			GerenciadorBases gerenciador = new GerenciadorBases(entradaBase1, entradaBase2, Constantes.COMPARADOR_ELEMENTO_PADRAO, extratorFeatures);
			gerenciador.pareiaBasesParalelo();

			Supervisao sup;

			if (supervisaoArquivo){
				sup = new SupervisaoArquivo(gerenciador, config.getCSVResposta(), config.getVotacaoMaioria());
				sup.geraConjuntoTreino();
			}

			if (supervisaoHumana){
				AmostragemAleatoria amostragem = new AmostragemAleatoria();
				sup = new SupervisaoHumana(amostragem, gerenciador);
				sup.geraConjuntoTreino();
			}

			ConjuntoDados conjDados = gerenciador.getConjuntoDados();
			System.out.println(conjDados.getIndiceRespostasExistentes().size() + " respostas existentes no treino.");
			
			
			//DUMP DAS MATRIZES
			DoubleMatrix ds = Matriz.geraMatriz(conjDados.getIndiceRespostasExistentes(), conjDados);
			DoubleMatrix target = Matriz.geraVetorResposta(conjDados.getIndiceRespostasExistentes(), conjDados);
			
			Files.write(Paths.get("./dataset.txt"), ds.toString().getBytes());
			Files.write(Paths.get("./target.txt"), target.toString().getBytes());
						
			//PREPARA TREINO
			Regressao regressao = Constantes.REGRESSAO_PADRAO;
			ValidacaoCruzada vc = new ValidacaoCruzada(regressao, conjDados, porcentagemTeste);
			
			Avaliador a;
			/*Avaliador a = vc.avalia("Curva.png");
			a.avalia(Constantes.LIMIAR_PADRAO);
			System.out.println(a.toString());
			*/
			
			for (int i=0; i< 1; i++){
				regressao = new RegressaoLogistica2();
				vc = new ValidacaoCruzada(regressao, conjDados, porcentagemTeste);
				a = vc.avalia("curva_aprendizado" + (i+1) + ".png");
				a.avalia(Constantes.LIMIAR_PADRAO);
				System.out.println(a.toString());
			}
			
			if (testeConfianca){
				TesteConfianca teste = new TesteConfianca(repeticoesTesteConfianca, vc, limiarClassificacao);
				teste.calculaConfianca();
				List<Double> limiares = Arrays.asList(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);
				//teste.estatiticasBrutas(limiares);
				teste.plota(limiares);
				System.out.println(teste.toString());
			}
			
		
			if (matching){
				List<Elemento> baseClassificada = gerenciador.classificaBase(regressao, limiarClassificacao, Constantes.PREENCHIMENTO_PADRAO, Constantes.CATEGORIZACAO_PADRAO);
				SaidaCSV saida = config.getCSVClassificao();
				saida.salva(baseClassificada);
			}
			
			MetricaRegressao metrica = new MetricaRegressao(regressao, extratorFeatures);
			Desduplicador desduplicador = new Desduplicador(gerenciador.getBase1(), metrica, toleranciaDesduplicacao); //TODO escolher base principal
			
			if (localizaDuplicatas){
				desduplicador.localizaDuplicatasBasePrincipal();
			}
			
			if (localizaEquivalencia){
				desduplicador.localizaSimilares(gerenciador.getBase2());
			}
			
			//sr.salvaPesos(regressao, config.getArquivoRegressao());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
