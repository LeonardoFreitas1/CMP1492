package Estoque.Controle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import Estoque.Dao.UsuarioDao;
import Estoque.Modelo.Usuario;
import Estoque.Util.Utilidade;

public class UsuarioControle  extends SelectorComposer<Window>{

	@Wire
	Checkbox checkSenha;
	@Wire
	Textbox txtNovoLoginUsu, txtNovaSenhaUsu, txtPesqUsu;
	@Wire
	Button btnIncluirUsu, btnAlterarUsu, btnExcluirUsu, btnLimparUsu, btnAtualizarPesqUsu, btnLimparPesqUsuario;	
	@Wire
	Combobox combPermissao;
	@Wire
	Listbox lsbPesqUsu;
	@Wire 
	Intbox intIdUsuario;
	@Wire
	Comboitem combItemAdmin, combItemPadrao;
	@Wire
	Tab tabManterUsuario;
	
	public String OPCAO = "";
	

	//ao criar a janela de usuario define as permissões
	@Listen("onCreate=#Usuario")
	public void permissao()
	{
		//de acordo com a permissao bloqueia ou desbloqueia os botões de incluir, alterar e excluir
		int admin = Integer.parseInt(Sessions.getCurrent().getAttribute("administrador").toString());
		if(admin == 1)
		{
			//desbloqueia
			btnIncluirUsu.setDisabled(false);
			btnAlterarUsu.setDisabled(false);
			btnExcluirUsu.setDisabled(false);
		}
		else
		{
			//bloqueia
			btnIncluirUsu.setDisabled(true);
			btnAlterarUsu.setDisabled(true);
			btnExcluirUsu.setDisabled(true);
		}
	}
	
	//recebe um usuario com dados desatualizados
	//retorna o usuario com os dados atualizados de acordo com o que esta digitado/selecionado na tela
	public Usuario atualizaDadosUsuario(Usuario usuario)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		this.validaDados();//verifica se os dados na tela são válidos
		
		//se não for inclusão, inclui o id do usuario na tela(intbox do id)
		if(!OPCAO.equals("I")) 
		{
			usuario.setIdUsuario(this.intIdUsuario.getValue());		
		}
		usuario.setLogin(this.txtNovoLoginUsu.getValue());
		usuario.setSenha(this.txtNovaSenhaUsu.getValue());
		if(this.combPermissao.getText().length() == 13) 
		{
			usuario.setAdministrador(true);		
		}
		else
		{
			usuario.setAdministrador(false);
		}

