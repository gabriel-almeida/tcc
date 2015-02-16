package entrada_saida;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import modelo.Elemento;
import utilidades.AnalisePerformace;

public class EntradaCSV {
	private static int tamMaxChave = 9;

	private String delimitador = "\";\"";
	private List<String> colunasRelevantes;
	private List<String> tipoColunas;
	private String arqCsv;

	private String colunaChave;
	private int indiceChave;
	private Map<String, Integer> colunasMapeadas;

	public EntradaCSV(String arqCsv, List<String> colunasRelevantes, List<String> tipoColunas, String colunaChave) {
		this.colunasRelevantes = colunasRelevantes;
		this.colunaChave = colunaChave;
		this.tipoColunas = tipoColunas;
		this.arqCsv = arqCsv;
		if (colunasRelevantes.size() != tipoColunas.size()){
			throw new RuntimeException("Tamanhos inconsistentes de coluna.");
		}
	}

	public String getDelimitador() {
		return delimitador;
	}
	public void setDelimitador(String delimitador) {
		this.delimitador = delimitador;
	}

	public List<String> getColunasRelevantes() {
		return colunasRelevantes;
	}

	public List<String> getTipoColunas() {
		return tipoColunas;
	}

	public String getColunaChave() {
		return colunaChave;
	}
	public String getNomeArquivo() {
		return arqCsv;
	}
	/**
	 * Implementacao concorrente da leitura de CSV. 
	 * Caso exista uma repeticao de chaves, os elementos afetados serao impressos e apenas um deles sera usado aleatoriamente
	 * O mapa retornado eh thread-safe
	 * */
	@SuppressWarnings("resource")
	public Map<String, Elemento> leCsv() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(arqCsv));

		this.colunasMapeadas = new HashMap<String, Integer>();
		
		//LOG tempo
		AnalisePerformace tempo = new AnalisePerformace();
		
		this.parseiaCabecalho(br.readLine());
		
		Map<String, Elemento> resultado = br.lines().parallel()
				.map(this::parseiaLinha)
				.collect(Collectors.toConcurrentMap(Elemento::getChave, Function.identity(), 
						(chaveA, chaveB) -> {
							System.out.println("Repetido: " + chaveA + " -> " + chaveB); //Essa funcao eh chamada quando ha uma colisao de chaves
							return chaveB;
						} ));

		//Log Tempo
		tempo.capturaTempo(resultado.size());
		tempo.imprimeEstatistica("Lendo " + arqCsv);

		br.close();
		return resultado;
	}

	private Elemento parseiaLinha(String linha){
		String campos[] = linha.split(delimitador);

		String chave = campos[indiceChave].replaceAll("\"","").substring(0, tamMaxChave); //TODO rever esse substring 
		Elemento e = new Elemento(chave, this.colunasRelevantes, this.tipoColunas);
		
		this.colunasRelevantes.stream()
			.forEach(colunaAtual -> e.addElemento(colunaAtual, campos[this.colunasMapeadas.get(colunaAtual)].replaceAll("\"", "")) );
		
		return e;
	}
	private void parseiaCabecalho(String s){
		List<String> cabecalho = Arrays.stream(s.split(delimitador))
				.map(campo -> campo.replaceAll("\"", ""))
				.collect(Collectors.toList());
		
		this.indiceChave = cabecalho.indexOf(this.colunaChave);
		
		this.colunasMapeadas = this.colunasRelevantes.stream()
			.collect(Collectors.toMap(Function.identity(), coluna -> cabecalho.indexOf(coluna)));
	}

}
