package utilidades;

import com.wcohen.ss.JaroWinkler;

import com.wcohen.ss.MongeElkan;
import com.wcohen.ss.ScaledLevenstein;
import com.wcohen.ss.Levenstein;

public class MetricasSimilaridade {
	/**
	 * Wrapper de algumas metricas de similadidade.
	 * */
	
	private static MongeElkan me = new MongeElkan();
	private static JaroWinkler jw = new JaroWinkler();
	private static ScaledLevenstein ln = new ScaledLevenstein();
	private static Levenstein l = new Levenstein();	
	
	public static double MongeElkan(String a, String b){
		return me.score(a, b);
	}
	public static double JaroWinkler(String a, String b){
		return jw.score(a, b);
	}
	public static double LevensteinNormalizado(String a, String b){
		return ln.score(a, b);
	}
	public static double Levenstein(String a, String b){
		return l.score(a, b);
	}
	public static void main(String args[]){
		System.out.println(JaroWinkler("gabrasdfel", "gabrielaaa"));
		System.out.println(JaroWinkler("gabrielaaa", "gabrasdfel" ));
	}
}
