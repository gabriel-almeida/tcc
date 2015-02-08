import java.io.IOException;

import modelo.ConjuntoDados;
import processamento.PreProcessamento;
import aprendizado.Regressao;
import aprendizado.Supervisao;
import aprendizado.SupervisaoArquivo;
import aprendizado.SupervisaoHumana;
import avaliacao.Avaliador;
import avaliacao.ValidacaoCruzada;
import entrada.AmostragemAleatoria;
import entrada.ArquivoConfiguracao;
import entrada.EntradaCSV;
import entrada.GerenciadorBases;
import extracaoFeatures.ExtratorFeatures;
import extracaoFeatures.IgualdadeNome;

public class Main {
	public static double extraiPorcentagem(String s){
		double d = Double.parseDouble(s);
		if (d > 100 || d < 0){
			throw new RuntimeException("Esperado porcentagem entre 0 e 100, recebido " + d);
		}
		return d;
	} 
	public static void main(String[] args) {
		String arqConfiguracao = "configuracao.txt";
		double porcentagemTeste = 0.10;
		boolean supervisaoHumana = false;

		for (int i = 0; i< args.length; i++){
			String argumento = args[i];
			if (argumento.equals("-teste")){
				String s = args[++i];
				porcentagemTeste = extraiPorcentagem(s);
			}
			else if (argumento.equals("-configuracao")){
				arqConfiguracao = args[++i];
			}
			else if (argumento.equals("-supervisao-humana")){
				supervisaoHumana = true;
			}
			else if (argumento.equals("-apenas-matching")){
				//TODO carregar regressao do arquivo
			}
			else {
				System.out.println("HELP!");
			}
		}
		try {
			ArquivoConfiguracao config = new ArquivoConfiguracao(arqConfiguracao);

			EntradaCSV entradaBase1 = config.getCSVBase1();
			EntradaCSV entradaBase2 = config.getCSVBase2();
			EntradaCSV entradaResposta = config.getCSVResposta();
			
			IgualdadeNome igualdade = new IgualdadeNome();
			
			PreProcessamento preprocessamento = config.getPreprocessamento();
			ExtratorFeatures extratorFeatures = new ExtratorFeatures(preprocessamento);

			GerenciadorBases gerenciador = new GerenciadorBases(entradaBase1, entradaBase2, igualdade, extratorFeatures);
			gerenciador.pareiaBasesComChave();

			Supervisao sup;
			if (supervisaoHumana || entradaResposta == null){
				AmostragemAleatoria amostragem = new AmostragemAleatoria();
				sup = new SupervisaoHumana(amostragem, gerenciador);
			} 
			else{
				sup = new SupervisaoArquivo(entradaResposta, gerenciador);
			}

			ConjuntoDados conjDados = sup.geraConjuntoTreino();
			Regressao regressao = new Regressao();

			ValidacaoCruzada vc = new ValidacaoCruzada(regressao, conjDados, porcentagemTeste);
			Avaliador a = vc.avalia();
			a.avalia(0.5);
			System.out.println(a.acuracia());
			geraGrafico(a, 0.1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static void geraGrafico(Avaliador a, double passo){
		System.out.println("Precisao\tRecall\tF1");
		for (double i = 0.0; i < 1; i += passo){
			a.avalia(i);
			System.out.println(a.precisaoPositiva() + "\t" + a.recallPositiva() + "\t" + a.f1MeasurePositiva());
		}
	}
	/*public static void main(String args[]){
		List<String> configuracao1 = new ArrayList<String>();
		configuracao1.add("no_resp");
		configuracao1.add("no_mae_resp");
		configuracao1.add("da_nascimento_resp");
		List<String> configuracao2 = new ArrayList<String>();
		configuracao2.add("nome");
		configuracao2.add("nome_mae");
		configuracao2.add("data_nascimento");
		//(arq1, configuracao1, "nu_basico_cpf_cgc");
		//arq2, configuracao2, "cpf"
		//Supervisao s = new Supervisao();
		//s.preparaBases("PessoasFisicaSIAPA.csv", "PessoasFisicasRFB.csv");
	}*/
}
