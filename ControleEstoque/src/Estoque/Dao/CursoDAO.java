package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Estoque.Modelo.Curso;
import Estoque.Persistencia.Conexao;

public class CursoDAO extends Conexao {

	public Curso montarCurso(ResultSet rs) throws SQLException,InterruptedException {
		Curso curso = new Curso();
		
		curso.setIdCurso(rs.getInt("id_curso"));
		curso.setNomeCurso(rs.getString("nome_curso"));
		return curso;
	}
	
	//inclui um usuário no banco de dados
	public boolean incluir(Curso curso) throws SQLException, InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			conn = this.getConnection();
			
			sql.append("INSERT INTO Curso      "); 
			sql.append("       (id_curso,      "); 
			sql.append("        nome_curso)    ");
			sql.append("VALUES (?, ?)          ");
			
			pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setInt(1, curso.getIdCurso());
			pstmt.setString(2, curso.getNomeCurso());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e) {
			return false;
		}
		
	}
		
	//retorna o usuario com o id igual ao passado como parametro
	public Curso getCursoId(int id) throws SQLException,InterruptedException  {
		Curso curso = new Curso();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("SELECT * FROM Curso WHERE id_curso=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				curso = montarCurso(rs);
			}
			
			return curso;
		} catch(Exception e) {
			return null;
		}
		
	}
	
	//Retorna uma lista de usuario com todos os usuarios presentes no banco de dados
	public ArrayList<Curso> getTodosCursos() throws SQLException,InterruptedException {
		ArrayList<Curso> listaFuncionario = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Curso curso = null;
		
		try {
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Curso ORDER BY id_curso");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				curso = this.montarCurso(rs);
				listaFuncionario.add(curso);
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
			return listaFuncionario;
	}
	
	//altera o usuario
	public boolean alterar(Curso curso) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql;
		try
		{
			conn = this.getConnection();
			sql = new StringBuilder();
			
			sql.append("UPDATE Curso ");
			sql.append("SET nome_curso=?   ");
			sql.append("WHERE id_curso     ");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, curso.getIdCurso());
			pstmt.setString(2, curso.getNomeCurso());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e){
			return false;
			
		}

	}
	
	//exclui o usuário, que possui o id passado como parametro no metodo, do banco de dados
	public boolean excluir(Curso curso) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("DELETE FROM Curso WHERE id_curso = ?");
				pstmt.setInt(1, curso.getIdCurso());
				
				pstmt.execute();
				
				return true;
			}
			catch(Exception e) {
				return false;
			}
			
		}
	
}
