package entrada_saida;
import java.util.List;
import java.util.function.Function;


public class CategorizacaoPorFaixas implements Function<Double, String> {
	private List<String> categorias;
	private List<Double> faixas;
	
	public CategorizacaoPorFaixas(List<String> categorias, List<Double> faixasCorte) {
		if (categorias.size() != faixasCorte.size() + 1){
			throw new RuntimeException("Deve haver necessariamente uma categoria a mais do que o numero de cortes.");
		}
		this.categorias = categorias;
		this.faixas = faixasCorte;
	}

	@Override
	public String apply(Double valor) {
		for (int i = 0; i< faixas.size(); i++){
			if (faixas.get(i) >= valor){
				return categorias.get(i);
			}
		}
		return categorias.get(categorias.size() - 1);
	}

}
