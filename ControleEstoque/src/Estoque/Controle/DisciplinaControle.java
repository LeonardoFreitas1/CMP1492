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

import Estoque.Dao.CursoDAO;
import Estoque.Dao.DisciplinaDAO;
import Estoque.Dao.FuncionarioDAO;
import Estoque.Dao.UsuarioDao;
import Estoque.Modelo.Curso;
import Estoque.Modelo.Disciplina;
import Estoque.Modelo.Funcionario;
import Estoque.Modelo.Usuario;
import Estoque.Util.Utilidade;
	
public class DisciplinaControle extends SelectorComposer<Window>{
	
	@Wire
	Tab tabManterDisciplina;
	@Wire
	Intbox intIdDisciplina;
	@Wire
	Textbox txtNomeDisciplina;
	@Wire
	Combobox combCurso,combProfessor;
	@Wire
	Button btnIncluirDisciplina;
	@Wire
	Button btnAlterarDisciplina;
	@Wire
	Button btnExcluirDisciplina;
	@Wire
	Button btnLimparDisciplina;
	@Wire
	Textbox txtPesqDisciplina;
	@Wire
	Listbox lsbPesqDisciplina;
	@Wire
	Button btnLimparPesqDisciplina;
	@Wire
	Button btnAtualizarPesqDisciplina;
		
	public String OPCAO = "";

	//ao criar a janela de disciplina define as permissões
	//de acordo com a permissao bloqueia ou desbloqueia os botões de incluir, alterar e excluir
	@Listen("onCreate=#Disciplina")
	public void permissao()
	{
		int admin = Integer.parseInt(Sessions.getCurrent().getAttribute("administrador").toString());
		if(admin == 1||admin == 2)
		{
			//desbloqueia
			btnIncluirDisciplina.setDisabled(false);
			btnAlterarDisciplina.setDisabled(false);
			btnExcluirDisciplina.setDisabled(false);
		}
		else
		{
			//bloqueia
			btnIncluirDisciplina.setDisabled(true);
			btnAlterarDisciplina.setDisabled(true);
			btnExcluirDisciplina.setDisabled(true);
		}
	}
	
	@Listen("onClick = #combCurso")
	public void preencheCombCurso()throws Exception
	{
		combCurso.getItems().clear();
		combCurso.setText("");
		
		CursoDAO cursoDao = new CursoDAO();
		ArrayList<Curso> listaCurso = new ArrayList<Curso>();
		
		listaCurso = cursoDao.getTodosCursos();
		
		while(!listaCurso.isEmpty())
		{
			this.combCurso.appendItem(listaCurso.remove(0).getNomeCurso());
		}
	}
	
	@Listen("onClick = #combProfessor")
	public void preencheCombProfessor()throws Exception
	{
		combCurso.getItems().clear();
		combCurso.setText("");

		FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
		UsuarioDao usuarioDao = new UsuarioDao();
		ArrayList<Funcionario> listaFuncionario = new ArrayList<Funcionario>();
		
		listaFuncionario = funcionarioDAO.getTodosUsuarios();
		
		while(!listaFuncionario.isEmpty())
		{
			if(listaFuncionario.get(0).getCargo() == 1)
			{
				this.combCurso.appendItem(usuarioDao.getUsuarioId(listaFuncionario.remove(0).getIdFuncionario()).getNome());
			}
			else listaFuncionario.remove(0);
		}
	}
	
	//limpa a lista de pesquisa
	public void limpaLsbPesquisa()
	{
		this.lsbPesqDisciplina.getItems().clear();
	}

