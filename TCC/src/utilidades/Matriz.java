package utilidades;

import java.util.List;

import modelo.ConjuntoDados;

import org.jblas.DoubleMatrix;

public abstract class Matriz {
	public static DoubleMatrix geraMatriz(List<Integer> indices, ConjuntoDados conjDados){
		DoubleMatrix matriz = new DoubleMatrix();
		for (int i : indices){
			DoubleMatrix linha = new DoubleMatrix(conjDados.getAmostra(i)).transpose();
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

}