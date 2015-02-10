package aprendizado;

import java.io.IOException;
import java.util.Map;

import modelo.ConjuntoDados;
import modelo.Elemento;
import entrada.EntradaCSV;
import entrada.GerenciadorBases;

public class SupervisaoArquivo implements Supervisao{
	private CriterioVotacao criterio;
	private GerenciadorBases gerenciador;
	private EntradaCSV entrada;
	
	public SupervisaoArquivo(GerenciadorBases gerenciador, EntradaCSV entrada, CriterioVotacao criterio) throws IOException{
		this.gerenciador = gerenciador;
		this.criterio = criterio;
		this.entrada = entrada;
	}

	@Override
	public ConjuntoDados geraConjuntoTreino(){
		try {
			Map<String, Elemento> resposta = entrada.leCsv();
			for (String chave : resposta.keySet()){
				Elemento elementoResposta = resposta.get(chave);
				double target = criterio.criterio(elementoResposta);

				this.gerenciador.setResposta(chave, target);
			}
			return this.gerenciador.getConjuntoDados();

		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
}
