package modelo;

public class Elemento {
	private String elementos[];
	private String chave;
	
	public Elemento(String chave) {
		super();
		this.chave = chave;
	}
	public String getChave() {
		return chave;
	}
	public void setChave(String chave) {
		this.chave = chave;
	}
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