		this.txtPesqUsu.setText("");
		return usuario;
	}
	
	//verifica se os dados foram selecionados e os digitados não são nulos
	public boolean validaDados()throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		int conta = 0;
		String mensagem = "";
		
		if(this.txtNovoLoginUsu.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "Informe o nome \n";
		}
		
		if(this.txtNovaSenhaUsu.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "Informe a senha \n";
		}
		if(this.combPermissao.getSelectedItem().getLabel().length() == 0)
		{
			conta++;
			mensagem += "Escolha uma permissão \n";
		}
		if(conta > 0)
		{
			Utilidade.mensagem(mensagem);
			return false;
		}
		return true;
	}		

		
	//preenche a lista de pesquisa
	public void preencheLsbPesquisa()throws SQLException, InterruptedException,IOException
	{
		this.limpaLsbPesquisa();//limpa a lista
		UsuarioDao UsuarioDao = new UsuarioDao();
		ArrayList<Usuario> listaUsuario = new ArrayList<Usuario>();
		
		//obtem todos os usuarios que possuem no login o que está digitado na caixa de pesquisa
		listaUsuario = UsuarioDao.getListaLoginNome(txtPesqUsu.getText().toString());
		
		while(!listaUsuario.isEmpty())
		{
			Listitem li = new Listitem();
			Listcell lc01 = new Listcell();
			Listcell lc02 = new Listcell();
			Listcell lc03 = new Listcell();
			
			//preenche o campo do id
			lc01.setLabel(Integer.toString(listaUsuario.get(0).getIdUsuario()));
			
			//preenche o campo do login 
			lc02.setLabel(listaUsuario.get(0).getLogin());
			
			//preenche o campo da permissão
			if(listaUsuario.remove(0).isAdministrador())
			{
				lc03.setLabel("Administrador");
			}
			else
			{
				lc03.setLabel("Padrão");
			}
			
			//insere na linha os campos 
			li.appendChild(lc01);
			li.appendChild(lc02);
			li.appendChild(lc03);
			
			//insere a linha na lista
			this.lsbPesqUsu.appendChild(li);
		}
	}	
		
	//ao selecionar o checkbox de mostrar a senha 
	//transforma o campo da senha em caracteres legiveis
	@Listen("onCheck = #checkSenha")
	public void mostrarSenha()
	{
		if(checkSenha.isChecked())this.txtNovaSenhaUsu.setType("text");
		else this.txtNovaSenhaUsu.setType("password");
	}
		
	//ao clicar no botão incluir
	//inclui o usuario no banco
	@Listen("onClick = #btnIncluirUsu")
	public void onClickbtnIncluir()throws Exception
	{
		OPCAO = "I";
		UsuarioDao usuarioDao = new UsuarioDao();
		Usuario usuario = new Usuario();

		if(validaDados())//verifica se os dados são válidos
		{
			usuario = this.atualizaDadosUsuario(usuario);// atualiza os dados do objeto
			usuario = usuarioDao.incluir(usuario);//tenta incluir no banco 
			
			if(usuario.getMensagemErro() == "Usuario incluido com sucesso!")
			{
				this.limpaDados();
			}
			Utilidade.mensagem(usuario.getMensagemErro());
		}
	}
	
	//ao clicar no botão alterar
	//altera o usuario no banco
	@Listen("onClick = #btnAlterarUsu")
	public void onClickbtnAlterar()throws Exception
	{
		OPCAO = "A";
		Usuario usuario = new Usuario();
		UsuarioDao usuarioDAO = new UsuarioDao();

		if(this.intIdUsuario.getValue() == null || this.intIdUsuario.getText().isEmpty() || this.intIdUsuario.getText().equals(""))
		{
			Utilidade.mensagem("Selecione um usuario para alterar.");
		}
		else
		{
			usuario = this.atualizaDadosUsuario(usuario);
			usuario = usuarioDAO.alterar(usuario);
										
			Utilidade.mensagem(usuario.getMensagemErro());
		}
		this.limpaDados();
	}
	
	//ao clicar no botão excluir
	//exclui o usuario no banco
	@Listen("onClick = #btnExcluirUsu")
	public void onClickbtnExcluir()throws Exception
	{
		OPCAO = "E";
		Usuario usuario = new Usuario();
		UsuarioDao usuarioDAO = new UsuarioDao();
		
		if(this.intIdUsuario.getValue() == null || this.intIdUsuario.getText().isEmpty() || this.intIdUsuario.getText().equals(""))
		{
			Utilidade.mensagem("Selecione um usuario para excluir.");
		}
		else
		{				
			usuario = this.atualizaDadosUsuario(usuario);
			usuario = usuarioDAO.excluir(usuario);
			
			Utilidade.mensagem(usuario.getMensagemErro());
		}
		
		this.limpaDados();
	}
	
	//ao clicar no botão limpar na tela de pesquisa
	//limpa a lista de pesquisa
	@Listen("onClick = #btnLimparPesqUsuario")
	public void limpaLsbPesquisa()
	{
		this.lsbPesqUsu.getItems().clear();
	}
	
	//ao clicar no botao atualizar pesquisa
	//atualiza a lista de pesquisa filtrando as informações com o que está digitado no textbox pesquisa
	@Listen("onClick = #btnAtualizarPesqUsu")
	public void onClickbtnAtualizarLista()throws SQLException, InterruptedException, IOException
	{
		this.preencheLsbPesquisa();
	}
	
	//ao clicar em um usuario na lista de pesquisa
	//abre a aba de manutenção e a preenche com os dados do usuario selecionado
	@Listen("onSelect = #lsbPesqUsu")
	public void obtemUsuarioSelecionado() throws SQLException, InterruptedException
	{
		//obtem a posição na lista do usuario selecionado
		int indice = this.lsbPesqUsu.getSelectedIndex();
		int id = 0;
		UsuarioDao usuarioDao = new UsuarioDao();
		Usuario usuario = new Usuario();
		
		if(indice >=0)
		{
			Listcell lc01 = new Listcell();
			
			//obtem o primeiro campo da linha(id) do usuario selecionado na lista de pesquisa 
			lc01 = (Listcell)this.lsbPesqUsu.getSelectedItem().getChildren().get(0);
			id = Integer.parseInt(lc01.getLabel().toString());
			//obtem o usuario cadastrado no banco com o id selecionado
			usuario = usuarioDao.getUsuarioId(id);
			
			if(usuario != null)
			{
				//preenche a aba de manutenção com os dados do usuario selecionado
				this.intIdUsuario.setText(Integer.toString(usuario.getIdUsuario()));
				this.txtNovoLoginUsu.setText(usuario.getLogin());
				this.txtNovaSenhaUsu.setText(usuario.getSenha());
				if(usuario.isAdministrador()) this.combPermissao.setText("Administrador");
				else this.combPermissao.setText("Padrão");
				
				//limpa a aba de pesquisa
				this.limpaLsbPesquisa();
				this.txtPesqUsu.setText("");
				this.tabManterUsuario.setSelected(true);
			}
			else Utilidade.mensagem(usuario.getMensagemErro());
		}
	}
	
	//ao clicar no botão limpar na aba manutenção 
	//limpa todos os campos da aba manutenção
	@Listen("onClick = #btnLimparUsu")
	public void limpaDados()
		{
			OPCAO = "";
			this.intIdUsuario.setText("");
			this.txtNovoLoginUsu.setText("");
			this.txtNovaSenhaUsu.setText("");
			this.txtPesqUsu.setText("");
			this.combPermissao.setText("");
			this.checkSenha.setChecked(false);
			this.limpaLsbPesquisa();				
		}
}
