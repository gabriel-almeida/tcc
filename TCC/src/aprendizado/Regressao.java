package aprendizado;

import java.util.List;

import modelo.ConjuntoDados;

public interface Regressao {
	public void treina(ConjuntoDados conjDados, List<Integer> indices);
	public List<Double> classifica(ConjuntoDados conjDados, List<Integer> indicesTeste);
	public double classifica(List<Double> features);
	public Object clone();
	
	public default void setValidacao(ConjuntoDados conjDados, List<Integer> indicesTeste){}
	public default List<Double> getCurvaAprendizadoTreino() { return null; }
	public default List<Double> getCurvaAprendizadoValidacao() { return null; };
}
