package avaliacao;

import java.util.List;


public class Avaliador {
	private List<Double> esperado;
	private List<Double> recebido;
	private double verdadeiroPositivo = 0;
	private double verdadeiroNegativo = 0;
	private double falsoPositivo = 0;
	private double falsoNegativo = 0;

	public double getVerdadeiroPositivo() {
		return verdadeiroPositivo;
	}

	public double getVerdadeiroNegativo() {
		return verdadeiroNegativo;
	}

	public double getFalsoPositivo() {
		return falsoPositivo;
	}

	public double getFalsoNegativo() {
		return falsoNegativo;
	}

	public List<Double> getEsperado() {
		return esperado;
	}

	public void setEsperado(List<Double> esperado) {
		this.esperado = esperado;
	}

	public List<Double> getRecebido() {
		return recebido;
	}

	public Avaliador(List<Double> esperado, List<Double> recebido) {
		super();
		this.esperado = esperado;
		this.recebido = recebido;
	}

	public void setRecebido(List<Double> recebido) {
		this.recebido = recebido;
	}

	public void avalia(double limiar){
		for (int i = 0; i < esperado.size(); i++){
			double target = esperado.get(i);
			double obtido = recebido.get(i) > limiar? 1.0: 0.0;

			if (target == obtido){
				if (obtido==1.0){
					verdadeiroPositivo++;
				}
				else{
					verdadeiroNegativo++;
				}
			}
			else{
				if (obtido==1.0){
					falsoPositivo++;
				}
				else{
					falsoNegativo++;
				}
			}
		}
	}
	public double total(){
		return verdadeiroPositivo + verdadeiroNegativo + falsoNegativo + falsoPositivo;
	}
	public double acuracia(){
		return (verdadeiroPositivo + verdadeiroNegativo)/total();
	}
	//Relacao a positivo
	public double precisaoPositiva(){
		return verdadeiroPositivo/(verdadeiroPositivo + falsoPositivo);
	}
	public double recallPositiva(){
		return verdadeiroPositivo/(verdadeiroPositivo + falsoNegativo);
	}
	public double f1MeasurePositiva(){
		return 2*precisaoPositiva()*recallPositiva()/(precisaoPositiva() + recallPositiva());
	}
	//relacao a negativo
	public double precisaoNegativo(){
		return verdadeiroNegativo/(verdadeiroNegativo + falsoNegativo);
	}
	public double recallNegativo(){
		return verdadeiroNegativo/(verdadeiroNegativo + falsoPositivo);
	}
	public double f1MeasureNegativo(){
		return 2*precisaoNegativo()*recallNegativo()/(precisaoNegativo() + recallNegativo());
	}

}
