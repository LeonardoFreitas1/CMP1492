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
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import Estoque.Dao.GrupoDao;
import Estoque.Modelo.Grupo;
import Estoque.Util.Utilidade;

public class GrupoControle extends SelectorComposer<Window>{


	@Wire
	Menuitem menuItemLogin, menuItemUsuario, menuItemRelatorio;
	@Wire
	Menu menuPrincipal;
		@Wire
		Tab tabManterGrupo;
		@Wire
		Intbox intIdGrupo;
		@Wire
		Textbox txtNomeGrupo;
		@Wire
		Button btnIncluirGrupo;
		@Wire
		Button btnAlterarGrupo;
		@Wire
		Button btnExcluirGrupo;
		@Wire
		Button btnLimparGrupo;
		@Wire
		Textbox txtPesqGrupo;
		@Wire
		Listbox lsbPesqGrupo;
		@Wire
		Button btnLimparPesqGrupo;
		@Wire
		Button btnAtualizarPesqGrupo;
		@Wire
		Window Grupo;
			
		public String OPCAO = "";
	
		//ao criar a tela de grupo ativa ou desativa(depende da permissão do usuario logado)
		//a opção de clicar nos botoes de incluir, alterar e excluir
		@Listen("onCreate=#Grupo")
		public void permissao()
		{
			//obtem se o usuario logodo é administrador(1) ou padrão(0)
			int admin = Integer.parseInt(Sessions.getCurrent().getAttribute("administrador").toString());
			if(admin == 1)
			{
				btnIncluirGrupo.setDisabled(false);
				btnAlterarGrupo.setDisabled(false);
				btnExcluirGrupo.setDisabled(false);
			}
			else
			{
				btnIncluirGrupo.setDisabled(true);
				btnAlterarGrupo.setDisabled(true);
				btnExcluirGrupo.setDisabled(true);
			}
		}
		
		//limpa lista de pesquisa
		public void limpaLsbPesquisa()
		{
			this.lsbPesqGrupo.getItems().clear();
		}
		
		//preenche a lista de pesquisa de acordo com o que esta digitado no textbox de pesquisa
		public void preencheLsbPesquisa()throws SQLException, InterruptedException,IOException
		{
			this.limpaLsbPesquisa();
			GrupoDao GrupoDao = new GrupoDao();
			ArrayList<Grupo> listaGrupo = new ArrayList<Grupo>();
			
			listaGrupo = GrupoDao.getListaGrupoNome(this.txtPesqGrupo.getText().toString());
			
			while(!listaGrupo.isEmpty())
			{
				Listitem li = new Listitem();
				Listcell lc01 = new Listcell();
				Listcell lc02 = new Listcell();
				
				lc01.setLabel(Integer.toString(listaGrupo.get(0).getIdGrupo()));
				lc02.setLabel(listaGrupo.remove(0).getNomeGrupo());
				
				li.appendChild(lc01);
				li.appendChild(lc02);
				this.lsbPesqGrupo.appendChild(li);
			}
		}
		
		//inclui um grupo no banco de dados
		@Listen("onClick = #btnIncluirGrupo")
		public void onClickbtnIncluir()throws Exception
		{
			OPCAO = "I";
			GrupoDao grupoDao = new GrupoDao();
			Grupo grupo = new Grupo();
			
			if(validaDados())
			{
				grupo = this.atualizaDados(grupo);
				grupo = grupoDao.incluir(grupo);
				Utilidade.mensagem(grupo.getMensagemErro());
			}
			if(grupo.getMensagemErro() == "Grupo incluido com sucesso!")
			{
				this.limpaDados();
			}
		}
		
		//altera um grupo no banco de dados
		@Listen("onClick = #btnAlterarGrupo")
		public void onClickbtnAlterar()throws Exception
		{
			OPCAO = "A";
			Grupo grupo = new Grupo();
			GrupoDao grupoDAO = new GrupoDao();
			
			//verifica se foi selecionado um grupo.
			//se o id não for nulo foi selecionado
			if(this.intIdGrupo.getValue() == null || this.intIdGrupo.getText().isEmpty() || this.intIdGrupo.getText().equals(""))
			{
				Utilidade.mensagem("Selecione um grupo para alterar.");
			}
			else
			{
				grupo = this.atualizaDados(grupo);
				grupo = grupoDAO.alterar(grupo);
				Utilidade.mensagem(grupo.getMensagemErro());
			}
			this.limpaDados();
		}
		
