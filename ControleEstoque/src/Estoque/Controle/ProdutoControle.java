package Estoque.Controle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import Estoque.Dao.GrupoDao;
import Estoque.Dao.ProdutoDao;
import Estoque.Modelo.Grupo;
import Estoque.Modelo.Produto;
import Estoque.Util.Utilidade;
public class ProdutoControle extends SelectorComposer<Window>{


	@Wire
	Tab tabManterProduto;
	@Wire
	Intbox intIdProduto;
	@Wire
	Textbox txtNomeProduto;
	@Wire
	Combobox combGrupoProduto;
	@Wire
	Button btnIncluirProduto;
	@Wire
	Button btnAlterarProduto;
	@Wire
	Button btnExcluirProduto;
	@Wire
	Button btnLimparProduto;
	@Wire
	Textbox txtPesqProduto;
	@Wire
	Listbox lsbPesqProduto;
	@Wire
	Button btnLimparPesqProduto;
	@Wire
	Button btnAtualizarPesqProduto;
		
	public String OPCAO = "";

	@Listen("onCreate=#Produto")
	public void permissao()
	{
		//de acordo com a permissao bloqueia ou desbloqueia os botões de incluir, alterar e excluir
		int admin = Integer.parseInt(Sessions.getCurrent().getAttribute("administrador").toString());
		if(admin == 1)
		{
			btnIncluirProduto.setDisabled(false);
			btnAlterarProduto.setDisabled(false);
			btnExcluirProduto.setDisabled(false);
		}
		else
		{
			btnIncluirProduto.setDisabled(true);
			btnAlterarProduto.setDisabled(true);
			btnExcluirProduto.setDisabled(true);
		}
	}
	
	//limpa a lista de pesquisa na aba de pesquisa
	public void limpaLsbPesquisa()
	{
		this.lsbPesqProduto.getItems().clear();
	}
	
	//preenche a lista de pesquisa de acordo com o que foi digitado no textbox de pesquisa
	public void preencheLsbPesquisa()throws SQLException, InterruptedException,IOException
	{
		this.limpaLsbPesquisa();
		ProdutoDao ProdutoDao = new ProdutoDao();
		ArrayList<Produto> listaProduto = new ArrayList<Produto>();
		
		listaProduto = ProdutoDao.getListaProdutoNomeGrupo(this.txtPesqProduto.getText().toString());
		
		while(!listaProduto.isEmpty())
		{
			Listitem li = new Listitem();
			Listcell lc01 = new Listcell();
			Listcell lc02 = new Listcell();
			Listcell lc03 = new Listcell();
			
			lc01.setLabel(Integer.toString(listaProduto.get(0).getIdProduto()));
			lc02.setLabel(listaProduto.get(0).getNomeProduto());
			lc03.setLabel(listaProduto.remove(0).getGrupo().getNomeGrupo());
			
			li.appendChild(lc01);
			li.appendChild(lc02);
			li.appendChild(lc03);
			this.lsbPesqProduto.appendChild(li);
		}
	}
	
	//ao clicar no botão incluir
	//incli o produto no banco de dados
	@Listen("onClick = #btnIncluirProduto")
	public void onClickbtnIncluir()throws Exception
	{
		OPCAO = "I";
		ProdutoDao produtoDao = new ProdutoDao();
		Produto produto = new Produto();
		
		if(validaDados())
		{
			produto = this.atualizaDados(produto);
			produto = produtoDao.incluir(produto);
			Utilidade.mensagem(produto.getMensagemErro());
		}
		if(produto.getMensagemErro() == "Produto incluido com sucesso!")
		{
			this.limpaDados();
		}
	}
	
	//se os dados forem validos altera o produto no banco de dados
	@Listen("onClick = #btnAlterarProduto")
	public void onClickbtnAlterar()throws Exception
	{
		OPCAO = "A";
		Produto produto = new Produto();
		ProdutoDao produtoDAO = new ProdutoDao();

		if(this.intIdProduto.getValue() == null || this.intIdProduto.getText().isEmpty() || this.intIdProduto.getText().equals(""))
		{
			Utilidade.mensagem("Selecione um produto para alterar.");
		}
		else
		{
			produto = this.atualizaDados(produto);
			produto = produtoDAO.alterar(produto);
			Utilidade.mensagem(produto.getMensagemErro());
		}
		this.limpaDados();
	}
	
