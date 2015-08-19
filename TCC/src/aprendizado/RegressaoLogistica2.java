package aprendizado;

import java.util.ArrayList;
import java.util.List;

import modelo.ConjuntoDados;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import utilidades.AnalisePerformace;
import utilidades.Matriz;

public class RegressaoLogistica2 implements Regressao {
	//Numeros magicos
	private double coeficienteRegularizacao = 0.0;
	private double coeficienteAprendizadoInicial = 0.001;
	private double coeficienteAprendizadoAtual;
	private int numMaximoPassos = 1000;
	private double variacaoMinimaErro = 1E-4;
	public static final double maxCoeficienteAprendizado = 0.1;
	public static final double minCoeficienteAprendizado = 0.0001;
	private double coefBoldDriverMelhora = 1.1;
	private double coefBoldDriverPiora = 0.5;
	

	//Modelo
	private DoubleMatrix pesos;
	
	//Informacoes para gerar a curva de aprendizado
	private DoubleMatrix validacao;
	private DoubleMatrix targetValidacao;
	private List<Double> curvaAprendizadoTreino = new ArrayList<Double>();
	private List<Double> curvaAprendizadoValidacao = new ArrayList<Double>();;
	
	
	public RegressaoLogistica2(){
	}
	
	public RegressaoLogistica2(double coeficienteAprendizadoInicial, int numPassos, double variacaoMinimaErro){
		this.coeficienteAprendizadoInicial = coeficienteAprendizadoInicial;
		this.numMaximoPassos = numPassos;
		this.variacaoMinimaErro = variacaoMinimaErro;
	}
	
	public void setCoeficienteRegularizacao(double coeficiente){
		this.coeficienteRegularizacao = coeficiente; 
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
		//Teste de sanidade
		if (dataset.rows != target.rows){
			throw new RuntimeException("Dataset e target devem ter a mesma quantidade de linhas");
		}
		
		//Prepara bias/intercept
		dataset = Matriz.adicionaColunaBias(dataset);
		int quantColunas = dataset.columns;

		//Inicializa pesos: acho que nao importa muito o valor inicial
		this.pesos = DoubleMatrix.ones(quantColunas);

		//Registro do melhor modelo
		double melhorVerossimilhanca = Double.NEGATIVE_INFINITY;
		DoubleMatrix melhoresPesos = null;
		
		//LOG Tempo
		AnalisePerformace tempo = new AnalisePerformace();
		
		//Iteracao principal
		int i;
		this.coeficienteAprendizadoAtual = this.coeficienteAprendizadoInicial;
		for (i = 0; i< numMaximoPassos; i++ ){
			DoubleMatrix erro = target.sub(this.funcaoHipotese(dataset)); // n x 1
			DoubleMatrix gradiente = dataset.transpose().mmul(erro); // (n x m)' (n x 1)  = m x 1
			
			//Regularizacao		
			double norma = this.pesos.norm2();
			DoubleMatrix penalidadeR2 = this.pesos.div(norma).mul(coeficienteRegularizacao);
			
			//Atualizacao
			this.pesos = this.pesos.add(gradiente.mul(coeficienteAprendizadoAtual)).sub(penalidadeR2);
			
			
			double verossimilhancaAtual = funcaoVerossimilhanca(dataset, target);

			//Se existir um  conjunto de validacao, coletar estatisticas da curva de aprendizado
			if (this.validacao != null){
				this.curvaAprendizadoTreino.add(erroMedioQuadratico(dataset, target));
				this.curvaAprendizadoValidacao.add(erroMedioQuadratico(validacao, targetValidacao));
			}
			
			//Verifica se eh o melhor modelo ate o momento
			if (melhorVerossimilhanca < verossimilhancaAtual || i == 0){
				melhoresPesos = this.pesos;
				
				//CRITERIO DE PARADA: MELHORA DE GRADIENTE INSIGNIFICANTE
				double melhora = Math.abs(melhorVerossimilhanca - verossimilhancaAtual);
				if (melhora < this.variacaoMinimaErro){
					break;
				}
				//Heuristica do Bold Driver: se melhora, aumento um pouco meu coeficiente de aprendizado
				this.coeficienteAprendizadoAtual = Math.min(maxCoeficienteAprendizado, Math.max(minCoeficienteAprendizado, coeficienteAprendizadoAtual*coefBoldDriverMelhora));
				melhorVerossimilhanca = verossimilhancaAtual;
			}
			//Senao for o melhor modelo, rollback
			else{
				//Heuristica do Bold Driver: se piora, diminuo muito meu coeficiente de aprendizado
				this.coeficienteAprendizadoAtual = Math.min(maxCoeficienteAprendizado, Math.max(minCoeficienteAprendizado, coeficienteAprendizadoAtual*coefBoldDriverPiora));
				this.pesos = melhoresPesos;
			}
			
			
		}
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
		RegressaoLogistica2 novaRegressao = new RegressaoLogistica2(this.coeficienteAprendizadoInicial, this.numMaximoPassos, this.variacaoMinimaErro);		novaRegressao.coeficienteRegularizacao = this.coeficienteRegularizacao;
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


