package aprendizado;

import modelo.Elemento;

public interface CriterioVotacao {
	public double criterio(Elemento e);
	public boolean condicaoExistencia(Elemento e);
}