	//preenche a lista de pesquisa
	public void preencheLsbPesquisa()throws SQLException, InterruptedException,IOException
	{
		//limpa a lista
		this.limpaLsbPesquisa();
		DisciplinaDAO DisciplinaDAO = new DisciplinaDAO();
		ArrayList<Disciplina> listaDisciplina = new ArrayList<Disciplina>();
		
		//obtem todas as seções que possuem no nome o que está digitado na caixa de pesquisa
		listaDisciplina = DisciplinaDAO.getListaDisciplinaNome(this.txtPesqDisciplina.getText().toString());
		
		while(!listaDisciplina.isEmpty())
		{
			Listitem li = new Listitem();
			Listcell lc01 = new Listcell();
			Listcell lc02 = new Listcell();
			Listcell lc03 = new Listcell();
			Curso curso = new Curso();
			CursoDAO cursoDao = new CursoDAO();
			curso = cursoDao.getCursoId(listaDisciplina.get(0).getIdCurso());
			lc01.setLabel(curso.getNomeCurso());
			
			//preenche o campo do id
			lc02.setLabel(Integer.toString(listaDisciplina.get(0).getIdDisciplina()));
			
			//preenche o campo do nome
			lc03.setLabel(listaDisciplina.remove(0).getNomeDisciplina());
			
			//insere na linha os campos 
			li.appendChild(lc01);
			
			//insere a linha na lista
			li.appendChild(lc02);
			
			//insere a linha na lista
			li.appendChild(lc02);
			this.lsbPesqDisciplina.appendChild(li);
		}
	}
	
	//ao clicar no botão incluir
	//inclui a disciplina no banco
	@Listen("onClick = #btnIncluirDisciplina")
	public void onClickbtnIncluir()throws Exception
	{
		OPCAO = "I";
		DisciplinaDAO disciplinaDao = new DisciplinaDAO();
		Disciplina disciplina = new Disciplina();
		
		if(validaDados())
		{
			disciplina = this.atualizaDados(disciplina);
			boolean incluiu = disciplinaDao.incluir(disciplina);
			if(!incluiu) Utilidade.mensagem("Erro! Disciplina não foi incluida.");
			else 
			{
				Utilidade.mensagem("Disciplina incluida com sucesso!");
				this.limpaDados();
			}
		}
	}
	
	//ao clicar no botão alterar
	//altera a disciplina no banco
	@Listen("onClick = #btnAlterarDisciplina")
	public void onClickbtnAlterar()throws Exception
	{
		OPCAO = "A";
		Disciplina disciplina = new Disciplina();
		DisciplinaDAO disciplinaDAO = new DisciplinaDAO();

		if(this.intIdDisciplina.getValue() == null || this.intIdDisciplina.getText().isEmpty() || this.intIdDisciplina.getText().equals(""))
		{
			Utilidade.mensagem("Selecione uma disciplina para alterar.");
		}
		else
		{
			disciplina = this.atualizaDados(disciplina);
			boolean alterar = disciplinaDAO.alterar(disciplina);
			if(!alterar) Utilidade.mensagem("Erro! Disciplina não foi alterada.");
			else
			{
				Utilidade.mensagem("Disciplina alterada com sucesso!");				
				this.limpaDados();
			}
		}
	}
	
	//ao clicar no botão excluir
	//exclui a disciplina no banco
	@Listen("onClick = #btnExcluirDisciplina")
	public void onClickbtnExcluir()throws Exception
	{
		OPCAO = "E";
		Disciplina disciplina = new Disciplina();
		DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
		
		if(this.intIdDisciplina.getValue() == null || this.intIdDisciplina.getText().isEmpty() || this.intIdDisciplina.getText().equals(""))
		{
			Utilidade.mensagem("Selecione uma disciplina para excluir.");
		}
		else
		{
			disciplina = this.atualizaDados(disciplina);
			boolean excluir =  disciplinaDAO.excluir(disciplina);
			if(!excluir) Utilidade.mensagem("Erro! Disciplina não foi excluida.");
			else
			{
				Utilidade.mensagem("Disciplina excluida com sucesso!");				
				this.limpaDados();
			}
		}
	}
	
	//verifica se os dados digitados não são nulos
	public boolean validaDados()throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		int conta = 0;
		String mensagem = "";
		
		if(this.txtNomeDisciplina.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "Informe o nome da disciplina \n";
		}
		
		if(this.combCurso.getSelectedItem().toString().length() == 0)
		{
			conta++;
			mensagem += "Informe o Curso da disciplina \n";
		}
		
