package entrada_saida;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import modelo.ConjuntoDados;
import modelo.Elemento;
import utilidades.AnalisePerformace;
import utilidades.Constantes;
import aprendizado.MetricaRegressao;
import aprendizado.Regressao;
import desduplicacao.ArvoreBK;
import extracaoFeatures.ExtratorFeatures;

public class GerenciadorBases {
	private EntradaCSV entradaBase1;
	private EntradaCSV entradaBase2;
	private BiPredicate<Elemento, Elemento> condicaoIgualdade;
	private ExtratorFeatures extrator;

	//TODO talvez tres hashmaps sejam muito custosos
	private Map<String, Integer> indiceChave;
	private Map<String, Elemento> base1;
	private Map<String, Elemento> base2;
	private List<String> chavesNoConjuntoDados;
	private ConjuntoDados dataset;

	public GerenciadorBases(EntradaCSV entradaBase1, EntradaCSV entradaBase2,
			BiPredicate<Elemento, Elemento> condicaoIgualdade, ExtratorFeatures extrator) {
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

		AnalisePerformace tempo = new AnalisePerformace();

		chavesComuns.parallelStream().forEach((String chave) -> {
			Elemento e1 = this.base1.get(chave); 
			Elemento e2 = this.base2.get(chave);

			boolean iguais = condicaoIgualdade.test(e1, e2);

			if (!iguais){
				ArrayList<Double> features = extrator.extrai(e1, e2);
				this.dataset.adicionaAmostra(features);
				this.indiceChave.put(chave, this.indiceChave.size());
				this.chavesNoConjuntoDados.add(chave);
			}			
		});
		tempo.capturaTempo(chavesComuns.size());
		tempo.imprimeEstatistica("Pareamento Paralelo");
	}

	public void desduplica(Regressao r){
		MetricaRegressao metrica = new MetricaRegressao(r, extrator);
		BiFunction<Elemento, Elemento, Integer> funcao = metrica.geraFuncaoMetricaSimilaridade(30); //TODO magic
		ArvoreBK<Elemento> arvore = new ArvoreBK<Elemento>(funcao);
		arvore.adicionaElementos(this.base1.values());

		System.out.println("Profundidade = " + arvore.getProfundidade() + " Numero Nos = " + arvore.getNumeroNos());
		System.out.println(arvore.media());

		/*AnalisePerformace.zera();
		AnalisePerformace.capturaTempo(0);
		List<List<Elemento>> a = this.base1.values().stream().parallel().map(e -> arvore.busca(e, 0)).filter(lista -> lista.size() > 1).collect(Collectors.toList());
		AnalisePerformace.capturaTempo(1000000);
		AnalisePerformace.imprimeEstatistica("Busca Arvore BK");

		System.out.println(a.size() );
		System.out.println(a);*/
	}

	public List<Elemento> classificaBase(Regressao r, double limiar, BiFunction<Elemento, Elemento, Elemento> funcaoPreenchimento, Function<Double, String> funcaoCategorizacao){
		int tamanhoBase = this.dataset.tamanho();

		//Log tempo
		AnalisePerformace tempo = new AnalisePerformace();

		List<Integer> indices = IntStream.range(0, tamanhoBase).boxed().collect(Collectors.toList()); //lista com todos os indices
		List<Double> classificacaoBase = r.classifica(this.dataset, indices);

		List<Elemento> resultado = IntStream.range(0, tamanhoBase).parallel()
				.mapToObj( i -> {
					String chave = this.chavesNoConjuntoDados.get(i);
					double classificacaoAtual = classificacaoBase.get(i);
		
					//Gero o elemento basico apartir das duas bases
					Elemento elementoSaida = funcaoPreenchimento.apply(this.base1.get(chave), this.base2.get(chave)); 
					
					//Adiciona conteudo extra ao Elemento, relativo a classificacao 
					elementoSaida.adicionaColuna(Constantes.NOME_COLUNA_CERTEZA_CLASSIFICACAO, Constantes.TIPO_COLUNA_CERTEZA_CLASSIFICACAO);
					elementoSaida.addElemento(Constantes.NOME_COLUNA_CERTEZA_CLASSIFICACAO, Double.toString(classificacaoAtual));
					if (funcaoCategorizacao != null){
						elementoSaida.adicionaColuna(Constantes.NOME_COLUNA_CATEGORIA_CLASSIFICACAO, Constantes.TIPO_COLUNA_CATEGORIA_CLASSIFICACAO);
						elementoSaida.addElemento(Constantes.NOME_COLUNA_CATEGORIA_CLASSIFICACAO, funcaoCategorizacao.apply(classificacaoAtual));
					}
					
					return elementoSaida;
				}).collect(Collectors.toList());
		
		//Log tempo
		tempo.capturaTempo(resultado.size());
		tempo.imprimeEstatistica("Classificando e gerando arquivo de saida.");
		
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