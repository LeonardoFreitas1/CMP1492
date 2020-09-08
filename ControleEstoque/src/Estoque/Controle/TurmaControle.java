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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Doublespinner;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import Estoque.Dao.AlunoDAO;
import Estoque.Dao.CargoDAO;
import Estoque.Dao.CursoDAO;
import Estoque.Dao.DisciplinaDAO;
import Estoque.Dao.TurmaDAO;
import Estoque.Dao.UsuarioDao;
import Estoque.Modelo.Aluno;
import Estoque.Modelo.Curso;
import Estoque.Modelo.Disciplina;
import Estoque.Modelo.Turma;
import Estoque.Modelo.Usuario;
import Estoque.Util.Utilidade;
	
public class TurmaControle extends SelectorComposer<Window>{
	
	@Wire
	Chosenbox chosenAluno;
	@Wire
	Doublespinner doubleFalta;
	@Wire
	Decimalbox decNota;
	@Wire 
	Tab tabManterTurma;
	@Wire
	Window Turma;
	@Wire
	Intbox intIdTurma;
	@Wire
	Combobox combCurso,combDisciplina, combProfessor;
	@Wire
	Button btnIncluirTurma;
	@Wire
	Button btnAlterarTurma;
	@Wire
	Button btnExcluirTurma;
	@Wire
	Button btnLimparTurma;
	@Wire
	Textbox txtPesqTurma, txtNomeTurma;
	@Wire
	Listbox lsbPesqTurma;
	@Wire
	Listbox listFaltaNota;
	@Wire
	Button btnLimparPesqTurma;
	@Wire
	Button btnAtualizarPesqTurma;
		
	public String OPCAO = "";

	@Listen("onCreate = #Turma")
	public void preencheComboboxs() throws SQLException, InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException
	{
		//preenche o combobox de curso e disciplina e o chosenbox de alunos
		this.PreencheChosenbox();
		this.preenchecombCursoDisciplina();;
		
		//de acordo com a permissao bloqueia ou desbloqueia os botões de incluir, alterar e excluir
		int admin = Integer.parseInt(Sessions.getCurrent().getAttribute("administrador").toString());
		if(admin == 1)
		{
			btnIncluirTurma.setDisabled(false);
			btnAlterarTurma.setDisabled(false);
			btnExcluirTurma.setDisabled(false);
		}
		else
		{
			btnIncluirTurma.setDisabled(true);
			btnAlterarTurma.setDisabled(true);
			btnExcluirTurma.setDisabled(true);
		}
	}
	
	public void limpaLsbPesquisa()
	{
		//limpa a lista de pesquisa de movimentações que fica na aba de pesquisa
		this.lsbPesqTurma.getItems().clear();
	}
	
	public void preencheLsbPesquisa(ArrayList<Turma> listaTurma)throws SQLException, InterruptedException,IOException
	{
		//preenche a lista de pesquisa de movimentações com as informaçoes da lista passada como parâmetro
		this.limpaLsbPesquisa();
		
		DisciplinaDAO  disciplinaDao = new DisciplinaDAO();
		Disciplina disciplina = new Disciplina();
		CursoDAO cursoDao = new CursoDAO();
		
		while(!listaTurma.isEmpty())
		{
			Listitem li = new Listitem();
			Listcell lc01 = new Listcell();
			Listcell lc02 = new Listcell();
			Listcell lc03 = new Listcell();
			
			lc01.setLabel(Integer.toString(listaTurma.get(0).getIdTurma()));
			disciplina = disciplinaDao.getDisciplinaId(listaTurma.remove(0).getIdDisciplina());
			lc02.setLabel(disciplina.getNomeDisciplina());
			lc03.setLabel(cursoDao.getCursoId(disciplina.getIdCurso()).getNomeCurso());
			
		
			li.appendChild(lc01);
			li.appendChild(lc02);
			li.appendChild(lc03);
			this.lsbPesqTurma.appendChild(li);
		}
	}

	@Listen("onClick = #btnIncluirTurma")
	public void onClickbtnIncluir()throws Exception
	{
		//ao clicar no botão incluir que fica na aba de manutenção
		//verifica se os dados selecionados na tela são validos
		//atualiza os dados da turma de acordo com o que esta digitado/selecionado na tela
		//inclui a turma no banco de dados
		//mostra para o usuario uma mensagem avisando se a turma foi incluida ou não
		//limpa os dados da tela de manutenção e pesquisa
		OPCAO = "I";
		if(this.validaDados())
		{
			TurmaDAO turmaDAO = new TurmaDAO();
			ArrayList<Turma> listaTurma = new ArrayList<Turma>();
			listaTurma = this.atualizaDados(listaTurma);
			boolean incluiu;
			while(!listaTurma.isEmpty())
			{
				incluiu = turmaDAO.incluir(listaTurma.remove(0));
				
			}
			if(!incluiu) Utilidade.mensagem("Erro! Turma não foi adicionada.");
			else
			{
				Utilidade.mensagem("Turma adicionada com sucesso!.");
				this.limpaDados();
			}			
		}
	}
	
