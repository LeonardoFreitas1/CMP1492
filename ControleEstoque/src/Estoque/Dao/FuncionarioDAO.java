package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Estoque.Modelo.Funcionario;
import Estoque.Persistencia.Conexao;

public class FuncionarioDAO extends Conexao {
	
	public Funcionario montarFuncionario(ResultSet rs) throws SQLException,InterruptedException {
		Funcionario funcionario = new Funcionario();
		
		funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
		funcionario.setCNH(rs.getString("CNH"));
		funcionario.setCargo(rs.getInt("id_cargo"));
		funcionario.setIdUsuario(rs.getInt("id_usuario"));
		funcionario.setDisciplina(rs.getInt("id_disciplina"));
		return funcionario;
	}
	
	//inclui um usuário no banco de dados
	public boolean incluir(Funcionario funcionario) throws SQLException, InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			conn = this.getConnection();
			
			sql.append("INSERT INTO Funcionario      "); 
			sql.append("       (id_funcionario,      "); 
			sql.append("        CNH,                 ");
		    sql.append("        id_cargo,            ");
		    sql.append("        id_usuario,          "); 
		    sql.append("        id_disciplina)       "); 
			sql.append("VALUES (?, ?, ?, ?, ?)       ");
			
			pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setInt(1, funcionario.getIdFuncionario());
			pstmt.setString(2, funcionario.getCNH());
			pstmt.setInt(3, funcionario.getCargo());
			pstmt.setInt(4, funcionario.getIdUsuario());
			pstmt.setInt(5, funcionario.getDisciplina());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e) {
			return false;
		}
		
	}
		
	//retorna o usuario com o id igual ao passado como parametro
	public Funcionario getAlunoId(int id) throws SQLException,InterruptedException  {
		Funcionario funcionario = new Funcionario();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("SELECT * FROM Funcionario WHERE id_funcionario=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				funcionario = montarFuncionario(rs);
			}
			
			return funcionario;
		} catch(Exception e) {
			return null;
		}
		
	}
	
	//Retorna uma lista de usuario com todos os usuarios presentes no banco de dados
	public ArrayList<Funcionario> getTodosUsuarios() throws SQLException,InterruptedException {
		ArrayList<Funcionario> listaFuncionario = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Funcionario funcionario = null;
		
		try {
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Funcionario ORDER BY id_funcionario");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				funcionario = this.montarFuncionario(rs);
				listaFuncionario.add(funcionario);
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
	public boolean alterar(Funcionario funcionario) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql;
		try
		{
			conn = this.getConnection();
			sql = new StringBuilder();
			
			sql.append("UPDATE Funcionario");
			sql.append("SET CNH=?,");
			sql.append("id_cargo=?,");
			sql.append("id_usuario=?,");
			sql.append("id_disciplina=?");
			sql.append("WHERE id_funcionario");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, funcionario.getCNH());
			pstmt.setInt(2, funcionario.getCargo());
			pstmt.setInt(3, funcionario.getIdUsuario());
			pstmt.setInt(4, funcionario.getDisciplina());
			pstmt.setInt(5, funcionario.getIdFuncionario());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e){
			return false;
			
		}

	}
	
	//exclui o usuário, que possui o id passado como parametro no metodo, do banco de dados
	public boolean excluir(Funcionario funcionario) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("DELETE FROM Funcionario WHERE id_Funcionario = ?");
				pstmt.setInt(1, funcionario.getIdFuncionario());
				
				pstmt.execute();
				
				return true;
			}
			catch(Exception e) {
				return false;
			}
			
		}
}
