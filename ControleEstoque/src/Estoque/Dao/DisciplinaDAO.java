package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Estoque.Modelo.Disciplina;
import Estoque.Persistencia.Conexao;

public class DisciplinaDAO extends Conexao{
	
	public Disciplina montarDisciplina(ResultSet rs) throws SQLException,InterruptedException {
		Disciplina disciplina = new Disciplina();
		
		disciplina.setIdDisciplina(rs.getInt("id_disciplina"));
		disciplina.setNomeDisciplina(rs.getString("nome_disciplina"));
		disciplina.setIdCurso(rs.getInt("id_curso"));
		disciplina.setIdFuncionario(rs.getInt("id_professor"));
		return disciplina;
	}
	
	//inclui um usuário no banco de dados
	public boolean incluir(Disciplina disciplina) throws SQLException, InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			conn = this.getConnection();
			
			sql.append("INSERT INTO Disciplinas      "); 
			sql.append("       (id_disciplina,       "); 
			sql.append("        nome_disciplina,     ");
		    sql.append("        id_curso,            ");
		    sql.append("        id_professor)        "); 
			sql.append("VALUES (?, ?, ?, ?)          ");
			
			pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setInt(1, disciplina.getIdDisciplina());
			pstmt.setString(2, disciplina.getNomeDisciplina());
			pstmt.setInt(3, disciplina.getIdCurso());
			pstmt.setInt(4, disciplina.getIdFuncionario());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e) {
			return false;
		}
		
	}
		
	//retorna o usuario com o id igual ao passado como parametro
	public Disciplina getDisciplinaId(int id) throws SQLException,InterruptedException  {
		Disciplina disciplina = new Disciplina();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("SELECT * FROM Disciplina WHERE id_disciplina=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				disciplina = montarDisciplina(rs);
			}
			
			return disciplina;
		} catch(Exception e) {
			return null;
		}
		
	}
	
	//Retorna uma lista de usuario com todos os usuarios presentes no banco de dados
	public ArrayList<Disciplina> getTodasDisciplinas() throws SQLException,InterruptedException {
		ArrayList<Disciplina> listaDisciplina = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Disciplina disciplina = null;
		
		try {
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Disciplina ORDER BY id_disciplina");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				disciplina = this.montarDisciplina(rs);
				listaDisciplina.add(disciplina);
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
			return listaDisciplina;
	}
	
	//altera o usuario
	public boolean alterar(Disciplina disciplina) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql;
		try
		{
			conn = this.getConnection();
			sql = new StringBuilder();
			
			sql.append("UPDATE Disciplina");
			sql.append("SET nome_disciplina=?,");
			sql.append("id_curso=?,");
			sql.append("id_professor=?,");
			sql.append("WHERE id_disciplina=?");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, disciplina.getNomeDisciplina());
			pstmt.setInt(2, disciplina.getIdCurso());
			pstmt.setInt(3, disciplina.getIdFuncionario());
			pstmt.setInt(4, disciplina.getIdDisciplina());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e){
			return false;
			
		}

	}
	
	//exclui o usuário, que possui o id passado como parametro no metodo, do banco de dados
	public boolean excluir(Disciplina disciplina) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("DELETE FROM Disciplina WHERE id_Disciplina = ?");
				pstmt.setInt(1, disciplina.getIdDisciplina());
				
				pstmt.execute();
				
				return true;
			}
			catch(Exception e) {
				return false;
			}
			
		}
	
}
