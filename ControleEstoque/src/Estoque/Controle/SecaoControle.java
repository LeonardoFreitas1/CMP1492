package Estoque.Controle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import Estoque.Dao.SecaoDao;
import Estoque.Modelo.Secao;
import Estoque.Util.Utilidade;
	
public class SecaoControle extends SelectorComposer<Window>{
	
	@Wire
	Tab tabManterSecao;
	@Wire
	Intbox intIdSecao;
	@Wire
	Textbox txtNomeSecao;
	@Wire
	Button btnIncluirSecao;
	@Wire
	Button btnAlterarSecao;
	@Wire
	Button btnExcluirSecao;
	@Wire
	Button btnLimparSecao;
	@Wire
	Textbox txtPesqSecao;
	@Wire
	Listbox lsbPesqSecao;
	@Wire
	Button btnLimparPesqSecao;
	@Wire
	Button btnAtualizarPesqSecao;
		
	public String OPCAO = "";

	//ao criar a janela de seção define as permissões
	//de acordo com a permissao bloqueia ou desbloqueia os botões de incluir, alterar e excluir
	@Listen("onCreate=#Secao")
	public void permissao()
	{
		int admin = Integer.parseInt(Sessions.getCurrent().getAttribute("administrador").toString());
		if(admin == 1)
		{
			//desbloqueia
			btnIncluirSecao.setDisabled(false);
			btnAlterarSecao.setDisabled(false);
			btnExcluirSecao.setDisabled(false);
		}
		else
		{
			//bloqueia
			btnIncluirSecao.setDisabled(true);
			btnAlterarSecao.setDisabled(true);
			btnExcluirSecao.setDisabled(true);
		}
	} 
	
	//limpa a lista de pesquisa
	public void limpaLsbPesquisa()
	{
		this.lsbPesqSecao.getItems().clear();
	}

	//preenche a lista de pesquisa
	public void preencheLsbPesquisa()throws SQLException, InterruptedException,IOException
	{
		//limpa a lista
		this.limpaLsbPesquisa();
		SecaoDao SecaoDao = new SecaoDao();
		ArrayList<Secao> listaSecao = new ArrayList<Secao>();
		
		//obtem todas as seções que possuem no nome o que está digitado na caixa de pesquisa
		listaSecao = SecaoDao.getListaSecaoNome(this.txtPesqSecao.getText().toString());
		
		while(!listaSecao.isEmpty())
		{
			Listitem li = new Listitem();
			Listcell lc01 = new Listcell();
			Listcell lc02 = new Listcell();
			
			//preenche o campo do id
			lc01.setLabel(Integer.toString(listaSecao.get(0).getIdSecao()));
			
			//preenche o campo do nome
			lc02.setLabel(listaSecao.remove(0).getNomeSecao());
			
			//insere na linha os campos 
			li.appendChild(lc01);
			
			//insere a linha na lista
			li.appendChild(lc02);
			this.lsbPesqSecao.appendChild(li);
		}
	}
	
	//ao clicar no botão incluir
	//inclui a seção no banco
	@Listen("onClick = #btnIncluirSecao")
	public void onClickbtnIncluir()throws Exception
	{
		OPCAO = "I";
		SecaoDao secaoDao = new SecaoDao();
		Secao secao = new Secao();
		
		if(validaDados())
		{
			secao = this.atualizaDados(secao);
			secao = secaoDao.incluir(secao);
			Utilidade.mensagem(secao.getMensagemErro());
		}
		if(secao.getMensagemErro() == "Seção incluida com sucesso!")
		{
			this.limpaDados();
		}
	}
	
	//ao clicar no botão alterar
	//altera a seção no banco
	@Listen("onClick = #btnAlterarSecao")
	public void onClickbtnAlterar()throws Exception
	{
		OPCAO = "A";
		Secao secao = new Secao();
		SecaoDao secaoDAO = new SecaoDao();

		if(this.intIdSecao.getValue() == null || this.intIdSecao.getText().isEmpty() || this.intIdSecao.getText().equals(""))
		{
			Utilidade.mensagem("Selecione uma seção para alterar.");
		}
		else
		{
			secao = this.atualizaDados(secao);
			secao = secaoDAO.alterar(secao);
			Utilidade.mensagem(secao.getMensagemErro());
		}
		this.limpaDados();
	}
	
