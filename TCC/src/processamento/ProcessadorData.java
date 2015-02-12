package processamento;

import java.util.HashSet;
import java.util.Set;

public class ProcessadorData implements Processador {
	private Set<String> blacklist;
	public ProcessadorData() {
		blacklist =  new HashSet<String>();
		blacklist.add("00021130");
		blacklist.add("19700101");
	}
	@Override
	public String processa(String s) {
		//TODO tentar melhorar e talvez inferir data
		String resultado = s.replaceAll("[^0-9]", "");
		try{
			resultado = resultado.substring(0, 8);
			if (blacklist.contains(resultado))
				return "";
		}catch (Exception e){
			//System.out.println("hue");
		}
		return resultado;
	}
}
