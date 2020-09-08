package Estoque.Controle;

import java.io.IOException;
import java.sql.SQLException;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import Estoque.Dao.UsuarioDao;
import Estoque.Modelo.Usuario;
import Estoque.Util.Utilidade;

public class LoginPermissaoControle extends SelectorComposer<Window>{
	
	@Wire
	Checkbox checkMostrarSenha;
	@Wire
	Textbox txtUsu, txtSenha;
	@Wire
	Button btnLogin, btnLogout;
	@Wire
	Window Login;
	@Wire
	Menuitem menuItemLogin, menuItemUsuario, menuItemRelatorio;
	@Wire
	Menu menuPrincipal;

	//ao selecionar o checkbox de mostrar a senha 
	//transforma o campo da senha em caracteres legiveis
	@Listen("onCheck = #checkMostrarSenha")
	public void mostrarSenha()
	{ 
		if(checkMostrarSenha.isChecked())this.txtSenha.setType("text");
		else this.txtSenha.setType("password");
	}

	//ao clicar no botão login
	@Listen("onClick = #btnLogin")
	public void Login() throws ClassNotFoundException, SQLException, InterruptedException, IOException
	{
		Sessions.getCurrent().setAttribute("login", 0);
		if(this.validaDadosLogin())
		{
			Usuario usu = new Usuario();
			UsuarioDao usuDao = new UsuarioDao();
			
			usu = this.atualizaDadosLogin(usu);
			
			//verifica se o login e a senha estão cadastrados no banco de dados
			if(usuDao.validaLogin(usu))
			{
				//desabilita o botão do login e habilita o do logout
				this.btnLogin.setDisabled(true);
				this.btnLogout.setDisabled(false);
				
				//guarda se o login está feito
				Sessions.getCurrent().setAttribute("login", 1);
				
				//guarda a permissão do usuario
				usu = usuDao.getUsuarioLogin(usu.getEmail());
				if(usu.isAdministrador()) Sessions.getCurrent().setAttribute("administrador", 1);
				else Sessions.getCurrent().setAttribute("administrador", 0);
				
				//fecha a janela do login
				this.Login.detach();
				

				//emite uma mensagem para o usuario avisando se o login foi efetuado ou não.
				Utilidade.mensagem("Bem vindo(a) ");
			}
			else
			{
				Utilidade.mensagem("Usuario ou senha invalidos.");
			}
		}
	}
	
	@Listen("onClick = #btnLogout")
	public void logout() 
	{
		//ao clicar no botão logout
		//limpa os campos da senha e login
		//guarda o login = 0
		this.txtSenha.setText("");
		this.txtUsu.setText("");
		Sessions.getCurrent().setAttribute("login", 0);
		this.btnLogin.setDisabled(false);
		this.btnLogout.setDisabled(true);
	}
	
	public Usuario atualizaDadosLogin(Usuario usuario)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		//atualiza o objeto usuario com as informações que estão na tela
		usuario.setEmail(this.txtUsu.getValue());
		usuario.setSenha(this.txtSenha.getValue());
		return usuario;
	}

	public boolean validaDadosLogin()throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		//verifica se os dados digitados na tela estão corretos
		//se não estiver emite uma mensagem de erro
		int conta = 0;
		String mensagem = "";
		
		if(this.txtUsu.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "Informe o nome \n";
		}
		
		if(this.txtSenha.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "Informe a senha \n";
		}
		if(conta > 0)
		{
			Utilidade.mensagem(mensagem);
			return false;
		}
		return true;
	}

}
