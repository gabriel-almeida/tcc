package aprendizado;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;


public class Regressao {
	private DoubleMatrix pesos;
	
	private DoubleMatrix adicionaColunaBias(DoubleMatrix matriz){
		DoubleMatrix bias = DoubleMatrix.ones(matriz.rows);
		DoubleMatrix matrizComBias = DoubleMatrix.concatVertically(matriz, bias);
		return matrizComBias;
	}
	
	public void treina(DoubleMatrix matrizTreino, DoubleMatrix target){
		if (matrizTreino.rows != target.rows){
			throw new RuntimeException("ERRO: matriz de target deve ter mesma quantidade de linhas que a matriz de treino");
		}
		DoubleMatrix treinoBias = adicionaColunaBias(matrizTreino);
		this.pesos = Solve.solveLeastSquares(treinoBias, target);
	}

	public DoubleMatrix classifica(DoubleMatrix matrizTeste){
		if (matrizTeste.columns + 1 != pesos.rows){
			throw new RuntimeException("ERRO: matriz de teste deveria ter " + (pesos.rows - 1)  + " colunas.");
		}
		DoubleMatrix testeBias = adicionaColunaBias(matrizTeste);
		return testeBias.mul(pesos);
	}
}
