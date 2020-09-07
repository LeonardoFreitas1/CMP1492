package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Estoque.Modelo.Usuario;
import Estoque.Persistencia.Conexao;

public class UsuarioDao extends Conexao{
	
	//monta e retorna o usuário
	public Usuario montarUsuario(ResultSet rs) throws SQLException,InterruptedException {
		Usuario usuario = new Usuario();
		
		usuario.setIdUsuario(rs.getInt("id_usuario"));
		usuario.setNome(rs.getString("nome_usuario"));
		usuario.setDataNascimento(rs.getString("data_nascimento"));
		usuario.setEmail(rs.getString("email"));
		usuario.setSenha(rs.getString("senha"));
		return usuario;
	}
	
	//inclui um usuário no banco de dados
	public boolean incluir(Usuario usuario) throws SQLException, InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			conn = this.getConnection();
			
			sql.append("INSERT INTO Usuario       "); 
			sql.append("       (id_usuario,       "); 
			sql.append("        nome_usuario,     ");
		    sql.append("        data_nascimento,  ");
		    sql.append("        email,            ");
            sql.append("        senha)            "); 
			sql.append("VALUES (?, ?, ?, ?, ?)    ");
			
			pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setInt(1, usuario.getIdUsuario());
			pstmt.setString(2, usuario.getNome());
			pstmt.setString(3, usuario.getDataNascimento());
			pstmt.setString(4, usuario.getEmail());
			pstmt.setString(5, usuario.getSenha());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e) {
			return false;
		}
		
	}
		
	//retorna o usuario com o id igual ao passado como parametro
	public Usuario getUsuarioId(int id) throws SQLException,InterruptedException 
	{
		Usuario usuario = new Usuario();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Usuario where id_usuario=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				usuario = montarUsuario(rs);
			}
			
			return usuario;
		} catch(Exception e) {
			return null;
		}
		
	}
	
	//Retorna uma lista de usuario com todos os usuarios presentes no banco de dados
	public ArrayList<Usuario> getTodosUsuarios() throws SQLException,InterruptedException
	{
		ArrayList<Usuario> listaUsuario = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Usuario usuario = null;
		
		try
		{
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Usuario ORDER BY Nome");
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				usuario = this.montarUsuario(rs);
				listaUsuario.add(usuario);
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
			return listaUsuario;
	}
	
	//altera o usuario
	public boolean alterar(Usuario usuario) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql;
		try
		{
			conn = this.getConnection();
			sql = new StringBuilder();
			
			sql.append("UPDATE Usuario");
			sql.append("SET nome_usuario=?,");
			sql.append("data_nascimento=?,");
			sql.append("email=?,");
			sql.append("senha=?");
			sql.append("where id_usuario=?");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, usuario.getNome());
			pstmt.setString(2, usuario.getDataNascimento());
			pstmt.setString(3, usuario.getEmail());
			pstmt.setString(4, usuario.getSenha());
			pstmt.setInt(5, usuario.getIdUsuario());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e){
			return false;
			
		}

	}
	
	//exclui o usuário, que possui o id passado como parametro no metodo, do banco de dados
	public boolean excluir(Usuario usuario) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("DELETE FROM Usuario WHERE id_usuario = ?");
			pstmt.setInt(1, usuario.getIdUsuario());
			
			pstmt.execute();
			
			return true;
		}
		catch(Exception e) {
			return false;
		}
		
	}
	
	//verifica se o login e a senha existem no banco de dados e estão associados ao mesmo usuário
	public boolean validaLogin(Usuario usuario) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Usuario WHERE email=? AND senha=?");
			
			pstmt.setString(1, usuario.getEmail());
			pstmt.setString(2, usuario.getSenha());
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return true;
			}
			return false;
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
	}
	
	//retorna um usuario que possua o login IGUAL ao passado como parametro
	public Usuario getUsuarioLogin(String email) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Usuario usu = new Usuario();
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Usuario WHERE email=?");
			
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				usu = this.montarUsuario(rs);
			}
			return usu;
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
	}
	
	//verifica se existe no banco um usuario com o login Igual ao passado como parametro
	public Boolean verificaExisteUsuarioLogin(String email) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Usuario WHERE email=?");
			
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return true;
			}
			return false;
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
	}
}
