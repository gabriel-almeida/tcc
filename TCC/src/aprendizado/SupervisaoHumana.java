package aprendizado;

import java.util.List;
import java.util.Scanner;

import modelo.ConjuntoDados;
import modelo.Elemento;
import entrada.AmostragemAleatoria;
import entrada.GerenciadorBases;

public class SupervisaoHumana implements Supervisao{
	private AmostragemAleatoria amostragem;
	private GerenciadorBases gerenciador;
	private int tamLoteAmostra = 10;

	public SupervisaoHumana(AmostragemAleatoria amostragem, GerenciadorBases gerenciador) {
		this.amostragem = amostragem;
		this.gerenciador = gerenciador;
	}
	public void setTamLoteAmostra(int tam){
		this.tamLoteAmostra = tam;
	}

	@Override
	public ConjuntoDados geraConjuntoTreino(){
		Scanner sc = new Scanner(System.in);
		ConjuntoDados conjDadosAtual = gerenciador.getConjuntoDados();
		List<Integer> indicesAmostra = amostragem.amostra(tamLoteAmostra, conjDadosAtual);
		for (int indice : indicesAmostra){
			Elemento e1 = gerenciador.getElemento1(indice);
			Elemento e2 = gerenciador.getElemento2(indice);
			
			//TODO Melhorar sysout
			System.out.println("Chave = " + e1.getChave() );
			System.out.println("Base 1:");
			System.out.println(e1);
			System.out.println("Base 2:");
			System.out.println(e2);

			do{
				System.out.println("Mesmo individuo? S/N");
				String s = sc.nextLine().trim().toLowerCase();
				if (s.equals("s")){
					gerenciador.setResposta(indice, 1.0);
				}
				else if (s.equals("n")){
					gerenciador.setResposta(indice, 0.0);
				}
				else{
					continue;
				}
			} while (false);
		}
		sc.close();
		return gerenciador.getConjuntoDados();
	}

}
