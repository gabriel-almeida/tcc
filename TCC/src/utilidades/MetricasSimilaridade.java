package utilidades;

import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.MongeElkan;
import com.wcohen.ss.ScaledLevenstein;

public class MetricasSimilaridade {
	/**
	 * Wrapper de algumas metricas de similadidade.
	 * */
	
	public static MongeElkan me = new MongeElkan();
	public static JaroWinkler jw = new JaroWinkler();
	public static ScaledLevenstein l = new ScaledLevenstein();
	
	public static double MongeElkan(String a, String b){
		return me.score(a, b);
	}
	public static double JaroWinkler(String a, String b){
		return jw.score(a, b);
	}
	public static double Levenstein(String a, String b){
		return l.score(a, b);
	}
}
