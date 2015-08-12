package aprendizado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.ConjuntoDados;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import sun.nio.cs.ext.TIS_620;
import utilidades.AnalisePerformace;
import utilidades.Matriz;

public class RegressaoLogistica2 implements Regressao {
	private double coeficienteAprendizado=0.001;
	private int numPassos=10000;
	private double erroMinimo= 1E-6;
	private double normaMinima = 1E-5; //TODO rever esses parametros de treino
	private DoubleMatrix pesos;
	
	private DoubleMatrix validacao;
	private DoubleMatrix targetValidacao;
	
	private List<Double> curvaAprendizadoTreino = new ArrayList<Double>();
	private List<Double> curvaAprendizadoValidacao = new ArrayList<Double>();;
	
	public static final double maxEta = 0.1;
	public static final double minEta = 0.0001;

	public RegressaoLogistica2(){
	}
	public RegressaoLogistica2(double coeficienteAprendizado, int numPassos, double erroMinimo){
		this.coeficienteAprendizado=coeficienteAprendizado;
		this.numPassos = numPassos;
		this.erroMinimo = erroMinimo;
	}
	
	private double erroMedioQuadratico(DoubleMatrix dataset, DoubleMatrix target){
		DoubleMatrix h = funcaoHipotese(dataset);
		return MatrixFunctions.pow(h.sub(target), 2).mean();
	}
	private double funcaoVerossimilhanca(DoubleMatrix dataset, DoubleMatrix target){
		DoubleMatrix h = funcaoHipotese(dataset);
		DoubleMatrix primeiroTermo = MatrixFunctions.log(h).mul(target);
		DoubleMatrix segundoTermo = MatrixFunctions.log(h.neg().add(1)).mul(target.neg().add(1));
		return primeiroTermo.add(segundoTermo).sum();
	}
	
	private DoubleMatrix funcaoHipotese(DoubleMatrix dataset){
		DoubleMatrix numerador = DoubleMatrix.ones(dataset.rows);

		DoubleMatrix expoente = dataset.mmul(this.pesos).neg();
		
		DoubleMatrix divisor = MatrixFunctions.exp(expoente);
		divisor = divisor.add(1.0);
		
		DoubleMatrix resultado = numerador.div(divisor);
		return resultado;
	}
	/**
	 * Implementacao da Regressao Logisitica com Gradiente Decrescente. 
	 * Espera que o target seja um vetor coluna binario de -1 e 1.
	 */
	private void treino(DoubleMatrix dataset, DoubleMatrix target){
		if (dataset.rows != target.rows){
			throw new RuntimeException("Dataset e target devem ter a mesma quantidade de linhas");
		}
		
		dataset = Matriz.adicionaColunaBias(dataset);
		int quantColunas = dataset.columns;

		this.pesos = DoubleMatrix.ones(quantColunas);
		this.pesos = this.pesos.mul(0.1);

		
		double melhorVerossimilhanca = Double.NEGATIVE_INFINITY;
		DoubleMatrix melhoresPesos = null;
		
		//LOG Tempo
		AnalisePerformace tempo = new AnalisePerformace();
		
		//Iteracao principal
		int i;
		for (i = 0; i< numPassos; i++ ){
			DoubleMatrix erro = target.sub(this.funcaoHipotese(dataset)); // n x 1
			DoubleMatrix gradiente = dataset.transpose().mmul(erro); // (n x m)' (n x 1)  = m x 1
			
			//if (i > 1000) System.out.println("deu ruim.");
			
			
			DoubleMatrix a = this.pesos.dup().put(this.pesos.length - 1, 0.0);
			double norma = a.norm2();
			/*DoubleMatrix penalidade = this.pesos.div(norma).mul(-0.01);
			penalidade.put(penalidade.length - 1, 0.0);*/
			
			this.pesos = this.pesos.add(gradiente.mul(coeficienteAprendizado)).sub(norma*0.1);
			
			
			double verossimilhancaAtual = funcaoVerossimilhanca(dataset, target);

			//Se existir um  conjunto de validacao, coletar estatisticas da curva de aprendizado
			if (this.validacao != null){
				this.curvaAprendizadoTreino.add(erroMedioQuadratico(dataset, target));
				this.curvaAprendizadoValidacao.add(erroMedioQuadratico(validacao, targetValidacao));
				//this.curvaAprendizadoTreino.add(verossimilhancaAtual/dataset.rows);
				//this.curvaAprendizadoValidacao.add(funcaoVerossimilhanca(validacao, targetValidacao)/validacao.rows);
			}
			
			//Verifica se eh o melhor modelo ate o momento
			if (melhorVerossimilhanca < verossimilhancaAtual || i == 0){
				melhoresPesos = this.pesos;
				
				//CRITERIO DE PARADA: MELHORA DE GRADIENTE INSIGNIFICANTE
				double melhora = Math.abs(melhorVerossimilhanca - verossimilhancaAtual);
				if (melhora < this.normaMinima){
					break;
				}
				this.coeficienteAprendizado = Math.min(0.1, Math.max(0.0001, coeficienteAprendizado*1.1));
				melhorVerossimilhanca = verossimilhancaAtual;
			}
			else{
				this.coeficienteAprendizado = Math.min(0.1, Math.max(0.0001, coeficienteAprendizado*0.5));
				this.pesos = melhoresPesos;
			}
			
			//Criterio de Parada
			//if (normaGradiente < this.normaMinima || i == numPassos - 1 ){
			/*if (normaGradiente < this.normaMinima || i == numPassos - 1 ){
				System.out.println(i +  " Norma Gradiente: " + normaGradiente + " Melhor vessimilhanca: " + melhorVerossimilhanca );
				break;
			}*/
			
		}
		//this.pesos = melhoresPesos;
		tempo.capturaTempo(i);
		System.out.println("iteracao de parada: " + i + " Melhor vessimilhanca: " + melhorVerossimilhanca  + " melhores pesos :" + this.pesos);
		tempo.imprimeEstatistica("Regressao Logistica");
	}
	/**
	 * Retorna o resultado da regressao de cada amostra numa linha em um vetor coluna
	 * */
	private DoubleMatrix classifica(DoubleMatrix matrizTeste){
		if (matrizTeste.columns + 1 != pesos.rows){
			throw new RuntimeException("ERRO: matriz de teste deveria ter " + (pesos.rows - 1)  + " colunas.");
		}
		DoubleMatrix matrizTesteBias = Matriz.adicionaColunaBias(matrizTeste);
		
		return funcaoHipotese(matrizTesteBias);
	}

