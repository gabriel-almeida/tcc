package processamento;

public class ProcessadorData implements Processador {

	@Override
	public String processa(String s) {
		//TODO tentar melhorar e talvez inferir data
		String resultado = s.replaceAll("[^0-9]", "");
		return resultado;
	}
}
