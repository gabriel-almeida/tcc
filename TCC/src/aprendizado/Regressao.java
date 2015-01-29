package aprendizado;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;


public class Regressao {
	private DoubleMatrix pesos;
	
	public void treina(DoubleMatrix matrizTreino, DoubleMatrix target){
		DoubleMatrix bias = DoubleMatrix.ones(matrizTreino.rows);
		DoubleMatrix treinoBias = DoubleMatrix.concatVertically(matrizTreino, bias); 
		
		DoubleMatrix pseudoInversa = Solve.pinv(treinoBias);
		this.pesos = pseudoInversa.mul(target);
	}

	public DoubleMatrix classifica(DoubleMatrix matrizTeste){
		DoubleMatrix bias = DoubleMatrix.ones(matrizTeste.rows);
		DoubleMatrix testeBias = DoubleMatrix.concatVertically(matrizTeste, bias);
		return testeBias.mul(pesos);
	}
}
