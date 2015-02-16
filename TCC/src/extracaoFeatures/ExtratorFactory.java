package extracaoFeatures;

public abstract class ExtratorFactory {
	public static Extrator getExtratorPorNome(String nomeExtrator){
		if (nomeExtrator.contains("MetricasSimilaridade")){
			 return new ExtratorMetricasSimilaridade();
		} else if( nomeExtrator.equals("Binario")){
			return new ExtratorBinario();
		}
		throw new RuntimeException("Extrator desconhecido: " + nomeExtrator);
		
	}
}
