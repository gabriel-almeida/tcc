import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

import aprendizado.Regressao;
import avaliacao.Avaliador;
import avaliacao.ValidacaoCruzada;
import processamento.ExtratorFeatures;
import processamento.PreProcessamento;
import modelo.ConjuntoDados;
import modelo.Elemento;
import entrada.EntradaCSV;
import entrada.Supervisao;

public class Main {

	/*public static void main(String[] args) {
		
			Supervisao s = new Supervisao();
			String arq1;
			String arq2;
			double teste = 0.10;
			
			//ConjuntoDados conjDados = s.preparaBases(arq1, arq2, 10);
			Regressao regressao = new Regressao();
			
			ValidacaoCruzada vc = new ValidacaoCruzada(regressao, conjDados, teste);
			Avaliador a = vc.avalia();
			a.avalia(0.5);
			System.out.println(a.acuracia());
			geraGrafico(a, 0.1);
	}*/
	static void geraGrafico(Avaliador a, double passo){
		System.out.println("Precisao\tRecall\tF1");
		for (double i = 0.0; i < 1; i += passo){
			a.avalia(i);
			System.out.println(a.precisaoPositiva() + "\t" + a.recallPositiva() + "\t" + a.f1MeasurePositiva());
		}
	}

}
