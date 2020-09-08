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

import Estoque.Dao.CursoDAO;
import Estoque.Modelo.Curso;
import Estoque.Util.Utilidade;

public class CursoControle extends SelectorComposer<Window>{

		@Wire
		Tab tabManterCurso;
		@Wire
		Intbox intIdCurso;
		@Wire
		Textbox txtNomeCurso;
		@Wire
		Button btnIncluirCurso;
		@Wire
		Button btnAlterarCurso;
		@Wire
		Button btnExcluirCurso;
		@Wire
		Button btnLimparCurso;
		@Wire
		Textbox txtPesqCurso;
		@Wire
		Listbox lsbPesqCurso;
		@Wire
		Button btnLimparPesqCurso;
		@Wire
		Button btnAtualizarPesqCurso;
		@Wire
		Window Curso;
			
		public String OPCAO = "";
	
		//ao criar a tela de curso ativa ou desativa(depende da permissão do usuario logado)
		//a opção de clicar nos botoes de incluir, alterar e excluir
		@Listen("onCreate=#Curso")
		public void permissao()
		{
			//obtem se o usuario logodo é aluno(3),professor(1) ou secretaria(2)
			int admin = Integer.parseInt(Sessions.getCurrent().getAttribute("permissao").toString());
			if(admin == 1 || admin == 2)
			{
				btnIncluirCurso.setDisabled(false);
				btnAlterarCurso.setDisabled(false);
				btnExcluirCurso.setDisabled(false);
			}
			else
			{
				btnIncluirCurso.setDisabled(true);
				btnAlterarCurso.setDisabled(true);
				btnExcluirCurso.setDisabled(true);
			}
		}
		
		//limpa lista de pesquisa
		public void limpaLsbPesquisa()
		{
			this.lsbPesqCurso.getItems().clear();
		}
		
		//preenche a lista de pesquisa de acordo com o que esta digitado no textbox de pesquisa
		public void preencheLsbPesquisa()throws SQLException, InterruptedException,IOException
		{
			this.limpaLsbPesquisa();
			CursoDAO CursoDAO = new CursoDAO();
			ArrayList<Curso> listaCurso = new ArrayList<Curso>();
			
			listaCurso = CursoDAO.getListaCursoNome(this.txtPesqCurso.getText().toString());
			
			while(!listaCurso.isEmpty())
			{
				Listitem li = new Listitem();
				Listcell lc01 = new Listcell();
				Listcell lc02 = new Listcell();
				
				lc01.setLabel(Integer.toString(listaCurso.get(0).getIdCurso()));
				lc02.setLabel(listaCurso.remove(0).getNomeCurso());
				
				li.appendChild(lc01);
				li.appendChild(lc02);
				this.lsbPesqCurso.appendChild(li);
			}
		}
		
		//inclui um curso no banco de dados
		@Listen("onClick = #btnIncluirCurso")
		public void onClickbtnIncluir()throws Exception
		{
			OPCAO = "I";
			CursoDAO cursoDao = new CursoDAO();
			Curso curso = new Curso();
			
			if(validaDados())
			{
				curso = this.atualizaDados(curso);
				boolean incluir = cursoDao.incluir(curso);
				if(!incluir) Utilidade.mensagem("Erro! Curso não incluido.");
				else 
				{
					Utilidade.mensagem("Curso incluido com sucesso!");
					this.limpaDados();
				}
				
			}
		}
		
		//altera um curso no banco de dados
		@Listen("onClick = #btnAlterarCurso")
		public void onClickbtnAlterar()throws Exception
		{
			OPCAO = "A";
			Curso curso = new Curso();
			CursoDAO cursoDAO = new CursoDAO();
			
			//verifica se foi selecionado um curso.
			//se o id não for nulo foi selecionado
			if(this.intIdCurso.getValue() == null || this.intIdCurso.getText().isEmpty() || this.intIdCurso.getText().equals(""))
			{
				Utilidade.mensagem("Selecione um curso para alterar.");
			}
			else
			{
				curso = this.atualizaDados(curso);
				boolean alterar = cursoDAO.alterar(curso);
				if(!alterar) Utilidade.mensagem("Erro! Curso não alterado.");
				else 
				{
					Utilidade.mensagem("Curso alterado com sucesso!");
					this.limpaDados();
				}
			}
			this.limpaDados();
		}
		
