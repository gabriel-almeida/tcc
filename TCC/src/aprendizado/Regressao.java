package aprendizado;
import java.util.List;

import org.jblas.DoubleMatrix;
import org.jblas.Solve;


public class Regressao {
	private DoubleMatrix pesos;
	//TODO usar esta classe como wrapper da biblioteca JBLAS
	private DoubleMatrix adicionaColunaBias(DoubleMatrix matriz){
		DoubleMatrix bias = DoubleMatrix.ones(matriz.rows);
		DoubleMatrix matrizComBias = DoubleMatrix.concatHorizontally(matriz, bias);
		return matrizComBias;
	}
	
	public void treina(DoubleMatrix matrizTreino, DoubleMatrix target){
		if (matrizTreino.rows != target.rows){
			throw new RuntimeException("ERRO: matriz de target deve ter mesma quantidade de linhas que a matriz de treino");
		}
		DoubleMatrix treinoBias = adicionaColunaBias(matrizTreino);
		//DoubleMatrix pseudoInversa = Solve.pinv(treinoBias);
		//this.pesos = pseudoInversa.mmul(target);
		this.pesos = Solve.solveLeastSquares(treinoBias, target);
	}

	public DoubleMatrix classifica(DoubleMatrix matrizTeste){
		if (matrizTeste.columns + 1 != pesos.rows){
			throw new RuntimeException("ERRO: matriz de teste deveria ter " + (pesos.rows - 1)  + " colunas.");
		}
		DoubleMatrix testeBias = adicionaColunaBias(matrizTeste);
		DoubleMatrix resposta = testeBias.mmul(pesos);
		return resposta;
	}

	public List<Double> getPesos() {
		return pesos.elementsAsList();
	}

	public void setPesos(List<Double> pesos) {
		this.pesos = new DoubleMatrix(pesos);
	}
	
}
