package aprendizado;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import entrada.AmostragemAleatoria;
import entrada.EntradaCSV;
import extracaoFeatures.CondicaoIgualdade;
import extracaoFeatures.ExtratorFeatures;
import modelo.ConjuntoDados;
import modelo.Elemento;
import processamento.PreProcessamento;

public class Supervisao {
	
	private PreProcessamento preProcessamento;
	private ExtratorFeatures extrator;
	private AmostragemAleatoria amostragem;
	private EntradaCSV entradaBase1;
	private EntradaCSV entradaBase2;
	//TODO refatorar
	private CondicaoIgualdade condicaoIgualdade; 
	private CriterioVotacao criterio;
	
	public Supervisao(){
		//TODO REFAZER usando campos da classe
	}
	
	
	public ConjuntoDados supervisaoArquivo(EntradaCSV respostasCsv) throws IOException{
		pareiaBasesComChave();
		ConjuntoDados resultado = this.extrator.getConjuntoDados();
		Map<String, Elemento> resposta = respostasCsv.leCsv();
		for (String chave : resposta.keySet()){
			Elemento elementoResposta = resposta.get(chave);
			double target = criterio.criterio(elementoResposta);
			
			int indice = this.extrator.getIndice(chave);
			resultado.setResposta(indice, target);
		}
		return resultado;
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
	
	/**
	 * Pareio duas bases usando suas chaves como criterio, carregando o extrator de estatisticas.
	 * Apenas elementos ditos como diferentes sao usados
	 * */
	public void pareiaBasesComChave(){
		try {
			Map<String, Elemento> base1 = entradaBase1.leCsv();
			Map<String, Elemento> base2 = entradaBase2.leCsv();
			
			Set<String> chavesBase1 = base1.keySet();
			Set<String> chavesBase2 = base2.keySet();

			chavesBase1.retainAll(chavesBase2);
			Set<String> chavesComuns = chavesBase1; //legibilidade
			
			//TODO Paralelizar este FOR deve ter bons ganhos de velocidade
			for (String chave: chavesComuns){ 
				Elemento e1 = base1.get(chave); 
				Elemento e2 = base2.get(chave);
				
				//TODO pensar em trocar isso de lugar
				boolean iguais = this.condicaoIgualdade.condicaoIgualdade(e1, e2);
				
				if (!iguais){
					extrator.extrai(e1, e2);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
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
	}
}