	//ao clicar no botão excluir
	//exclui a seção no banco
	@Listen("onClick = #btnExcluirSecao")
	public void onClickbtnExcluir()throws Exception
	{
		OPCAO = "E";
		Secao secao = new Secao();
		SecaoDao secaoDAO = new SecaoDao();
		
		if(this.intIdSecao.getValue() == null || this.intIdSecao.getText().isEmpty() || this.intIdSecao.getText().equals(""))
		{
			Utilidade.mensagem("Selecione uma seção para excluir.");
		}
		else
		{
			secao = this.atualizaDados(secao);
			secao = secaoDAO.excluir(secao);
			Utilidade.mensagem(secao.getMensagemErro());
		}
		
		this.limpaDados();
	}
	
	//verifica se os dados digitados não são nulos
	public boolean validaDados()throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		int conta = 0;
		String mensagem = "";
		
		if(this.txtNomeSecao.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "Informe o nome da seção \n";
		}
		if(conta > 0)
		{
			Utilidade.mensagem(mensagem);
			return false;
		}
		return true;
	}

	//recebe uma seção com dados desatualizados
	//retorna a seção com os dados atualizados de acordo com o que esta digitado na tela
	public Secao atualizaDados(Secao secao)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		//se não for inclusão, inclui o id da seção na tela(intbox do id)
		if(!OPCAO.equals("I")) secao.setIdSecao(this.intIdSecao.getValue());
		
		secao.setNomeSecao(this.txtNomeSecao.getValue());
		this.txtPesqSecao.setText("");
		return secao;
	}
	
	@Listen("onClick = #btnLimparPesqSecao")
	public void onClickbtnLimparLista()
	{
		this.limpaLsbPesquisa();
	}
	
	//ao clicar no botao atualizar pesquisa
	//atualiza a lista de pesquisa filtrando as informações com o que está digitado no textbox pesquisa
	@Listen("onClick = #btnAtualizarPesqSecao")
	public void onClickbtnAtualizarLista()throws SQLException, InterruptedException, IOException
	{
		this.preencheLsbPesquisa();
	}
	
	//ao clicar em uma seção na lista de pesquisa
	//abre a aba de manutenção e a preenche com os dados da seção selecionada
	@Listen("onSelect = #lsbPesqSecao")
	public void obtemSecaoSelecionada() throws SQLException, InterruptedException
	{
		//obtem a posição na lista da seção selecionado
		int indice = this.lsbPesqSecao.getSelectedIndex();
		int id = 0;
		SecaoDao secaoDao = new SecaoDao();
		Secao secao = new Secao();
		
		if(indice >=0)
		{
			Listcell lc01 = new Listcell();
			
			//obtem o primeiro campo da linha(id) da seção selecionada na lista de pesquisa 
			lc01 = (Listcell)this.lsbPesqSecao.getSelectedItem().getChildren().get(0);
			id = Integer.parseInt(lc01.getLabel().toString());
			
			//obtem a seção cadastrada no banco com o id selecionado
			secao = secaoDao.getSecaoId(id);
			
			//preenche a aba de manutenção com os dados da seção selecionada
			if(secao != null)
			{
				this.intIdSecao.setText(Integer.toString(secao.getIdSecao()));
				this.txtNomeSecao.setText(secao.getNomeSecao());
				
				//limpa a aba de pesquisa
				this.limpaLsbPesquisa();
				this.txtPesqSecao.setText("");
				this.tabManterSecao.setSelected(true);
			}
			else Utilidade.mensagem(secao.getMensagemErro());
		}
	}
	
	//ao clicar no botão limpar na aba manutenção 
	//limpa todos os campos da aba manutenção
	@Listen("onClick = #btnLimparSecao")
	public void limpaDados()
	{
		OPCAO = "";
		this.intIdSecao.setText("");
		this.txtNomeSecao.setText("");
		this.txtPesqSecao.setText("");
		this.limpaLsbPesquisa();
	}

}
