package avaliacao;

import java.util.List;


public class Avaliador {
	private List<Double> esperado;
	private List<Double> recebido;
	int verdadeiroPositivo = 0;
	int verdadeiroNegativo = 0;
	int falsoPositivo = 0;
	int falsoNegativo = 0;
	
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
	//TODO Criar formulas das demais metricas
	
}