	@Override
	public void treina(ConjuntoDados conjDados, List<Integer> indices){
		DoubleMatrix treino = Matriz.geraMatriz(indices, conjDados);
		DoubleMatrix target = Matriz.geraVetorResposta(indices, conjDados);
		
		//Forca a binarizacao entre -1 e 1
		//DoubleMatrix targetsNegativos = target.eq(Constantes.VALOR_NEGATIVO);
		//target.put(targetsNegativos, -1.0);
			
		treino(treino, target);
	}
	
	//TODO: refatorar: ma ideia por a etapa de avaliacao aqui, idealmente seria um padrao listener
	public void setValidacao(ConjuntoDados conjDados, List<Integer> indicesValidacao){
		this.validacao = Matriz.geraMatriz(indicesValidacao, conjDados);
		this.validacao = Matriz.adicionaColunaBias(this.validacao);
		this.targetValidacao = Matriz.geraVetorResposta(indicesValidacao, conjDados);

	}
	
	@Override
	public List<Double> classifica(ConjuntoDados conjDados, List<Integer> indicesTeste){
		DoubleMatrix teste = Matriz.geraMatriz(indicesTeste, conjDados);
		DoubleMatrix resultado = classifica(teste);
		return resultado.elementsAsList();
	}
	@Override
	public Object clone(){
		RegressaoLogistica2 novaRegressao = new RegressaoLogistica2(this.coeficienteAprendizado, this.numPassos, this.erroMinimo);
		return novaRegressao;
	}
	@Override
	public double classifica(List<Double> features) {
		DoubleMatrix teste = new DoubleMatrix(features);
		DoubleMatrix resposta = this.classifica(teste.transpose());
		return resposta.get(0, 0);
	}
	
	public List<Double> getCurvaAprendizadoTreino() {
		return curvaAprendizadoTreino;
	}
	public List<Double> getCurvaAprendizadoValidacao() {
		return curvaAprendizadoValidacao;
	}
}


