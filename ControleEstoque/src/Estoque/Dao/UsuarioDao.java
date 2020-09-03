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
	public Usuario montarUsuario(ResultSet rs) throws SQLException,InterruptedException
	{
		Usuario usuario = new Usuario();

		usuario.setIdUsuario(rs.getInt("id_usuario"));
		usuario.setLogin(rs.getString("login"));
		usuario.setSenha(rs.getString("senha"));
		usuario.setAdministrador(rs.getBoolean("administrador"));
		return usuario;
	}
	
	//inclui um usuário no banco de dados
	public Usuario incluir(Usuario usuario) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		try
		{
			//verifica se existe usuario com o mesmo login
			if(this.verificaExisteUsuarioLogin(usuario.getLogin()))
			{
				usuario.setMensagemErro("Erro! Já existe um usuario cadastrado com esse Login.");
				return usuario;
			}

			conn = this.getConnection();
			sql = "insert into Usuario (login,senha,administrador) values(? , ?, ?)";
			pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setString(1, usuario.getLogin());
			pstmt.setString(2, usuario.getSenha());
			pstmt.setBoolean(3, usuario.isAdministrador());
			
			int auxIncluiu  = pstmt.executeUpdate();
			if(auxIncluiu > 0) usuario.setMensagemErro("Usuario incluido com sucesso!");
			else usuario.setMensagemErro("Erro! Usuario não foi inserido.");
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
		return usuario;
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
			else usuario.setMensagemErro("Erro! Não existe usuario cadastrado com esse id.");
			
		}
		finally {
			if(pstmt != null)
			{
				pstmt.close();
			}
			if(pstmt != null)
			{
				conn.close();
			}
		}
		return usuario;
	}
	
	//retorna uma lista de usuarios que possuem um login ou contenha em uma parte do login
	// o que esta sendo passado como parametro no metodo
	public ArrayList<Usuario> getListaLoginNome(String nome) throws SQLException,InterruptedException 
	{
		ArrayList<Usuario> listaUsuario = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Usuario usuario = null;
		try
		{
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Usuario where lower(login) like ?");
			pstmt.setString(1, "%" + nome.toLowerCase() + "%");
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
			pstmt = conn.prepareStatement("select * from Usuario order by login");
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
	public Usuario alterar(Usuario usuario) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try
		{
			conn = this.getConnection();

			if(this.getUsuarioId(usuario.getIdUsuario()) == null) 
			{
				usuario.setMensagemErro("Erro! Esse usuario não existe.");
				return usuario;
			}
			//verifica se existe outro usuario com o mesmo login
			if(this.verificaExisteUsuarioLogin(usuario.getLogin()))
			{
				int idUsuarioMesmoLogin = this.getUsuarioLogin(usuario.getLogin()).getIdUsuario();
				//verifica se o usuario com o mesmo login é o que esta sendo alterado
				if (idUsuarioMesmoLogin != usuario.getIdUsuario())
				{
					usuario.setMensagemErro("Erro! Já existe um usuario cadastrado com esse Login.");
					return usuario;
				}
			}
			pstmt = conn.prepareStatement("update Usuario set login=? ,senha=?, administrador=?  where id_usuario=?");
			pstmt.setString(1, usuario.getLogin());
			pstmt.setString(2, usuario.getSenha());
			pstmt.setBoolean(3, usuario.isAdministrador());
			pstmt.setInt(4, usuario.getIdUsuario());
			
			int conta = pstmt.executeUpdate();
			if(conta > 0)usuario.setMensagemErro("Usuario alterado com sucesso!");
			else usuario.setMensagemErro("Erro! Usuario não foi alterado.");
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
		return usuario;
	}
	
	//exclui o usuário, que possui o id passado como parametro no metodo, do banco de dados
	public Usuario excluir(Usuario usuario) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			
			if(this.getUsuarioId(usuario.getIdUsuario()) == null)
			{
				usuario.setMensagemErro("Erro! Esse usuario não existe.");
				return usuario;
			}
			
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("delete from usuario where id_usuario = ?");
			pstmt.setInt(1, usuario.getIdUsuario());
			int conta = pstmt.executeUpdate();
			if(conta > 0)
			{
				usuario.setMensagemErro("Usuario excluido com sucesso!");
			}
			
			else usuario.setMensagemErro("Erro! Usuario não foi excluido.");
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
		return usuario;
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
			pstmt = conn.prepareStatement("select * from Usuario where login=? and senha=?");
			
			pstmt.setString(1, usuario.getLogin());
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
	public Usuario getUsuarioLogin(String login) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Usuario usu = new Usuario();
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Usuario where login=?");
			
			pstmt.setString(1, login);
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
	public Boolean verificaExisteUsuarioLogin(String login) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Usuario where login=?");
			
			pstmt.setString(1, login);
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
