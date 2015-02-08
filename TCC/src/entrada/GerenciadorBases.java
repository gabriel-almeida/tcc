package entrada;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aprendizado.Regressao;
import modelo.ConjuntoDados;
import modelo.Elemento;
import extracaoFeatures.CondicaoIgualdade;
import extracaoFeatures.ExtratorFeatures;

public class GerenciadorBases {
	private EntradaCSV entradaBase1;
	private EntradaCSV entradaBase2;
	private CondicaoIgualdade condicaoIgualdade;
	private ExtratorFeatures extrator;

	//TODO talvez tres hashmaps sejam muito custosos
	private Map<String, Integer> indiceChave;
	private Map<String, Elemento> base1;
	private Map<String, Elemento> base2;
	private List<String> chavesNoConjuntoDados;
	private ConjuntoDados dataset;
	
	public GerenciadorBases(EntradaCSV entradaBase1, EntradaCSV entradaBase2,
			CondicaoIgualdade condicaoIgualdade, ExtratorFeatures extrator) {
		super();
		this.entradaBase1 = entradaBase1;
		this.entradaBase2 = entradaBase2;
		this.condicaoIgualdade = condicaoIgualdade;
		this.extrator = extrator;

		//inicializa variaveis
		this.base1 = new HashMap<String, Elemento>();
		this.base2 = new HashMap<String, Elemento>();
		this.dataset = new ConjuntoDados();
		this.indiceChave = new HashMap<String, Integer>();
		this.chavesNoConjuntoDados = new ArrayList<String>();
	}

	//TODO melhorar nomes
	public Elemento getElementoConjuntoDados1(int indice){
		String chave = this.chavesNoConjuntoDados.get(indice);
		return base1.get(chave);
	}
	public Elemento getElementoConjuntoDados2(int indice){
		String chave = this.chavesNoConjuntoDados.get(indice);
		return base2.get(chave);
	}

	public ExtratorFeatures getExtrator() {
		return extrator;
	}

	public ConjuntoDados getConjuntoDados(){
		return this.dataset;
	}


	/**
	 * Pareio duas bases usando suas chaves como criterio, carregando o extrator de estatisticas.
	 * Apenas elementos ditos como diferentes sao usados
	 * @throws IOException 
	 * */
	public void pareiaBasesComChave() throws IOException{
		this.base1 = entradaBase1.leCsv();
		this.base2 = entradaBase2.leCsv();

		Set<String> chavesBase1 = this.base1.keySet();
		Set<String> chavesBase2 = this.base2.keySet();

		chavesBase1.retainAll(chavesBase2);
		Set<String> chavesComuns = chavesBase1; //legibilidade

		int i=0;
		//TODO Paralelizar este FOR deve ter bons ganhos de velocidade
		for (String chave: chavesComuns){ 
			Elemento e1 = this.base1.get(chave); 
			Elemento e2 = this.base2.get(chave);

			boolean iguais = condicaoIgualdade.condicaoIgualdade(e1, e2);

			if (!iguais){
				ArrayList<Double> features = extrator.extrai(e1, e2);
				this.dataset.adicionaAmostra(features);
				this.indiceChave.put(chave, this.dataset.tamanho());
				this.chavesNoConjuntoDados.add(chave);
			}
			
			//LOG Progresso
			if (i % 10000 == 0){
				System.out.println(i);
			}
			i++;
		}
	}
	public void classificaBase(Regressao r){
		//TODO
	}
	
	public void setResposta(String chave, double resposta){
		int i = this.indiceChave.get(chave);
		this.dataset.setResposta(i, resposta);
	}
	public void setResposta(int i, double resposta){
		this.dataset.setResposta(i, resposta);
	}
}