	@Listen("onClick = #btnAlterarTurma")
	public void onClickbtnAlterar()throws Exception
	{
		//ao clicar no botão alterar que fica na aba de manutenção
		//verifica se os dados selecionados na tela são validos
		//atualiza os dados da turma de acordo com o que esta digitado/selecionado na tela
		//altera a turma no banco de dados
		//mostra para o usuario uma mensagem avisando se a turma foi alterada ou não
		//limpa os dados da tela de manutenção e pesquisa
		OPCAO = "A";
		Turma turma = new Turma();
		TurmaDAO turmaDAO = new TurmaDAO();
		if(this.validaDados())
		{
	
			if(this.intIdTurma.getValue() == null || this.intIdTurma.getText().isEmpty() || this.intIdTurma.getText().equals(""))
			{
				Utilidade.mensagem("Selecione um turma para alterar.");
			}
			else
			{
				ArrayList<Turma> listaTurma = new ArrayList<Turma>();
				listaTurma = this.atualizaDados(listaTurma);
				boolean alterada;
				while(!listaTurma.isEmpty())
				{
					alterada = turmaDAO.alterar(listaTurma.remove(0));
					
				}
				if(!alterada) Utilidade.mensagem("Erro! Turma não foi alterada.");
				else
				{
					Utilidade.mensagem("Turma alterada com sucesso!.");
					this.limpaDados();
				}	
			}
		}
	}
	
	@Listen("onClick = #btnExcluirTurma")
	public void onClickbtnExcluir()throws Exception
	{
		//ao clicar no botão excluir que fica na aba de manutenção
		//atualiza os dados da turma de acordo com o que esta digitado/selecionado na tela
		//exclui a turma no banco de dados
		//mostra para o usuario uma mensagem avisando se a turma foi excluida ou não
		//limpa os dados da tela de manutenção e pesquisa		
		OPCAO = "E";
		Turma turma = new Turma();
		TurmaDAO turmaDAO = new TurmaDAO();
		
		if(this.intIdTurma.getValue() == null || this.intIdTurma.getText().isEmpty() || this.intIdTurma.getText().equals(""))
		{
			Utilidade.mensagem("Selecione um turma para excluir.");
		}
		else
		{		
			ArrayList<Turma> listaTurma = new ArrayList<Turma>();
			listaTurma = this.atualizaDados(listaTurma);
			boolean excluida;
			while(!listaTurma.isEmpty())
			{
				excluida = turmaDAO.excluir(listaTurma.remove(0));
				
			}
			if(!excluida) Utilidade.mensagem("Erro! Turma não foi excluida.");
			else
			{
				Utilidade.mensagem("Turma excluida com sucesso!.");
				this.limpaDados();
			}	
		}
	}
	
	public boolean validaDados()throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		//verifica se os dados foram selecionados
		//se não foram mostra uma mensagem para o usuario selecionar
		int conta = 0;
		String mensagem = "";
		
		if(this.combCurso.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "selecione um curso \n";
		}
		if(this.combDisciplina.getText().toString().length() == 0)
		{
			conta++;
			mensagem += "Selecione uma disciplina \n";
		}
		if(this.chosenAluno.getSelectedObjects().isEmpty())
		{
			conta++;
			mensagem += "Selecione um aluno \n";
		}
		
