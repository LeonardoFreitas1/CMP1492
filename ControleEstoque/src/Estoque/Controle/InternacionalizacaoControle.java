package Estoque.Controle;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


public class InternacionalizacaoControle extends SelectorComposer<Window>{
	@Wire
	Combobox combInternacionalizacao;
	@Wire
	Menuitem menuItemLogin, menuItemUsuario, menuItemSecao, menuItemGrupo, menuItemProduto, menuItemMovimentacao, menuItemRelatorio;
	@Wire
	Menu menuPrincipal;
	
	
	
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
	Button btnLimparPesqMovimentacao;
	@Wire
	Button btnAtualizarPesqMovimentacao;
	@Wire
	Label secao,Produto,filtro,pesquisa;
	@Wire
	Window Movimentacao;
	@Wire
	Listheader lhProduto,lhSecao;
	@Wire 
	Tab tabManterMovimentacao,tabPesquisarMovimentacao;
	
	//qunado a tela de menu abrir o idioma j� selecionado ser� o portugu�s
	@Listen("onCreate = #wMenu")
	public void selecionarPortugues()
	{
		this.combInternacionalizacao.setSelectedIndex(2);
		this.mudarIdioma();
	}
	
	//ao criar a tela de movimenta��o
	//o idioma � o mesmo que que est� selelcionado na tela de menu
	@Listen("onCreate = #Movimentacao")
	public void mudarIdiomaMovimentacao()
	{
		String idioma = Sessions.getCurrent().getAttribute("idioma").toString();
		if(idioma.length() == 9)
		{
			btnIncluirMovimentacao.setLabel("Incluir");
			btnAlterarMovimentacao.setLabel("Alterar");
			btnExcluirMovimentacao.setLabel("Excluir");
			btnLimparMovimentacao.setLabel("Limpar");
			btnLimparPesqMovimentacao.setLabel("Limpar");
			btnAtualizarPesqMovimentacao.setLabel("Pesquisar");
			secao.setValue("Se��o");
			Produto.setValue("Produto");
			filtro.setValue("Filtrar pesquisa");
			pesquisa.setValue("Pesquisar movimenta��es");
			lhProduto.setLabel("Produto");
			lhSecao.setLabel("Se��o");
			tabManterMovimentacao.setLabel("Manter Registro");
			tabPesquisarMovimentacao.setLabel("Pesquisar");
		}
		else
		{
			if(idioma.length() == 6)
			{
				btnIncluirMovimentacao.setLabel("Include");
				btnAlterarMovimentacao.setLabel("Change");
				btnExcluirMovimentacao.setLabel("Delete");
				btnLimparMovimentacao.setLabel("Clean");
				btnLimparPesqMovimentacao.setLabel("Clean");
				btnAtualizarPesqMovimentacao.setLabel("Search");
				secao.setValue("Section");
				Produto.setValue("Product");
				filtro.setValue("Filter search");
				pesquisa.setValue("Search movement");
				lhProduto.setLabel("Product");
				lhSecao.setLabel("Section");
				tabManterMovimentacao.setLabel("keep record");
				tabPesquisarMovimentacao.setLabel("Search");
			}
			else
			{
				btnIncluirMovimentacao.setLabel("Incluir");
				btnAlterarMovimentacao.setLabel("Cambio");
				btnExcluirMovimentacao.setLabel("Eliminar");
				btnLimparMovimentacao.setLabel("Limpiar");
				btnLimparPesqMovimentacao.setLabel("Limpiar");
				btnAtualizarPesqMovimentacao.setLabel("Buscar");
				secao.setValue("Secci�n");
				Produto.setValue("Producto");
				filtro.setValue("Filtrar");
				pesquisa.setValue("Buscar movimientos");
				lhProduto.setLabel("Produto");
				lhSecao.setLabel("Secci�n");
				tabManterMovimentacao.setLabel("Mantenimiento");
				tabPesquisarMovimentacao.setLabel("Buscar");
			}
		}
	}
	
	//ao selecionar um idioma
	//a tela as informacoes da tela de menu ficam no idioma selecionado
	@Listen("onSelect = #combInternacionalizacao")
	public void mudarIdioma()
	{
		String idioma = combInternacionalizacao.getText().toString();
		Sessions.getCurrent().setAttribute("idioma", idioma);
		if(idioma.length() == 9)
		{
			this.menuItemLogin.setLabel("Entrar/Sair");
			this.menuItemUsuario.setLabel("Usu�rio");
			this.menuItemSecao.setLabel("Se��o");
			this.menuItemGrupo.setLabel("Grupo");
			this.menuItemProduto.setLabel("Produto");
			this.menuItemMovimentacao.setLabel("Movimentacao");
			this.menuItemRelatorio.setLabel("Relat�rio");	
			this.menuPrincipal.setLabel("Manuten��o de registros");
		}
		else
		{
			if(idioma.length() == 6)
			{
				this.menuItemLogin.setLabel("Login/Logout");
				this.menuItemUsuario.setLabel("Username");
				this.menuItemSecao.setLabel("Section");
				this.menuItemGrupo.setLabel("Group");
				this.menuItemProduto.setLabel("Product");
				this.menuItemMovimentacao.setLabel("Movement");
				this.menuItemRelatorio.setLabel("Report");	
				this.menuPrincipal.setLabel("Maintenance");
			}
			else
			{
				this.menuItemLogin.setLabel("Iniciar sesi�n/Cerrar sesi�n");
				this.menuItemUsuario.setLabel("Usuario");
				this.menuItemSecao.setLabel("Secci�n");
				this.menuItemGrupo.setLabel("Grupo");
				this.menuItemProduto.setLabel("Producto");
				this.menuItemMovimentacao.setLabel("Movimiento");
				this.menuItemRelatorio.setLabel("Informe");	
				this.menuPrincipal.setLabel("Mantenimiento de registros");
			}
		}
	}
}
