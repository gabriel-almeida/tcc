import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

import processamento.ExtratorFeatures;
import processamento.PreProcessamento;
import modelo.Elemento;
import entrada.EntradaCSV;

public class Main {

	public static void main(String[] args) {
		try {
			Map<String, Elemento> base1 = EntradaCSV.leCsv("siapa.csv");
			Map<String, Elemento> base2 = EntradaCSV.leCsv("infoconv.csv");
			PreProcessamento pp = new PreProcessamento();
			ExtratorFeatures ef = new ExtratorFeatures(pp);
			//TODO tentar usar o forEach novo do java 8
			//TODO possivelmente passar isso para alguma outra base
			for (String chave: base1.keySet()){
				Elemento e1 = base1.get(chave);
				Elemento e2 = base2.get(chave);
				ef.extrai(e1, e2);
			}
			ef.getConjuntoDados();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