		if(this.combProfessor.getSelectedItem().toString().length() == 0)
		{
			conta++;
			mensagem += "Informe o Professor da disciplina \n";
		}
		if(conta > 0)
		{
			Utilidade.mensagem(mensagem);
			return false;
		}
		return true;
	}

	//recebe uma disciplina com dados desatualizados
	//retorna a disciplina com os dados atualizados de acordo com o que esta digitado na tela
	public Disciplina atualizaDados(Disciplina disciplina)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		//se não for inclusão, inclui o id da disciplina na tela(intbox do id)
		if(!OPCAO.equals("I")) disciplina.setIdDisciplina(this.intIdDisciplina.getValue());
		
		disciplina.setNomeDisciplina(this.txtNomeDisciplina.getValue());
		
		String nomeCurso = this.combCurso.getSelectedItem().getLabel();
		CursoDAO cursoDao = new CursoDAO();
		disciplina.setIdCurso(cursoDao.getCursoNome(nomeCurso).getIdCurso());
		
		FuncionarioDAO funcionarioDao = new FuncionarioDAO();
		disciplina.setIdFuncionario(funcionarioDao.getFuncionarioNome(this.combProfessor.getSelectedItem().getLabel().toString()).getIdFuncionario());
		this.txtPesqDisciplina.setText("");
		return disciplina;
	}
	
	@Listen("onClick = #btnLimparPesqDisciplina")
	public void onClickbtnLimparLista()
	{
		this.limpaLsbPesquisa();
	}
	
	//ao clicar no botao atualizar pesquisa
	//atualiza a lista de pesquisa filtrando as informações com o que está digitado no textbox pesquisa
	@Listen("onClick = #btnAtualizarPesqDisciplina")
	public void onClickbtnAtualizarLista()throws SQLException, InterruptedException, IOException
	{
		this.preencheLsbPesquisa();
	}
	
	//ao clicar em uma disciplina na lista de pesquisa
	//abre a aba de manutenção e a preenche com os dados da disciplina selecionada
	@Listen("onSelect = #lsbPesqDisciplina")
	public void obtemDisciplinaSelecionada() throws SQLException, InterruptedException
	{
		//obtem a posição na lista da disciplina selecionado
		int indice = this.lsbPesqDisciplina.getSelectedIndex();
		int id = 0;
		DisciplinaDAO disciplinaDao = new DisciplinaDAO();
		Disciplina disciplina = new Disciplina();
		
		if(indice >=0)
		{
			Listcell lc01 = new Listcell();
			
			//obtem o segundo campo da linha(id) da disciplina selecionada na lista de pesquisa 
			lc01 = (Listcell)this.lsbPesqDisciplina.getSelectedItem().getChildren().get(1);
			id = Integer.parseInt(lc01.getLabel().toString());
			
			//obtem a disciplina cadastrada no banco com o id selecionado
			disciplina = disciplinaDao.getDisciplinaId(id);
			
			//preenche a aba de manutenção com os dados da disciplina selecionada
			if(disciplina != null)
			{
				this.intIdDisciplina.setText(Integer.toString(disciplina.getIdDisciplina()));
				this.txtNomeDisciplina.setText(disciplina.getNomeDisciplina());
				CursoDAO cursoDao = new CursoDAO();
				FuncionarioDAO funcionarioDao = new FuncionarioDAO();
				UsuarioDao usuarioDAO = new UsuarioDao();
				
				int i=0;
				while(this.combCurso.getItems().get(i).getLabel() != cursoDao.getCursoId(disciplina.getIdFuncionario()).getNomeCurso())
				{					
					i++;
				}
				this.combCurso.setSelectedIndex(i);
				
				Funcionario funcionario = funcionarioDao.getAlunoId(disciplina.getIdFuncionario());
				Usuario usuario = usuarioDAO.getUsuarioId(funcionario.getIdFuncionario());
				i=0;
				while(this.combProfessor.getItems().get(i).getLabel() != usuario.getNome())
				{					
					i++;
				}
				this.combProfessor.setSelectedIndex(i);
				
				//limpa a aba de pesquisa
				this.limpaLsbPesquisa();
				this.txtPesqDisciplina.setText("");
				this.tabManterDisciplina.setSelected(true);
			}
			else Utilidade.mensagem("Selecione uma Disciplina.");
		}
	}
	
	//ao clicar no botão limpar na aba manutenção 
	//limpa todos os campos da aba manutenção
	@Listen("onClick = #btnLimparDisciplina")
	public void limpaDados()
	{
		OPCAO = "";
		this.intIdDisciplina.setText("");
		this.txtNomeDisciplina.setText("");
		this.txtPesqDisciplina.setText("");
		this.limpaLsbPesquisa();
	}

}