	//excluio produto selecionado do banco de dados
	@Listen("onClick = #btnExcluirProduto")
	public void onClickbtnExcluir()throws Exception
	{
		OPCAO = "E";
		Produto produto = new Produto();
		ProdutoDao produtoDAO = new ProdutoDao();
		
		if(this.intIdProduto.getValue() == null || this.intIdProduto.getText().isEmpty() || this.intIdProduto.getText().equals(""))
		{
			Utilidade.mensagem("Selecione um produto para excluir.");
		}
		else
		{
			produto = this.atualizaDados(produto);
			produto = produtoDAO.excluir(produto);
			Utilidade.mensagem(produto.getMensagemErro());
		}
		
		this.limpaDados();
	}
	
	//valida os dados que estão seleciondos/digitados na tela
	public boolean validaDados()throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		int conta = 0;
		String mensagem = "";
		
		if(this.txtNomeProduto.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "Informe o nome do produto \n";
		}
		
		if(this.combGrupoProduto.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "selecione um grupo \n";
		}
		if(conta > 0)
		{
			Utilidade.mensagem(mensagem);
			return false;
		}
		return true;
	}
	
	//atualiza o produto com os dados que estão selecionados/digitados na tela
	public Produto atualizaDados(Produto produto)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		if(!OPCAO.equals("I")) produto.setIdProduto(this.intIdProduto.getValue());
		
		produto.setNomeProduto(this.txtNomeProduto.getValue());
		
		GrupoDao grupoDao = new GrupoDao();
		String nomeGrupo = this.combGrupoProduto.getText().toString();
		produto.setGrupo(grupoDao.getGrupoNome(nomeGrupo));
		this.txtPesqProduto.setText("");
		return produto;
	}
	
	//limpa a lista de pesquisa
	@Listen("onClick = #btnLimparPesqProduto")
	public void onClickbtnLimparLista()
	{
		this.limpaLsbPesquisa();
	}
	
	//atualiza a lista de pesquisa
	@Listen("onClick = #btnAtualizarPesqProduto")
	public void onClickbtnAtualizarLista()throws SQLException, InterruptedException, IOException
	{
		this.preencheLsbPesquisa();
	}
	
	//obtem o produto que o usuário selecionou
	//e preenche a aba de manutenção de produto com os dados do produto selecionado
	@Listen("onSelect = #lsbPesqProduto")
	public void obtemProdutoSelecionado() throws SQLException, InterruptedException
	{
		int indice = this.lsbPesqProduto.getSelectedIndex();
		int id = 0;
		ProdutoDao produtoDao = new ProdutoDao();
		Produto produto = new Produto();
		
		if(indice >=0)
		{
			Listcell lc01 = new Listcell();
			
			lc01 = (Listcell)this.lsbPesqProduto.getSelectedItem().getChildren().get(0);
			id = Integer.parseInt(lc01.getLabel().toString());
			produto = produtoDao.getProdutoId(id);
			
			if(produto != null)
			{
				this.intIdProduto.setText(Integer.toString(produto.getIdProduto()));
				this.txtNomeProduto.setText(produto.getNomeProduto());
				this.combGrupoProduto.setText(produto.getGrupo().getNomeGrupo());
				
				this.limpaLsbPesquisa();
				this.txtPesqProduto.setText("");
				this.tabManterProduto.setSelected(true);
			}
			else Utilidade.mensagem(produto.getMensagemErro());
		}
	}
	
	//preenche o combobox de grupo com todos os grupos cadastrados no banco de dados
	@Listen("onClick = #combGrupoProduto")
	public void preenchecombGrupo()throws SQLException, InterruptedException,IOException
	{
		combGrupoProduto.getItems().clear();
		GrupoDao grupoDao = new GrupoDao();
		ArrayList<Grupo> listaGrupo = new ArrayList<Grupo>();
		
		listaGrupo = grupoDao.getTodosGrupos();
		
		while(!listaGrupo.isEmpty())
		{
			this.combGrupoProduto.appendItem(listaGrupo.remove(0).getNomeGrupo());
		}
	}
	
	//limpa os dados digitados/selecionados na aba de manutenção de produtos
	@Listen("onClick = #btnLimparProduto")
	public void limpaDados()
	{
		OPCAO = "";
		this.intIdProduto.setText("");
		this.txtNomeProduto.setText("");
		this.combGrupoProduto.setText("");
		this.limpaLsbPesquisa();
	}
}
