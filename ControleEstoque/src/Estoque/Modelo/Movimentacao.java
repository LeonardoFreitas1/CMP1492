package Estoque.Modelo;

import java.util.ArrayList;

public class Movimentacao extends ErroMensagem {
	private int idMovimentacao;
	private ArrayList<Produto> listaProduto = new ArrayList<Produto>();
	private Secao secao;
	
	public int getIdMovimentacao() {
		return idMovimentacao;
	}
	public void setIdMovimentacao(int idMovimentacao) {
		this.idMovimentacao = idMovimentacao;
	}
	
	public ArrayList<Produto> getListaProduto() {
		return listaProduto;
	}
	public void setListaProduto(ArrayList<Produto> listaProduto) {
		this.listaProduto = listaProduto;
	}
	public Secao getSecao() {
		return secao;
	}
	public void setSecao(Secao secao) {
		this.secao = secao;
	}
}