		if(conta > 0)
		{
			Utilidade.mensagem(mensagem);
			return false;
		}
		return true;
	}
	
	public ArrayList<Turma> atualizaDados(Turma turma)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		//recebe a turma com os dados antigos e a ataualiza
	
		//id
		if(!OPCAO.equals("I")) turma.setIdTurma(this.intIdTurma.getValue());
		
		//disciplina
		DisciplinaDAO disciplinaDao = new DisciplinaDAO();
		Disciplina disciplina = disciplinaDao.getDisciplinaNome(this.combDisciplina.getText().toString());
		turma.setIdDisciplina(disciplina.getIdDisciplina());

		ArrayList<Turma> listaTurmas = new ArrayList<Turma>();
		
		//alunos
		ListModelList<String> listaNomeAluno = new ListModelList<String>();
		listaNomeAluno.addAll(this.chosenAluno.getSelectedObjects());
		
		ArrayList<Aluno> listaAluno = new ArrayList<Aluno>();
		AlunoDAO alunoDao = new AlunoDAO();
		while(!listaNomeAluno.isEmpty())
		{
			turma.setIdAluno(alunoDao.getAlunoNome(listaNomeAluno.remove(0)));
			listaTurmas.add(turma);
		}
		this.atualizaDadosNotaFalta(turma);
		this.txtPesqTurma.setText("");
		
		return listaTurmas;
	}
	
	@Listen("onClick = #btnLimparPesqTurma")
	public void onClickbtnLimparLista()
	{
		//ao clicar no boão limpar que fica na aba pesquisa
		//limpa a lista de pesquisa de movimentações que também fica na aba pesquisa.
		this.limpaLsbPesquisa();
	}
	

	//ao clicar no botão atualizar que fica na aba pesquisa
	//preenche a lista de pesquisa de movimentações que fica na aba pesquisa
	@Listen("onClick = #btnAtualizarPesqTurma")
	public void onClickbtnAtualizarLista()throws SQLException, InterruptedException, IOException
	{
		int nome = Integer.parseInt(this.txtPesqTurma.getText().toString());

		TurmaDAO TurmaDAO = new TurmaDAO();
		ArrayList<Turma> listaTurma = new ArrayList<Turma>();
		
		if(nome > 0)
		{
			listaTurma.add(TurmaDAO.getAlunoId(nome));
		}
		else listaTurma = TurmaDAO.getTodosUsuarios();
			
		this.preencheLsbPesquisa(listaTurma);
	}
	
	@Listen("onSelect = #lsbPesqTurma")
	public void obtemTurmaSelecionada() throws SQLException, InterruptedException, IOException
	{
		//obtem a turma selecionada na aba pesquisa
		
			
		Listcell lc01 = new Listcell();
		lc01 = (Listcell)this.lsbPesqTurma.getSelectedItem().getChildren().get(0);
		
		int idTurma = Integer.parseInt(lc01.getLabel().toString());

		TurmaDAO turmaDAO = new TurmaDAO();
		DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
		CursoDAO cursoDAO = new CursoDAO();
		Turma turma = turmaDAO.getAlunoId(idTurma);
		Disciplina disciplina = disciplinaDAO.getDisciplinaId(turma.getIdDisciplina());
		Curso curso = cursoDAO.getCursoId(disciplina.getIdCurso());
		
		if(idTurma > 0)
		{
		
			this.intIdTurma.setText(Integer.toString(turma.getIdTurma()));
			int i=0;
			while(this.combCurso.getItems().get(i).getLabel() != curso.getNomeCurso())
			{					
				i++;
			}
			this.combCurso.setSelectedIndex(i);
			i=0;
			while(this.combDisciplina.getItems().get(i).getLabel() != disciplina.getNomeDisciplina())
			{					
				i++;
			}
			this.combDisciplina.setSelectedIndex(i);
			
			UsuarioDao usuarioDao = new UsuarioDao();
			
			ArrayList<Aluno> listaAluno = turma.getListaAluno();
			ArrayList<Usuario> listaUsuarioAluno = new ArrayList<Usuario>();
			ListModelList<String> listaNomeAluno = new ListModelList<String>();
			
			while(!listaAluno.isEmpty())
			{
				listaUsuarioAluno.add(usuarioDao.getUsuarioId(listaAluno.remove(0).getIdUsuario()));
			}
			while(!listaUsuarioAluno.isEmpty())
			{
				listaNomeAluno.add(listaUsuarioAluno.remove(0).getNome());
			}
			this.chosenAluno.setSelectedObjects(listaNomeAluno);
			
			
			this.limpaLsbPesquisa();
			this.txtPesqTurma.setText("");
			this.obtemTurmaSelecionadaNotaFalta();
			this.tabManterTurma.setSelected(true);
			
		}
			else Utilidade.mensagem("Selecione uma Turma.");
		
	}
	
	public void preenchecombCursoDisciplina()throws SQLException, InterruptedException,IOException
	{
		//preenche o combobox de curso e de disciplina com todos cadastrados no sistema
		
		this.combCurso.getItems().clear();
		this.combDisciplina.getItems().clear();
		CursoDAO cursoDao = new CursoDAO();
		DisciplinaDAO disciplinaDao = new DisciplinaDAO();
		ArrayList<Curso> listaCurso = cursoDao.getTodosCursos();
		ArrayList< Disciplina> listaDisciplina = disciplinaDao.getTodasDisciplinas();
				
		while(!listaCurso.isEmpty())
		{
			this.combCurso.appendItem(listaCurso.remove(0).getNomeCurso());
		}
		while(!listaDisciplina.isEmpty())
		{
			this.combDisciplina.appendItem(listaDisciplina.remove(0).getNomeDisciplina());
		}
	}
	
	public void PreencheChosenbox() throws SQLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, InterruptedException
	{
		//preenche o chosenbox de alunos com todos os alunos cadastrados no sistema
		AlunoDAO alunoDao = new AlunoDAO();
		ArrayList<Aluno> listaAluno = alunoDao.getTodosUsuarios();
		UsuarioDao usuarioDao = new UsuarioDao();
		ArrayList<Usuario> listaUsuarioAluno = new ArrayList<Usuario>();
		ListModelList<String> listaNomeAlunos = new ListModelList<String>();
		
		while(!listaAluno.isEmpty())
		{
			listaUsuarioAluno.add(usuarioDao.getUsuarioId(listaAluno.remove(0).getIdUsuario()));
		}
		while(!listaUsuarioAluno.isEmpty())
		{
			listaNomeAlunos.add(listaUsuarioAluno.remove(0).getNome());
		}
		this.chosenAluno.setModel(listaNomeAlunos);
	}
	
	@Listen("onClick = #btnLimparTurma")
	public void limpaDados() throws SQLException, InterruptedException, IOException
	{
		//limpa todos os campos da aba de manutenção e pesquisa
		OPCAO = "";
		this.intIdTurma.setText("");
		this.combCurso.setText("");
		this.combDisciplina.setText("");
		this.chosenAluno.clearSelection();
		this.txtPesqTurma.setText("");
		this.limpaLsbPesquisa();
	}
	
	@Listen("onSelect = #lsbPesqTurma")
	public void obtemTurmaSelecionadaNotaFalta() throws SQLException, InterruptedException, IOException
	{
		//obtem a turma selecionada na aba pesquisa
		//preenche a lista de alunos faltas e notas
			
		Listcell lc01 = new Listcell();
		lc01 = (Listcell)this.lsbPesqTurma.getSelectedItem().getChildren().get(0);
		
		int idTurma = Integer.parseInt(lc01.getLabel().toString());

		TurmaDAO turmaDao = new TurmaDAO();
		DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
		CursoDAO cursoDAO = new CursoDAO();
		Turma turma = turmaDao.getAlunoId(idTurma);
		Disciplina disciplina = disciplinaDAO.getDisciplinaId(turma.getIdDisciplina());
		Curso curso = cursoDAO.getCursoId(disciplina.getIdCurso());
		ArrayList<Turma> listaAlunoTurma = turmaDao.getAlunoId(turma.getIdTurma());//lista de alunos da turma
		if(idTurma > 0)
		{
			while(!listaAlunoTurma.isEmpty())
			{
				Listitem li = new Listitem();
				Listcell lc02 = new Listcell();
				Listcell lc03 = new Listcell();
				Listcell lc04 = new Listcell();
				
				UsuarioDao usuarioDao = new UsuarioDao();
				
				lc01.setLabel(Integer.toString(listaAlunoTurma.get(0).getIdAluno()));
				lc02.setLabel(usuarioDao.getUsuarioId(listaAlunoTurma.get(0).getIdAluno()).getNome());
				lc03.setValue(listaAlunoTurma.get(0).getFaltas());
				lc04.setValue(listaAlunoTurma.remove(0).getNota());
			
				li.appendChild(lc01);
				li.appendChild(lc02);
				li.appendChild(lc03);
				li.appendChild(lc04);
				this.listFaltaNota.appendChild(li);
			}
			
			this.limpaLsbPesquisa();
			this.txtPesqTurma.setText("");
		}
			else Utilidade.mensagem("Selecione uma Turma.");
		
	}
	
	public ArrayList<Turma> atualizaDadosNotaFalta(Turma turma)throws SQLException, InterruptedException, IOException, ClassNotFoundException 
	{
		//recebe a turma com os dados antigos e a ataualiza
	
		//id
		if(!OPCAO.equals("I")) turma.setIdTurma(this.intIdTurma.getValue());
		
		//disciplina
		DisciplinaDAO disciplinaDao = new DisciplinaDAO();
		Disciplina disciplina = disciplinaDao.getDisciplinaNome(this.combDisciplina.getText().toString());
		turma.setIdDisciplina(disciplina.getIdDisciplina());

		ArrayList<Turma> listaTurmas = new ArrayList<Turma>();
		
		//alunos
		ListModelList<String> listaNomeAluno = new ListModelList<String>();
		listaNomeAluno.addAll(this.chosenAluno.getSelectedObjects());
		
		ArrayList<Aluno> listaAluno = new ArrayList<Aluno>();
		AlunoDAO alunoDao = new AlunoDAO();
		while(!listaNomeAluno.isEmpty())
		{
			Aluno aluno = alunoDao.getAlunoNome(listaNomeAluno.remove(0);
			turma.setFaltas(Integer.parseInt(this.doubleFalta.getValue().toString()));
			turma.setNota(Float.parseFloat(this.decNota.getValue().toString()));
			turma.setIdAluno(aluno.getIdAluno());
			listaTurmas.add(turma);
		}
		this.txtPesqTurma.setText("");
		
		return listaTurmas;
	}
	
}
