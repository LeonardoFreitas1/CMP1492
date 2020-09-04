package Estoque.Controle;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import Estoque.Dao.MovimentacaoDao;
import Estoque.Dao.ProdutoDao;
import Estoque.Dao.SecaoDao;
import Estoque.Modelo.Movimentacao;
import Estoque.Modelo.Produto;
import Estoque.Modelo.Secao;
import Estoque.Util.Utilidade;
	
public class MovimentacaoControle extends SelectorComposer<Window>{
	
	@Wire 
	Tab tabManterMovimentacao;
	@Wire
	Window Movimentacao;
	@Wire
	Intbox intIdMovimentacao;
	@Wire
	Combobox combSecaoMovimentacao,filtarPesquisaMovimentacao;
	@Wire
	Chosenbox chosenProdutosMovimentacao;
	@Wire
	Button btnIncluirMovimentacao;
	@Wire
	Button btnAlterarMovimentacao;
	@Wire
	Button btnExcluirMovimentacao;
	@Wire
	Button btnLimparMovimentacao;
	@Wire
	Textbox txtPesqMovimentacao;
	@Wire
	Listbox lsbPesqMovimentacao;
	@Wire
	Button btnLimparPesqMovimentacao;
	@Wire
	Button btnAtualizarPesqMovimentacao;
		
	public String OPCAO = "";

	@Listen("onCreate = #Movimentacao")
	public void preencheComboboxs() throws SQLException, InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException
	{
		//preenche o combobox de seção e o chosenbox de produtos
		this.PreencheChosenbox();
		this.preenchecombSecaoMovimentacao();
		
		//de acordo com a permissao bloqueia ou desbloqueia os botões de incluir, alterar e excluir
		int admin = Integer.parseInt(Sessions.getCurrent().getAttribute("administrador").toString());
		if(admin == 1)
		{
			btnIncluirMovimentacao.setDisabled(false);
			btnAlterarMovimentacao.setDisabled(false);
			btnExcluirMovimentacao.setDisabled(false);
		}
		else
		{
			btnIncluirMovimentacao.setDisabled(true);
			btnAlterarMovimentacao.setDisabled(true);
			btnExcluirMovimentacao.setDisabled(true);
		}
	}
	
	public void limpaLsbPesquisa()
	{
		//limpa a lista de pesquisa de movimentações que fica na aba de pesquisa
		this.lsbPesqMovimentacao.getItems().clear();
	}
	
	public void preencheLsbPesquisa(ArrayList<Movimentacao> listaMovimentacao)throws SQLException, InterruptedException,IOException
	{
		//preenche a lista de pesquisa de movimentações com as informaçoes da lista passada como parâmetro
		this.limpaLsbPesquisa();
		String nome = this.txtPesqMovimentacao.getText().toString();
		
		while(!listaMovimentacao.isEmpty())
		{
			Listitem li = new Listitem();
			Listcell lc01 = new Listcell();
			Listcell lc02 = new Listcell();
			Listcell lc03 = new Listcell();
			
			lc01.setLabel(Integer.toString(listaMovimentacao.get(0).getIdMovimentacao()));
			lc02.setLabel(listaMovimentacao.get(0).getSecao().getNomeSecao());
			ArrayList<Produto> listaProduto = listaMovimentacao.remove(0).getListaProduto();
			while(!listaProduto.isEmpty())
			{
				if(listaProduto.remove(0).getNomeProduto() == nome)
					lc03.setLabel(nome + ". Selecione a movimentação para ver todos os produtos.");
				
			}
			lc03.setLabel("Selecione a movimentação para visualizar os produtos.");
			
			li.appendChild(lc01);
			li.appendChild(lc02);
			li.appendChild(lc03);
			this.lsbPesqMovimentacao.appendChild(li);
		}
	}
	
	@Listen("onClick = #btnIncluirMovimentacao")
	public void onClickbtnIncluir()throws Exception
	{
		//ao clicar no botão incluir que fica na aba de manutenção
		//verifica se os dados selecionados na tela são validos
		//atualiza os dados da movimentação de acordo com o que esta digitado/selecionado na tela
		//inclui a movimentação no banco de dados
		//mostra para o usuario uma mensagem avisando se a movimentação foi incluida ou não
		//limpa os dados da tela de manutenção e pesquisa
		OPCAO = "I";
		if(this.validaDados())
		{
			MovimentacaoDao movimentacaoDao = new MovimentacaoDao();
			Movimentacao movimentacao = new Movimentacao();
			
			movimentacao = this.atualizaDados(movimentacao);
			movimentacao = movimentacaoDao.incluir(movimentacao);
			Utilidade.mensagem(movimentacao.getMensagemErro());
			if(movimentacao.getMensagemErro() == "Movimentação e seus produtos incluidos com sucesso!")
			{
				this.limpaDados();
			}
		}
	}
	
