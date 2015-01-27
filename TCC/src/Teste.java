import java.util.ArrayList;

import modelo.ConjuntoDados;


public class Teste {

	public static void main(String[] args) {
		ConjuntoDados cd = new ConjuntoDados();
		ArrayList<Double> amostra = new ArrayList<Double>();
		amostra.add(1.0);
		amostra.add(2.0);
		cd.adicionaAmostra(amostra);
		ArrayList<Double> amostra2 = new ArrayList<Double>();
		amostra2.add(3.0);
		amostra2.add(4.0);
		cd.adicionaAmostra(amostra2);
		ArrayList<Double> amostra3 = new ArrayList<Double>();
		amostra3.add(3.0);
		amostra3.add(4.0);
		cd.adicionaAmostra(amostra3);
		System.out.println(cd.getTreino());
	}

}
