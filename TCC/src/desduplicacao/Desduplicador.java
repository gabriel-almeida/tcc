package desduplicacao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import modelo.Elemento;
import utilidades.AnalisePerformace;
import aprendizado.MetricaRegressao;

public class Desduplicador {

	private Collection<Elemento> basePrincipal;
	private BiFunction<Elemento, Elemento, Integer> metricaDiscretizada;
	private MetricaRegressao metrica;

	private ArvoreBK<Elemento> arvore;
	private int toleranciaArvoreBK = 0;
	private double toleranciaDesduplicacao;
	public Desduplicador(Collection<Elemento> basePrincipal, MetricaRegressao metrica, double toleranciaDesduplicacao){
		int quantBlocoDiscretizacao = (int) Math.round(1.0/toleranciaDesduplicacao);
		metricaDiscretizada = metrica.geraFuncaoMetricaSimilaridade(quantBlocoDiscretizacao);
		this.basePrincipal = basePrincipal;
		this.metrica = metrica;
		this.toleranciaDesduplicacao = toleranciaDesduplicacao;
	}

	public void qualidadeDesduplicacao(List<List<Elemento>> grupos){
		AnalisePerformace tempo = new AnalisePerformace();

		DoubleSummaryStatistics sumario = grupos.parallelStream().flatMapToDouble(listaAtual -> qualidadeGrupo(listaAtual).stream().mapToDouble(d -> d)).summaryStatistics();
		double media = sumario.getAverage();
		double minimo = sumario.getMin();
		double porcentagemMediaTolerancia = (1.0 - media) / toleranciaDesduplicacao;
		double porcentagemPiorCasoTolerancia = (1.0 - minimo) / toleranciaDesduplicacao;

		//Log Tempo
		tempo.capturaTempo(grupos.size());
		tempo.imprimeEstatistica("Calculo de qualidade das desduplicacoes.");

		System.out.println("Media dos grupos: " + media + " | " + porcentagemMediaTolerancia*100 + "% da tolerancia.");
		System.out.println("Pior caso dos grupos: " + minimo + " | " + porcentagemPiorCasoTolerancia*100 + "% da tolerancia.");
	}
	private List<Double> qualidadeGrupo(List<Elemento> lista){
		List<Double> resultado = new ArrayList<Double>();
		for (Elemento e1: lista){
			for (Elemento e2: lista){
				if (e1!=e2){
					double similaridade = this.metrica.medidaSimilaridade(e1, e2);
					resultado.add(similaridade);
				}
			}
		}
		return resultado;
		//return lista.stream().flatMapToDouble( e1 -> lista.stream().filter(e2 -> e1 != e2).mapToDouble( e2 -> this.metrica.medidaSimilaridade(e1, e2))).summaryStatistics();
	}

	public  List<List<Elemento>> unificaGruposDuplicatas(List<List<Elemento>> grupos){
		AnalisePerformace tempo = new AnalisePerformace();
		ConjuntoDisjunto<Elemento> conjDisjunto = new ConjuntoDisjunto<Elemento>();
		conjDisjunto.adicionaConjuntos(grupos);
		List<List<Elemento>> novo = conjDisjunto.getConjuntos().stream().map( s -> new ArrayList<Elemento>(s)).collect(Collectors.toList());
		tempo.capturaTempo(grupos.size());
		tempo.imprimeEstatistica("Unificacao dos grupos");
		return novo;
	}

	public void constroiArvore(){
		this.arvore = new ArvoreBK<Elemento>(this.metricaDiscretizada);
		this.arvore.adicionaElementos(basePrincipal);

		System.out.println("Estatisticas da Arvore BK\nProfundidade = " + arvore.getProfundidade() + "\nNumero Nos = " + arvore.getNumeroNos());
		System.out.println("Frequencias de uso dos Espacos " + arvore.frequenciaUsoIndices());
	}
	public void localizaDuplicatasBasePrincipal(){
		if (arvore == null){
			constroiArvore();
		}

		List<List<Elemento>> dup = arvore.possiveisDuplicatas();

		System.out.println("Localizados " + dup.size() + " grupos de duplicatas");
		System.out.println(dup.stream().mapToInt( l -> l.size()).summaryStatistics().toString());
		this.qualidadeDesduplicacao(dup);
		

		dup = this.unificaGruposDuplicatas(dup);
		System.out.println("Unificado para  " + dup.size() + " grupos de duplicatas");
		System.out.println(dup.stream().mapToInt( l -> l.size()).summaryStatistics().toString());
		
		this.qualidadeDesduplicacao(dup);

		dup.stream().forEach(
				l -> {
					System.out.println();
					System.out.println( qualidadeGrupo(l).stream().mapToDouble(d->d).min() ); 
					l.stream().forEach(e -> System.out.println(e));
				});
	}

	public void localizaSimilares(Collection<Elemento> baseComparada){
		if (arvore == null){
			constroiArvore();
		}
		AnalisePerformace tempo = new AnalisePerformace();
		Map<Elemento, List<Elemento>> pares = this.arvore.busca(baseComparada, this.toleranciaArvoreBK);
		tempo.capturaTempo(pares.size());
		tempo.imprimeEstatistica("Localizando Elemento Similares entre as duas bases");

		for (Elemento e: pares.keySet()){
			List<Elemento> candidatos = pares.get(e);
			System.out.println();
			System.out.println("Elemento : " + e);
			System.out.println("Candidatos : ");
			for (Elemento candidato: candidatos){
				System.out.println( "\t" + candidato);
			}
		}
	}

}