	@Listen("onClick = #btnAlterarMovimentacao")
	public void onClickbtnAlterar()throws Exception
	{
		//ao clicar no botão alterar que fica na aba de manutenção
		//verifica se os dados selecionados na tela são validos
		//atualiza os dados da movimentação de acordo com o que esta digitado/selecionado na tela
		//altera a movimentação no banco de dados
		//mostra para o usuario uma mensagem avisando se a movimentação foi alterada ou não
		//limpa os dados da tela de manutenção e pesquisa
		OPCAO = "A";
		Movimentacao movimentacao = new Movimentacao();
		MovimentacaoDao movimentacaoDAO = new MovimentacaoDao();
		if(this.validaDados())
		{
	
			if(this.intIdMovimentacao.getValue() == null || this.intIdMovimentacao.getText().isEmpty() || this.intIdMovimentacao.getText().equals(""))
			{
				Utilidade.mensagem("Selecione um movimentacao para alterar.");
			}
			else
			{
				movimentacao = this.atualizaDados(movimentacao);
				movimentacao = movimentacaoDAO.alterarMovimentacao(movimentacao);
				Utilidade.mensagem(movimentacao.getMensagemErro());
			}
			this.limpaDados();
		}
	}
	
	@Listen("onClick = #btnExcluirMovimentacao")
	public void onClickbtnExcluir()throws Exception
	{
		//ao clicar no botão excluir que fica na aba de manutenção
		//atualiza os dados da movimentação de acordo com o que esta digitado/selecionado na tela
		//exclui a movimentação no banco de dados
		//mostra para o usuario uma mensagem avisando se a movimentação foi excluida ou não
		//limpa os dados da tela de manutenção e pesquisa		
		OPCAO = "E";
		Movimentacao movimentacao = new Movimentacao();
		MovimentacaoDao movimentacaoDAO = new MovimentacaoDao();
		
		if(this.intIdMovimentacao.getValue() == null || this.intIdMovimentacao.getText().isEmpty() || this.intIdMovimentacao.getText().equals(""))
		{
			Utilidade.mensagem("Selecione um movimentacao para excluir.");
		}
		else
		{
			movimentacao = this.atualizaDados(movimentacao);
			movimentacao = movimentacaoDAO.excluirMovimentacao(movimentacao);
			Utilidade.mensagem(movimentacao.getMensagemErro());
		}
		
		this.limpaDados();
	}
	
	public boolean validaDados()throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		//verifica se os dados foram selecionados
		//se não foram mostra uma mensagem para o usuario selecionar
		int conta = 0;
		String mensagem = "";
		
		if(this.combSecaoMovimentacao.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "selecione uma seção \n";
		}
		if(this.chosenProdutosMovimentacao.getSelectedObjects().isEmpty())
		{
			conta++;
			mensagem += "Selecione um produto \n";
		}
		
