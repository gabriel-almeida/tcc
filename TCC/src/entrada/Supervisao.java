package entrada;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import modelo.ConjuntoDados;
import modelo.Elemento;
import processamento.ExtratorFeatures;
import processamento.PreProcessamento;

public class Supervisao {
	
	private PreProcessamento preProcessamento;
	private ExtratorFeatures extrator;
	private AmostragemAleatoria amostragem;
	
	public Supervisao(){
		preProcessamento = new PreProcessamento();
		extrator = new ExtratorFeatures(preProcessamento);
	}
	
	public ConjuntoDados supervisaoHumana(){
		//TODO refatorar
		ConjuntoDados dados = extrator.getConjuntoDados();
		amostragem = new AmostragemAleatoria(dados);
		Scanner sc = new Scanner(System.in);
		
		List<Integer> indicesAmostra = amostragem.amostra(10);
		for (int indice : indicesAmostra){
			Elemento e1 = extrator.getElemento1(indice);
			Elemento e2 = extrator.getElemento2(indice);
			for (int i = 0; i < e1.tamanho(); i++){
				System.out.println("E1["+i+"]= " + e1.getElemento(i));
				System.out.println("E2["+i+"]= " + e2.getElemento(i));
			}
			do{
				System.out.println("Sim/N");
				String s = sc.nextLine().trim().toLowerCase();
				if (s.equals("s")){
					dados.setResposta(indice, 1.0);
				}
				else if (s.equals("n")){
					dados.setResposta(indice, 0.0);
				}
				else{
					continue;
				}
			} while (false);
		}
		sc.close();
		return dados;
	}
	public void preparaBases(String arq1, String arq2){
		try {
			
			
			List<String> configuracao1 = new ArrayList<String>();
			configuracao1.add("no_resp");
			configuracao1.add("no_mae_resp");
			configuracao1.add("da_nascimento_resp");
			List<String> configuracao2 = new ArrayList<String>();
			configuracao2.add("nome");
			configuracao2.add("nome_mae");
			configuracao2.add("data_nascimento");
			
			Map<String, Elemento> base1 = EntradaCSV.leCsv(arq1, configuracao1, "nu_basico_cpf_cgc");
			System.out.println("Carregado 1!");
			Map<String, Elemento> base2 = EntradaCSV.leCsv(arq2, configuracao2, "cpf");
			System.out.println("Carregado 2!");
			
			Set<String> chavesBase1 = base1.keySet();
			Set<String> chavesBase2 = base2.keySet();

			chavesBase1.retainAll(chavesBase2);
			Set<String> chavesComuns = chavesBase1; //legibilidade

			//TODO tentar usar o forEach novo do java 8
			//TODO possivelmente passar isso para alguma outra base
			int contador = 0;
			System.out.println("Extraindo");
			
			//TODO Paralelizar este FOR deve ter bons ganhos de velocidade
			for (String chave: chavesComuns){ 
				Elemento e1 = base1.get(chave); 
				Elemento e2 = base2.get(chave);
				extrator.extrai(e1, e2);

				contador++;
				if (contador % 10000 == 0){
					System.out.println(contador);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		Supervisao s = new Supervisao();
		s.preparaBases("PessoasFisicaSIAPA.csv", "PessoasFisicasRFB.csv");
	}
}
