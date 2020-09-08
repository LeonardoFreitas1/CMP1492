package Estoque.Controle;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

public class MenuControle extends SelectorComposer<Window>{

	@Wire
	Menuitem menuItemLogin, menuItemUsuario, menuItemCurso, menuItemDisciplina, menuItemTurma, menuItemFalta, menuItemNota, menuItemRelatorio;
	@Wire
	Menu menuPrincipal;
	@Wire
	Window Login,menu;
	
	//ao criar a tela do menu
	@Listen("onCreate = #Menu")
	public void inicio()
	{		
		//guarda que o login é 0(não foi feto)
		Sessions.getCurrent().setAttribute("login", 0);
		
		//abre a janela do login
		this.abrirJanelaLogin();

		//bloqueia as opções de acessar registros e relatorios
		this.menuPrincipal.setDisabled(true);
		this.menuItemRelatorio.setDisabled(true);
	}	
	
	//quando o mouse estiver sobre a tela do menu verifica as permissões 
	//e bloqueia ou e as desbloqueia dependendo se o login foi feito
	@Listen("onMouseOver = #wMenu")
	public void permissao()
	{
		int logado = Integer.parseInt(Sessions.getCurrent().getAttribute("login").toString());
		if(logado == 1)
		{
			this.menuPrincipal.setDisabled(false);
			this.menuItemRelatorio.setDisabled(false);
		}
		else
		{
			this.menuPrincipal.setDisabled(true);
			this.menuItemRelatorio.setDisabled(true);
		}
	}
	
	//ao clicar no menuitem abre a janela correspondente
	
	@Listen("onClick=#menuItemRelatorio")
	public void abrirJanelaRelatorio()
	{
		String window = "Relatorio.zul";
		Window wAberta = (Window)Executions.createComponents(window, null, null);
		wAberta.doModal();
	}	
		
	@Listen("onClick=#menuItemLogin")
	public void abrirJanelaLogin()
	{
		String window = "Login.zul";
		Window wAberta = (Window)Executions.createComponents(window, null, null);
		wAberta.doModal();
	}
	
	@Listen("onClick=#menuItemUsuario")
	public void abrirJanelaUsuario()
	{
		String window = "Usuario.zul";
		Window wAberta = (Window)Executions.createComponents(window, null, null);
		wAberta.doModal();
	}
	
	@Listen("onClick=#menuItemCurso")
	public void abrirJanelaCurso()
	{
		String window = "Curso.zul";
		Window wAberta = (Window)Executions.createComponents(window, null, null);
		wAberta.doModal();
	}
	
	@Listen("onClick=#menuItemDisciplina")
	public void abrirJanelaDisciplina()
	{
		String window = "Disciplina.zul";
		Window wAberta = (Window)Executions.createComponents(window, null, null);
		wAberta.doModal();
	}
	
	@Listen("onClick=#menuItemTurma")
	public void abrirJanelaTurma()
	{
		String window = "Turma.zul";
		Window wAberta = (Window)Executions.createComponents(window, null, null);
		wAberta.doModal();
	}
}
