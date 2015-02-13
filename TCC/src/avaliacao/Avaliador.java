package avaliacao;

import java.util.List;

import utilidades.Constantes;


public class Avaliador {
	private List<Double> esperado;
	private List<Double> recebido;
	private int verdadeiroPositivo = 0;
	private int verdadeiroNegativo = 0;
	private int falsoPositivo = 0;
	private int falsoNegativo = 0;

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
			double obtido = recebido.get(i) > limiar? Constantes.valorPositivo: Constantes.valorNegativo; //TODO cuidado ao mudar o target

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
		return 1.0*(verdadeiroPositivo + verdadeiroNegativo)/total();
	}
	//Relacao a positivo
	public double precisaoPositiva(){
		return 1.0*verdadeiroPositivo/(verdadeiroPositivo + falsoPositivo);
	}
	public double recallPositiva(){
		return 1.0*verdadeiroPositivo/(verdadeiroPositivo + falsoNegativo);
	}
	public double f1MeasurePositiva(){
		return 2.0*precisaoPositiva()*recallPositiva()/(precisaoPositiva() + recallPositiva());
	}
	//relacao a negativo
	public double precisaoNegativo(){
		return 1.0*verdadeiroNegativo/(verdadeiroNegativo + falsoNegativo);
	}
	public double recallNegativo(){
		return 1.0*verdadeiroNegativo/(verdadeiroNegativo + falsoPositivo);
	}
	public double f1MeasureNegativo(){
		return 2.0*precisaoNegativo()*recallNegativo()/(precisaoNegativo() + recallNegativo());
	}
	public String toString(){
		String s = "Acuracia = %f\n"
				+ "Precisao  = %f (%f)\n"
				+ "Recall    = %f (%f)\n"
				+ "F1        = %f (%f)\n"
				+ "==============================\n"
				+ "Verdadeiros Positivos = %d\n"
				+ "Verdadeiros Negativos = %d\n"
				+ "Falsos      Positivos = %d\n"
				+ "Falsos      Negativos = %d\n";
		return String.format(s, this.acuracia(), 
				this.precisaoPositiva(), this.precisaoNegativo(),
				this.recallPositiva(), this.recallNegativo(),
				this.f1MeasurePositiva(), this.f1MeasureNegativo(),
				this.verdadeiroPositivo, this.verdadeiroNegativo,
				this.falsoPositivo, this.falsoNegativo);
	}
}
