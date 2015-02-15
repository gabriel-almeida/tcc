package entrada_saida;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import desduplicacao.ArvoreBK;
import modelo.ConjuntoDados;
import modelo.Elemento;
import utilidades.AnalisePerformace;
import utilidades.Constantes;
import aprendizado.MetricaRegressao;
import aprendizado.Regressao;
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
		this.indiceChave = Collections.synchronizedMap(new HashMap<String, Integer>());
		this.chavesNoConjuntoDados = Collections.synchronizedList(new ArrayList<String>());
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
	
	public void pareiaBasesParalelo() throws IOException{
		this.base1 = entradaBase1.leCsv();
		this.base2 = entradaBase2.leCsv();

		Set<String> chavesBase1 = this.base1.keySet();
		Set<String> chavesBase2 = this.base2.keySet();

		chavesBase1.retainAll(chavesBase2);
		Set<String> chavesComuns = chavesBase1; //legibilidade
		
		AnalisePerformace.zera();
		AnalisePerformace.capturaTempo(0);
		
		chavesComuns.parallelStream().forEach((String chave) -> {
			Elemento e1 = this.base1.get(chave); 
			Elemento e2 = this.base2.get(chave);

			boolean iguais = condicaoIgualdade.condicaoIgualdade(e1, e2);

			if (!iguais){
				ArrayList<Double> features = extrator.extrai(e1, e2);
				this.dataset.adicionaAmostra(features);
				this.indiceChave.put(chave, this.indiceChave.size());
				this.chavesNoConjuntoDados.add(chave);
			}			
		});
		AnalisePerformace.capturaTempo(chavesComuns.size());
		AnalisePerformace.imprimeEstatistica("Pareamento Paralelo");
	}
	
	public void desduplica(Regressao r){
		MetricaRegressao metrica = new MetricaRegressao(r, extrator);
		ArvoreBK arvore = new ArvoreBK(metrica, 10);
		
		AnalisePerformace.zera();
		this.base1.values().stream().limit(1000).forEach(e -> arvore.adicionaElemento(e));
		
		List<List<Elemento>> a = this.base1.values().stream().limit(700).parallel().map(e -> arvore.busca(e, 1)).filter(lista -> lista.size() > 1).collect(Collectors.toList());
		System.out.println(a.size() );
		System.out.println(a);
	}
	
	public List<Elemento> classificaBase(Regressao r, double limiar, String nomeColunaClassificacao){
		int tamanhoBase = this.dataset.tamanho();

		//Gero lista com todos os indices
		List<Integer> indices = new ArrayList<Integer>();
		for (int i=0; i < tamanhoBase; i++){	
			indices.add(i);
		}
		
		List<Double> classificacaoBase = r.classifica(this.dataset, indices);
		
		List<Elemento> resultado = new ArrayList<Elemento>();
		for (int i = 0; i < tamanhoBase; i++){
			String chave = this.chavesNoConjuntoDados.get(i);
			double classificacaoAtual = classificacaoBase.get(i);
			
			Elemento e = new Elemento(chave, Arrays.asList(new String[]{"Elemento1 da Base 1", "Elemento 1 da Base 2", nomeColunaClassificacao})); //TODO Melhorar o metodo de claassificacao de base
			
			e.addElemento(0, this.base1.get(chave).getElemento(0));
			e.addElemento(1, this.base2.get(chave).getElemento(0));
			e.addElemento(2, classificacaoAtual > limiar? Constantes.stringIguais: Constantes.stringDiferentes);
			resultado.add(e);
		}
		return resultado;
	}
	
	public void setResposta(String chave, double resposta){
		Integer i = this.indiceChave.get(chave);
		//Caso a chave exista no conj de treino
		if (i != null){
			this.dataset.setResposta(i, resposta);
		}
	}
	public void setResposta(int i, double resposta){
		this.dataset.setResposta(i, resposta);
	}
	
	/*TODO refazer
	 * public void salvaRespostasAtuais(ArquivoConfiguracao config) throws IOException{
		EntradaCSV treinoCSV = config.getCSVResposta();
		
		String colunaChave = treinoCSV.getColunaChave();
		List<String> descritores = treinoCSV.getColunasRelevantes();
		String arqNome = treinoCSV.getNomeArquivo();
		
		SaidaCSV objSaida = new SaidaCSV(arqNome, colunaChave);
		
		List<Elemento> listaTreino = new ArrayList<Elemento>();
		
		List<Integer> supervisaoExistente = this.dataset.getIndiceRespostasExistentes();
		for (Integer indice: supervisaoExistente){
			String chave = this.chavesNoConjuntoDados.get(indice);
			
			Double respostaDada = this.dataset.getRespostaEsperada(indice);
			int votosPositivos = respostaDada == 1.0? 1 : 0;
			int votosNegativo = respostaDada == 0.0? 1 : 0;
			
			Elemento e = new Elemento(chave, descritores);
			e.addElemento(0, Integer.toString(votosPositivos));
			e.addElemento(1, Integer.toString(votosNegativo));
			listaTreino.add(e);
		}
		objSaida.salva(listaTreino);
	}*/
}