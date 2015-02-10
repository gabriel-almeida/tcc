package aprendizado;

import modelo.Elemento;

public class CriterioMaioria implements CriterioVotacao {
	private String descritorVotoPositivo;
	private String descritorVotoNegativo;
	
	public CriterioMaioria(String descritorVotoPositivo,
			String descritorVotoNegativo) {
		super();
		this.descritorVotoPositivo = descritorVotoPositivo;
		this.descritorVotoNegativo = descritorVotoNegativo;
	}
	
	@Override
	public double criterio(Elemento e) {
		int positivo = Integer.parseInt(e.getElemento(descritorVotoPositivo));
		int negativo = Integer.parseInt(e.getElemento(descritorVotoNegativo));
		return positivo>negativo? 1.0:0.0;
	}

	@Override
	public boolean condicaoExistencia(Elemento e) {
		return true;
	}

}
