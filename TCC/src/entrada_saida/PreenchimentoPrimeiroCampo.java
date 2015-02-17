package entrada_saida;
import java.util.ArrayList;

import java.util.List;
import java.util.function.BiFunction;

import modelo.Elemento;


public class PreenchimentoPrimeiroCampo implements BiFunction<Elemento, Elemento, Elemento> {

	@Override
	public Elemento apply(Elemento arg0, Elemento arg1) {
		List<String> descritores = new ArrayList<String>();
		descritores.add(arg0.getDescritores().get(0));
		descritores.add(arg1.getDescritores().get(0));
		
		Elemento elementoSaida = new Elemento(arg0.getChave(), descritores);
		elementoSaida.addElemento(0, arg0.getElemento(0));
		elementoSaida.addElemento(1, arg1.getElemento(0));
		return elementoSaida;
	}

}