		//exclui um curso do banco de dados
		@Listen("onClick = #btnExcluirCurso")
		public void onClickbtnExcluir()throws Exception
		{
			OPCAO = "E";
			Curso curso = new Curso();
			CursoDAO cursoDao = new CursoDAO();
			
			if(this.intIdCurso.getValue() == null || this.intIdCurso.getText().isEmpty() || this.intIdCurso.getText().equals(""))
			{
				Utilidade.mensagem("Selecione um curso para excluir.");
			}
			else
			{
				curso = this.atualizaDados(curso);
				boolean excluir = cursoDao.excluir(curso);
				
				if(excluir == false)
				{
					Utilidade.mensagem("Erro! Curso não excluido.");
				}
				else 
				{
					Utilidade.mensagem("Curso excluido com sucesso!");
					this.limpaDados();					
				}
			}
		}
		
		//verifica se os dados digitados são válidos
		public boolean validaDados()throws SQLException, InterruptedException, IOException, ClassNotFoundException 
		{
			int conta = 0;
			String mensagem = "";
			
			if(this.txtNomeCurso.getText().toString().length() == 0)
			{
				conta++;
				mensagem += "Informe o nome do curso \n";
			}
			if(conta > 0)
			{
				Utilidade.mensagem(mensagem);
				return false;
			}
			return true;
		}
		
		//atualiza as informações de um objeto curso para o que está digitado na tela
		public Curso atualizaDados(Curso curso)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
		{
			if(!OPCAO.equals("I")) curso.setIdCurso(this.intIdCurso.getValue());
			
			curso.setNomeCurso(this.txtNomeCurso.getValue());
			this.txtPesqCurso.setText("");
			return curso;
		}
		
		//limpa a lista de pesquisa que fica na aba pesquisa
		@Listen("onClick = #btnLimparPesqCurso")
		public void onClickbtnLimparLista()
		{
			this.limpaLsbPesquisa();
		}
		
		//atualiza a lista de pesquisa
		@Listen("onClick = #btnAtualizarPesqCurso")
		public void onClickbtnAtualizarLista()throws SQLException, InterruptedException, IOException
		{
			this.preencheLsbPesquisa();
		}
		
		//obtem o curso que o usuario selecionou na aba de pesquisa e preenche a aba de manutenção 
		//com as informações do curso selecionado
		@Listen("onSelect = #lsbPesqCurso")
		public void obtemCursoSelecionado() throws SQLException, InterruptedException
		{
			int indice = this.lsbPesqCurso.getSelectedIndex();
			int id = 0;
			CursoDAO cursoDao = new CursoDAO();
			Curso curso = new Curso();
			
			if(indice >=0)
			{
				Listcell lc01 = new Listcell();
				
				lc01 = (Listcell)this.lsbPesqCurso.getSelectedItem().getChildren().get(0);
				id = Integer.parseInt(lc01.getLabel().toString());
				curso = cursoDao.getCursoId(id);
				
				if(curso != null)
				{
					this.intIdCurso.setText(Integer.toString(curso.getIdCurso()));
					this.txtNomeCurso.setText(curso.getNomeCurso());
					
					this.limpaLsbPesquisa();
					this.txtPesqCurso.setText("");
					this.tabManterCurso.setSelected(true);
				}
				else Utilidade.mensagem("Selecione um curso.");
			}
		}
		
		//limpa os dados da aba de manutenção
		@Listen("onClick = #btnLimparCurso")
		public void limpaDados()
		{
			OPCAO = "";
			this.intIdCurso.setText("");
			this.txtNomeCurso.setText("");
			this.txtPesqCurso.setText("");
		}
}