		if(conta > 0)
		{
			Utilidade.mensagem(mensagem);
			return false;
		}
		return true;
	}
	
	public Movimentacao atualizaDados(Movimentacao movimentacao)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		//recebe a movimentação com os dados antigos e a ataualiza colocando a seção e a lista de produtos que estao selecionados na tela
		//se não for inclusão adiciona na movimentação o id dela que esta na tela
		//retorna a movimentação com os dados atualizados
	
		if(!OPCAO.equals("I")) movimentacao.setIdMovimentacao(this.intIdMovimentacao.getValue());

		ListModelList<String> listaNomeProduto = new ListModelList<String>();
		//listaNomeProduto.addAll(this.chosenProdutosMovimentacao.getSelectedObjects());
		ArrayList<Produto> listaProduto = new ArrayList<Produto>();

		while(!listaNomeProduto.isEmpty())
		{
			ProdutoDao produtoDao = new ProdutoDao();			
			listaProduto.add(produtoDao.getProdutoNome(listaNomeProduto.remove(0)));
		}
		movimentacao.setListaProduto(listaProduto);
		
		String nomeSecao = this.combSecaoMovimentacao.getSelectedItem().getLabel().toString();
		SecaoDao secaoDao = new SecaoDao();
		movimentacao.setSecao(secaoDao.getSecaoNome(nomeSecao));
		this.txtPesqMovimentacao.setText("");
		
		return movimentacao;
	}
	
	@Listen("onClick = #btnLimparPesqMovimentacao")
	public void onClickbtnLimparLista()
	{
		//ao clicar no boão limpar que fica na aba pesquisa
		//limpa a lista de pesquisa de movimentações que também fica na aba pesquisa.
		this.limpaLsbPesquisa();
	}
	

	//ao clicar no botão atualizar que fica na aba pesquisa
	//preenche a lista de pesquisa de movimentações que fica na aba pesquisa
	@Listen("onClick = #btnAtualizarPesqMovimentacao")
	public void onClickbtnAtualizarLista()throws SQLException, InterruptedException, IOException
	{
		String nome = this.txtPesqMovimentacao.getText().toString();

		MovimentacaoDao MovimentacaoDao = new MovimentacaoDao();
		ArrayList<Movimentacao> listaMovimentacao = new ArrayList<Movimentacao>();
		
		//se estiver selecionado o filtro de secao
		//a lista de movimentacao recebe apenas as movimentações que contem a seção com pelo menos uma parte do nome 
		//igual ao que esta no textbox de pesquisa
		if(this.filtarPesquisaMovimentacao.getText().toString().length() ==5)
		{
			listaMovimentacao = MovimentacaoDao.getListaMovimentacaoSecao(nome);
		}
		else
		{
			//filtra pelo nome do produto
			if(this.filtarPesquisaMovimentacao.getText().toString().length() ==7)
			{
				listaMovimentacao = MovimentacaoDao.getListaMovimentacaoProduto(nome);
			}
			else
			{
				//produto e secao juntos que contem pelomenos uma parte do nome
				//igual ao digitado no textbox de pesquisa
				listaMovimentacao = MovimentacaoDao.getListaMovimentacaoProdutoSecao(nome);
			}
		}
		this.preencheLsbPesquisa(listaMovimentacao);
	}
	
	@Listen("onSelect = #lsbPesqMovimentacao")
	public void obtemMovimentacaoSelecionada() throws SQLException, InterruptedException, IOException
	{
		//obtem a movimentação selecionada na aba pesquisa
		//preenche a aba de manter colocando o id, e selecionando a seção e os produtos da movimentação escolhida
		
			
		Listcell lc01 = new Listcell();
		lc01 = (Listcell)this.lsbPesqMovimentacao.getSelectedItem().getChildren().get(0);
		
		int idMovimentacao = Integer.parseInt(lc01.getLabel().toString());
		
		MovimentacaoDao movimentacaoDao = new MovimentacaoDao();
		Movimentacao movimentacao = movimentacaoDao.getMovimentacaoId(idMovimentacao);
		
		if(idMovimentacao > 0)
		{
		
			this.intIdMovimentacao.setText(Integer.toString(movimentacao.getIdMovimentacao()));
			this.combSecaoMovimentacao.setText(movimentacao.getSecao().getNomeSecao());
			
			ArrayList<Produto> listaProduto = movimentacao.getListaProduto();
			ListModelList<String> listaNomeProduto = new ListModelList<String>();
			
			while(!listaProduto.isEmpty())
			{
				listaNomeProduto.add(listaProduto.remove(0).getNomeProduto());
			}
			this.chosenProdutosMovimentacao.setSelectedObjects(listaNomeProduto);
			
			
			this.limpaLsbPesquisa();
			this.txtPesqMovimentacao.setText("");
			this.tabManterMovimentacao.setSelected(true);
		}
			else Utilidade.mensagem(movimentacao.getMensagemErro());
		
	}
	
	public void preenchecombSecaoMovimentacao()throws SQLException, InterruptedException,IOException
	{
		//preenche o combobox de seções com todas as seções cadastradas no sistema
		
		combSecaoMovimentacao.getItems().clear();
		SecaoDao secaoDao = new SecaoDao();
		ArrayList<Secao> listaSecao = new ArrayList<Secao>();
		
		listaSecao = secaoDao.getTodasSecoes();
		
		while(!listaSecao.isEmpty())
		{
			this.combSecaoMovimentacao.appendItem(listaSecao.remove(0).getNomeSecao());
		}
	}
	
	public void PreencheChosenbox() throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, InterruptedException
	{
		//preenche o chosenbox de produtos com todos os produtos cadastrados no sistema
		ProdutoDao produtoDao = new ProdutoDao();
		ArrayList<Produto> listaProduto = produtoDao.getTodosProdutos();
		ListModelList<String> listaNomeProduto = new ListModelList<String>();
		
		while(!listaProduto.isEmpty())
		{
			listaNomeProduto.add(listaProduto.remove(0).getNomeProduto());
		}
		this.chosenProdutosMovimentacao.setModel(listaNomeProduto);
	}
	
	@Listen("onClick = #btnLimparMovimentacao")
	public void limpaDados() throws SQLException, InterruptedException, IOException
	{
		//limpa todos os campos da aba de manutenção e pesquisa
		OPCAO = "";
		this.intIdMovimentacao.setText("");
		this.combSecaoMovimentacao.setText("");
		this.chosenProdutosMovimentacao.clearSelection();
		this.txtPesqMovimentacao.setText("");
		this.limpaLsbPesquisa();
	}
	
	
}
