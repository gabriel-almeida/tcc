package utilidades;

import java.util.ArrayList;
import java.util.List;

import modelo.ConjuntoDados;

import org.jblas.DoubleMatrix;

public abstract class Matriz {
	public static DoubleMatrix geraMatriz(List<Integer> indices, ConjuntoDados conjDados){
		DoubleMatrix matriz = new DoubleMatrix();
		for (int i : indices){
			ArrayList<Double> amostra = conjDados.getAmostra(i);
			if (amostra.stream().anyMatch(indice -> indice.isNaN()))
				System.out.println(i + " -> "+amostra);
			DoubleMatrix linha = new DoubleMatrix(amostra).transpose();
			if (matriz.columns == 0){
				matriz = linha;
			}
			else{
				matriz = DoubleMatrix.concatVertically(matriz, linha);
			}
		}
		return matriz;
	}
	
	public static DoubleMatrix geraVetorResposta(List<Integer> indices, ConjuntoDados conjDados){
		List<Double> respostas = conjDados.geraListaResposta(indices);
		return new DoubleMatrix(respostas);
	}
	public static DoubleMatrix adicionaColunaBias(DoubleMatrix matriz){
		DoubleMatrix bias = DoubleMatrix.ones(matriz.rows);
		DoubleMatrix matrizComBias = DoubleMatrix.concatHorizontally(matriz, bias);
		return matrizComBias;
	}

}
