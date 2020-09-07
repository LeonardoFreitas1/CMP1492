package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Estoque.Modelo.Aluno;
import Estoque.Persistencia.Conexao;

public class AlunoDAO extends Conexao{

	public Aluno montarAluno(ResultSet rs) throws SQLException,InterruptedException {
		Aluno aluno = new Aluno();
		
		aluno.setIdAluno(rs.getInt("id_aluno"));
		aluno.setIdUsuario(rs.getInt("id_usuario"));
		aluno.setDataMatricula(rs.getString("data_matricula"));
		aluno.setMatricula(rs.getString("matricula"));
		return aluno;
	}
	
	//inclui um usuário no banco de dados
	public boolean incluir(Aluno aluno) throws SQLException, InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			conn = this.getConnection();
			
			sql.append("INSERT INTO Aluno         "); 
			sql.append("       (id_aluno,         "); 
			sql.append("        data_matricula,   ");
		    sql.append("        matricula,        ");
		    sql.append("        id_usuario)       "); 
			sql.append("VALUES (?, ?, ?, ?)       ");
			
			pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setInt(1, aluno.getIdUsuario());
			pstmt.setString(2, aluno.getDataMatricula());
			pstmt.setString(3, aluno.getMatricula());
			pstmt.setInt(4, aluno.getIdUsuario());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e) {
			return false;
		}
		
	}
		
	//retorna o usuario com o id igual ao passado como parametro
	public Aluno getAlunoId(int id) throws SQLException,InterruptedException 
	{
		Aluno aluno = new Aluno();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Aluno where id_Aluno=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				aluno = montarAluno(rs);
			}
			
			return aluno;
		} catch(Exception e) {
			return null;
		}
		
	}
	
	//Retorna uma lista de usuario com todos os usuarios presentes no banco de dados
	public ArrayList<Aluno> getTodosUsuarios() throws SQLException,InterruptedException
	{
		ArrayList<Aluno> listaAluno = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Aluno aluno = null;
		
		try {
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Aluno ORDER BY matricula");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				aluno = this.montarAluno(rs);
				listaAluno.add(aluno);
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
			return listaAluno;
	}
	
	//altera o usuario
	public boolean alterar(Aluno aluno) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql;
		try
		{
			conn = this.getConnection();
			sql = new StringBuilder();
			
			sql.append("UPDATE Aluno");
			sql.append("SET data_matricula=?,");
			sql.append("matricula=?,");
			sql.append("WHERE id_aluno=?");
			sql.append("AND id_usuario=?");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, aluno.getDataMatricula());
			pstmt.setString(2, aluno.getMatricula());
			pstmt.setInt(3, aluno.getIdAluno());
			pstmt.setInt(4, aluno.getIdUsuario());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e){
			return false;
			
		}

	}
	
	//exclui o usuário, que possui o id passado como parametro no metodo, do banco de dados
	public boolean excluir(Aluno aluno) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("DELETE FROM Aluno WHERE id_Aluno = ?");
				pstmt.setInt(1, aluno.getIdAluno());
				
				pstmt.execute();
				
				return true;
			}
			catch(Exception e) {
				return false;
			}
			
		}
}