		//exclui um grupo do banco de dados
		@Listen("onClick = #btnExcluirGrupo")
		public void onClickbtnExcluir()throws Exception
		{
			OPCAO = "E";
			Grupo grupo = new Grupo();
			GrupoDao grupoDAO = new GrupoDao();
			
			if(this.intIdGrupo.getValue() == null || this.intIdGrupo.getText().isEmpty() || this.intIdGrupo.getText().equals(""))
			{
				Utilidade.mensagem("Selecione um grupo para excluir.");
			}
			else
			{
				grupo = this.atualizaDados(grupo);
				grupo = grupoDAO.excluir(grupo);
				Utilidade.mensagem(grupo.getMensagemErro());
			}
			
			this.limpaDados();
		}
		
		//verifica se os dados digitados são válidos
		public boolean validaDados()throws SQLException, InterruptedException, IOException, ClassNotFoundException 
		{
			int conta = 0;
			String mensagem = "";
			
			if(this.txtNomeGrupo.getText().toString().length() == 0)
			{
				conta++;
				mensagem += "Informe o nome do grupo \n";
			}
			if(conta > 0)
			{
				Utilidade.mensagem(mensagem);
				return false;
			}
			return true;
		}
		
		//atualiza as informações de um objeto grupo para o que está digitado na tela
		public Grupo atualizaDados(Grupo grupo)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
		{
			if(!OPCAO.equals("I")) grupo.setIdGrupo(this.intIdGrupo.getValue());
			
			grupo.setNomeGrupo(this.txtNomeGrupo.getValue());
			this.txtPesqGrupo.setText("");
			return grupo;
		}
		
		//limpa a lista de pesquisa que fica na aba pesquisa
		@Listen("onClick = #btnLimparPesqGrupo")
		public void onClickbtnLimparLista()
		{
			this.limpaLsbPesquisa();
		}
		
		//atualiza a lista de pesquisa
		@Listen("onClick = #btnAtualizarPesqGrupo")
		public void onClickbtnAtualizarLista()throws SQLException, InterruptedException, IOException
		{
			this.preencheLsbPesquisa();
		}
		
		//obtem o grupo que o usuario selecionou na aba de pesquisa e preenche a aba de manutenção 
		//com as informações do grupo selecionado
		@Listen("onSelect = #lsbPesqGrupo")
		public void obtemGrupoSelecionado() throws SQLException, InterruptedException
		{
			int indice = this.lsbPesqGrupo.getSelectedIndex();
			int id = 0;
			GrupoDao grupoDao = new GrupoDao();
			Grupo grupo = new Grupo();
			
			if(indice >=0)
			{
				Listcell lc01 = new Listcell();
				
				lc01 = (Listcell)this.lsbPesqGrupo.getSelectedItem().getChildren().get(0);
				id = Integer.parseInt(lc01.getLabel().toString());
				grupo = grupoDao.getGrupoId(id);
				
				if(grupo != null)
				{
					this.intIdGrupo.setText(Integer.toString(grupo.getIdGrupo()));
					this.txtNomeGrupo.setText(grupo.getNomeGrupo());
					
					this.limpaLsbPesquisa();
					this.txtPesqGrupo.setText("");
					this.tabManterGrupo.setSelected(true);
				}
				else Utilidade.mensagem(grupo.getMensagemErro());
			}
		}
		
		//limpa os dados da aba de manutenção
		@Listen("onClick = #btnLimparGrupo")
		public void limpaDados()
		{
			OPCAO = "";
			this.intIdGrupo.setText("");
			this.txtNomeGrupo.setText("");
			this.txtPesqGrupo.setText("");
		}
}
