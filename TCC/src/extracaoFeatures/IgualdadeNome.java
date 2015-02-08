package extracaoFeatures;

import modelo.Elemento;

public class IgualdadeNome implements CondicaoIgualdade {

	@Override
	public boolean condicaoIgualdade(Elemento e1, Elemento e2) {
		String s1 = e1.getElemento(0).trim();
		String s2 = e2.getElemento(0).trim();
		return s1.equals(s2);
	}

}
