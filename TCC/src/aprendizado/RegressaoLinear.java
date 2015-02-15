package aprendizado;
import java.util.List;

import modelo.ConjuntoDados;

import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import utilidades.Matriz;


public class RegressaoLinear implements Regressao {
	private DoubleMatrix pesos;
	
	public void treina(DoubleMatrix matrizTreino, DoubleMatrix target){
		if (matrizTreino.rows != target.rows){
			throw new RuntimeException("ERRO: matriz de target deve ter mesma quantidade de linhas que a matriz de treino");
		}
		DoubleMatrix treinoBias = Matriz.adicionaColunaBias(matrizTreino);
		this.pesos = Solve.solveLeastSquares(treinoBias, target);
	}

	public DoubleMatrix classifica(DoubleMatrix matrizTeste){
		if (matrizTeste.columns + 1 != pesos.rows){
			throw new RuntimeException("ERRO: matriz de teste deveria ter " + (pesos.rows - 1)  + " colunas.");
		}
		DoubleMatrix testeBias = Matriz.adicionaColunaBias(matrizTeste);
		DoubleMatrix resposta = testeBias.mmul(pesos);
		
		return resposta;
	}

	public List<Double> getPesos() {
		return pesos.elementsAsList();
	}

	public void setPesos(List<Double> pesos) {
		this.pesos = new DoubleMatrix(pesos);
	}
	
	@Override
	public void treina(ConjuntoDados conjDados, List<Integer> indices){
		DoubleMatrix treino = Matriz.geraMatriz(indices, conjDados);
		DoubleMatrix target = Matriz.geraVetorResposta(indices, conjDados);
		treina(treino, target);
	}
	
	@Override
	public List<Double> classifica(ConjuntoDados conjDados, List<Integer> indicesTeste){
		DoubleMatrix teste = Matriz.geraMatriz(indicesTeste, conjDados);
		DoubleMatrix resultado = classifica(teste);
		return resultado.elementsAsList();
	}
	public Object clone(){
		RegressaoLinear novaRegressao = new RegressaoLinear();
		return novaRegressao;
	}
}
