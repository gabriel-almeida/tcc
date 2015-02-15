package utilidades;

import java.util.ArrayList;
import java.util.List;

public class AnalisePerformace {
	private static List<Integer> contador = new ArrayList<Integer>();
	private static List<Long> tempo = new ArrayList<Long>();
	public static void capturaTempo(int contadorAtual){
		tempo.add(System.currentTimeMillis());
		contador.add(contadorAtual);
	}
	public static void imprimeEstatistica(String operacao){
		//int ultimoIndice = tempo.size() - 1;
		double media = 0.0;
		double pior = Double.MAX_VALUE;
		double melhor = Double.MIN_VALUE;
		for (int i = 1; i < contador.size(); i++){
			long t1 = tempo.get(i);
			long t0 = tempo.get(i - 1);
			long deltaT = (t1 - t0);
			
			int cont1 = contador.get(i);
			int cont0 =contador.get(i - 1);
			int deltaC = cont1 - cont0;
			
			double performace = 1000.0 * deltaC/deltaT;
			if (performace > melhor){
				melhor = performace;
			}
			if (performace < pior){
				pior = performace;
			}
			media = media*0.99 + (performace - media) * (1.0/i);
			//media += performace;
		}
		//media /= (tempo.size()-1);
		System.out.println( "Min " + leituraHumana(pior) + " / Med " + leituraHumana(media) + " / Max " + leituraHumana(melhor)+ " operacoes por segundo | " + operacao + " em " + (tempo.get(tempo.size() - 1) - tempo.get(0))/1000.0 + " segundos.");
		//tempo.set(tempo.size() - 1, System.currentTimeMillis()); //torna a prox medicao mais justa, removendo o tempo de execucao dessa rotina
	}
	
	private static double divisor = 1000;
	private static String leituraHumana(double valor){
		String unidade = "";
		double conversao = 1.0*valor;
		if (conversao > divisor){
			conversao /= divisor;
			unidade = "K";
			if (conversao > divisor){
				conversao /= divisor;
				unidade = "M";
				if (conversao > divisor){
					conversao /= divisor;
					unidade = "G";	
				}
			}
		}
		return Math.round(conversao) + " " + unidade;
	}
	
	public static void zera(){
		contador.clear();
		tempo.clear();
	}
}
