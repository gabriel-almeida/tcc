package modelo;

public class Elemento {
	private String elementos[];
	
	public String getElemento(int i){
		return this.elementos[i];
	}
	public void setElemento(int i, String s){
		this.elementos[i]=s;
	}
	public int tamanho(){
		return this.elementos.length;
	}
	
}
