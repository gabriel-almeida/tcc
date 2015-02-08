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
	
	public SupervisaoArquivo(EntradaCSV respostasCsv, GerenciadorBases gerenciador) throws IOException{
		this.gerenciador = gerenciador;
		Map<String, Elemento> resposta = respostasCsv.leCsv();
		for (String chave : resposta.keySet()){
			Elemento elementoResposta = resposta.get(chave);
			double target = criterio.criterio(elementoResposta);

			this.gerenciador.setResposta(chave, target);
		}
	}

	@Override
	public ConjuntoDados geraConjuntoTreino(){
		return this.gerenciador.getConjuntoDados();
	}
}
