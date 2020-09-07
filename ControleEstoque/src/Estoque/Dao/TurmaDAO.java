package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Estoque.Modelo.Turma;
import Estoque.Persistencia.Conexao;

public class TurmaDAO extends Conexao{
	
	public Turma montarTurma(ResultSet rs) throws SQLException,InterruptedException {
		Turma turma = new Turma();
		
		turma.setIdTurma(rs.getInt("id_Turma"));
		turma.setIdAluno(rs.getInt("id_aluno"));
		turma.setIdDisciplina(rs.getInt("id_disciplina"));
		turma.setNota(rs.getFloat("nota"));
		turma.setFaltas(rs.getInt("faltas"));
		return turma;
	}
	
	//inclui um usuário no banco de dados
	public boolean incluir(Turma turma) throws SQLException, InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			conn = this.getConnection();
			
			sql.append("INSERT INTO Funcionario  "); 
			sql.append("       (id_Turma,        "); 
			sql.append("        id_aluno,        ");
		    sql.append("        id_disciplina,   ");
		    sql.append("        nota,            "); 
		    sql.append("        faltas)          "); 
			sql.append("VALUES (?, ?, ?, ?, ?)   ");
			
			pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setInt(1, turma.getIdTurma());
			pstmt.setInt(2, turma.getIdAluno());
			pstmt.setInt(3, turma.getIdDisciplina());
			pstmt.setFloat(4, turma.getNota());
			pstmt.setInt(5, turma.getFaltas());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e) {
			return false;
		}
		
	}
		
	//retorna o usuario com o id igual ao passado como parametro
	public Turma getAlunoId(int id) throws SQLException,InterruptedException  {
		Turma turma = new Turma();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("SELECT * FROM Turma WHERE id_Turma=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				turma = montarTurma(rs);
			}
			
			return turma;
		} catch(Exception e) {
			return null;
		}
		
	}
	
	//Retorna uma lista de usuario com todos os usuarios presentes no banco de dados
	public ArrayList<Turma> getTodosUsuarios() throws SQLException,InterruptedException {
		ArrayList<Turma> listaTurma = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Turma turma = null;
		
		try {
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Turma ORDER BY id_Turma");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				turma = this.montarTurma(rs);
				listaTurma.add(turma);
			}
		}
		finally
		{
			if(pstmt != null)
			{
				pstmt.close();
			}
			if(pstmt != null)
			{
				conn.close();
			}
		}
			return listaTurma;
	}
	
	//altera o usuario
	public boolean alterar(Turma turma) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql;
		try
		{
			conn = this.getConnection();
			sql = new StringBuilder();
			
			sql.append("UPDATE Turma");
			sql.append("SET id_aluno=?,");
			sql.append("id_disciplina=?,");
			sql.append("nota=?");
			sql.append("faltas=?");
			sql.append("WHERE id_Turma=?");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, turma.getIdAluno());
			pstmt.setInt(2, turma.getIdDisciplina());
			pstmt.setFloat(3, turma.getNota());
			pstmt.setInt(4, turma.getFaltas());
			pstmt.setInt(5, turma.getIdTurma());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e){
			return false;
			
		}

	}
	
	//exclui o usuário, que possui o id passado como parametro no metodo, do banco de dados
	public boolean excluir(Turma turma) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("DELETE FROM Turma WHERE id_Turma = ?");
				pstmt.setInt(1, turma.getIdTurma());
				
				pstmt.execute();
				
				return true;
			}
			catch(Exception e) {
				return false;
			}
			
		}
	
}
