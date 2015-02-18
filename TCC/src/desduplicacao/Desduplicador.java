package desduplicacao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import modelo.Elemento;
import aprendizado.MetricaRegressao;

public class Desduplicador {
	
	private Collection<Elemento> basePrincipal;
	private BiFunction<Elemento, Elemento, Integer> funcao;
	
	private ArvoreBK<Elemento> arvore;
	private int toleranciaArvoreBK = 0;
	
	public Desduplicador(Collection<Elemento> basePrincipal, MetricaRegressao metrica, double toleranciaDesduplicacao){
		int quantBlocoDiscretizacao = (int) Math.round(1.0/toleranciaDesduplicacao);
		funcao = metrica.geraFuncaoMetricaSimilaridade(quantBlocoDiscretizacao);
		this.basePrincipal = basePrincipal;
	}
	
	public void constroiArvore(){
		this.arvore = new ArvoreBK<Elemento>(this.funcao);
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
		dup.stream().forEach(l -> {l.stream().forEach( e -> System.out.println(e)); System.out.println();});
	}
	
	public void localizaSimilares(Collection<Elemento> baseComparada){
		if (arvore == null){
			constroiArvore();
		}
		
		Map<Elemento, List<Elemento>> pares = this.arvore.busca(baseComparada, this.toleranciaArvoreBK);
		
		for (Elemento e: pares.keySet()){
			List<Elemento> candidatos = pares.get(e);
			if (candidatos.size() != 0){
				System.out.println("Elemento : " + e);
				System.out.println("Candidatos : ");
				for (Elemento candidato: candidatos){
					System.out.println( "\t" + candidato);
				}
			}
		}
	}
	
}
