package aprendizado;

import java.util.List;

import modelo.ConjuntoDados;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import utilidades.AnalisePerformace;
import utilidades.Matriz;

public class RegressaoLogistica implements Regressao {
	private double eta=0.01;
	private int numPassos=100000;
	private double epsilon= 1E-6;
	private double normaMinima = 1E-3;

	private DoubleMatrix pesos;

	public static final double maxEta=0.1;
	public static final double minEta=0.000001;

	public RegressaoLogistica(){
	}
	public RegressaoLogistica(double eta, int numPassos, double epsilon){
		this.eta=eta;
		this.numPassos = numPassos;
		this.epsilon = epsilon;
	}

	public double erro(DoubleMatrix dataset, DoubleMatrix target){
		DoubleMatrix produtoInterno = dataset.mmul(this.pesos);
		DoubleMatrix expoente = produtoInterno.mul(target);
		DoubleMatrix erro = MatrixFunctions.exp(expoente.neg());
		erro = erro.add(1.0);
		erro = MatrixFunctions.log(erro);
		return erro.mean();
	}
	
	/**
	 * Implementacao da Regressao Logisitica com Gradiente Decrescente
	 */
	private void treino(DoubleMatrix dataset, DoubleMatrix target){
		if (dataset.rows != target.rows){
			throw new RuntimeException("Dataset e target devem ter a mesma quantidade de linhas");
		}
		
		dataset = Matriz.adicionaColunaBias(dataset);
		int quantColunas = dataset.columns;

		this.pesos = DoubleMatrix.zeros(quantColunas);
		double etaAtual = this.eta;
		double erroAnterior = Double.MAX_VALUE;

		double melhorErro = Double.MAX_VALUE;
		DoubleMatrix melhoresPesos = null;
		
		//LOG Tempo
		AnalisePerformace.zera();
		AnalisePerformace.capturaTempo(0);
		for (int i = 0; i< numPassos; i++ ){

			DoubleMatrix numerador = dataset.mulColumnVector(target); //amostras que sao zero teram todas as features zeradas

			DoubleMatrix expoente = dataset.mmul(this.pesos); //vetor coluna
			expoente = expoente.mul(target); //vetor coluna

			DoubleMatrix divisor = MatrixFunctions.exp(expoente);
			divisor = divisor.add(1.0);

			DoubleMatrix gradiente = numerador.divColumnVector(divisor);//matriz amostra x features
			gradiente = gradiente.columnMeans();
			this.pesos = this.pesos.add(gradiente.transpose().mul(this.eta));
			
			double erroAtual = erro(dataset, target);
			double normaGradiente = gradiente.norm2();
			if (erroAtual <= this.epsilon || normaGradiente < this.normaMinima || i == numPassos - 1){
				//System.out.println(i + " Variacao Erro: " + Math.abs(erroAtual - erroAnterior) + " Eta: " + etaAtual + " Norma Gradiente: " + normaGradiente + " Melhor erro: " + melhorErro );
				break;
			}

			//Atualiza eta
			if (erroAtual > erroAnterior ){
				etaAtual = Math.max(minEta, etaAtual/10);
			}
			else{
				etaAtual = Math.min(maxEta, etaAtual*10);
			}

			if (melhorErro > erroAtual){
				melhorErro = erroAtual;
				melhoresPesos = this.pesos;
			}

			erroAnterior = erroAtual;
			
			//LOG Tempo
			if (i % 10000 == 0){
				AnalisePerformace.capturaTempo(i);
			}
		}
		this.pesos = melhoresPesos;
		AnalisePerformace.imprimeEstatistica("Regressao Logistica");
	}
	
	public DoubleMatrix classifica(DoubleMatrix matrizTeste){
		if (matrizTeste.columns + 1 != pesos.rows){
			throw new RuntimeException("ERRO: matriz de teste deveria ter " + (pesos.rows - 1)  + " colunas.");
		}
		
		DoubleMatrix matrizTesteBias = Matriz.adicionaColunaBias(matrizTeste);
		
		DoubleMatrix produtoInterno = matrizTesteBias.mmul(this.pesos);
		DoubleMatrix exponencial = MatrixFunctions.exp(produtoInterno);
		DoubleMatrix denominador = exponencial.add(1.0);
		DoubleMatrix sigmoid= exponencial.div(denominador);
		this.pesos.print();
		sigmoid.print();
		return sigmoid;
	}

	@Override
	public void treina(ConjuntoDados conjDados, List<Integer> indices){
		DoubleMatrix treino = Matriz.geraMatriz(indices, conjDados);
		DoubleMatrix target = Matriz.geraVetorResposta(indices, conjDados);
		
		for (int i = 0; i< target.rows; i++){
			if (target.get(i,0) == 0.0){//TODO CUIDADO
				target.put(i,0, -1.0);
			}
		}
		treino(treino, target);
	}

	@Override
	public List<Double> classifica(ConjuntoDados conjDados, List<Integer> indicesTeste){
		DoubleMatrix teste = Matriz.geraMatriz(indicesTeste, conjDados);
		DoubleMatrix resultado = classifica(teste);
		return resultado.elementsAsList();
	}
	
}


