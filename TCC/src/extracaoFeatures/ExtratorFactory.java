package extracaoFeatures;

public abstract class ExtratorFactory {
	public static Extrator getExtratorPorNome(String nomeExtrator){
		if (nomeExtrator.contains("MetricasSimilaridade")){
			 return new ExtratorMetricasSimilaridade();
		} else if( nomeExtrator.equals("Binario")){
			return new ExtratorBinario();
		}
		else if( nomeExtrator.equals("Levenstein")){
			return new ExtratorLevenstein();
		}
		throw new RuntimeException("Extrator desconhecido: " + nomeExtrator);
		
	}
}
