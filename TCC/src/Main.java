import modelo.ConjuntoDados;
import aprendizado.Regressao;
import aprendizado.Supervisao;
import avaliacao.Avaliador;
import avaliacao.ValidacaoCruzada;

public class Main {

	public static void main(String[] args) {
		
			Supervisao s = new Supervisao();
			double teste = 0.10;
			
			ConjuntoDados conjDados = s.supervisaoHumana();
			Regressao regressao = new Regressao();
			
			ValidacaoCruzada vc = new ValidacaoCruzada(regressao, conjDados, teste);
			Avaliador a = vc.avalia();
			a.avalia(0.5);
			System.out.println(a.acuracia());
			geraGrafico(a, 0.1);
	}
	static void geraGrafico(Avaliador a, double passo){
		System.out.println("Precisao\tRecall\tF1");
		for (double i = 0.0; i < 1; i += passo){
			a.avalia(i);
			System.out.println(a.precisaoPositiva() + "\t" + a.recallPositiva() + "\t" + a.f1MeasurePositiva());
		}
	}

}
