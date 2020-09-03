
package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Estoque.Modelo.Secao;
import Estoque.Persistencia.Conexao;
public class SecaoDao extends Conexao{
		
	//monta e retorna a se��o
	public Secao montarSecao(ResultSet rs) throws SQLException,InterruptedException
	{
		Secao secao = new Secao();

		secao.setIdSecao(rs.getInt("id_secao"));
		secao.setNomeSecao(rs.getString("nome_secao"));
		return secao;
	}
	
	//inclui uma se��o no banco de dados
	public Secao incluir(Secao secao) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		try
		{
			
			//verifica se existe uma se��o com o mesmo nome
			if(verificaExisteSecaoNome(secao))
			{
				secao.setMensagemErro("Erro! J� existe um se��o cadastrada com esse nome.");
				return secao;
			}
			conn = this.getConnection();
			sql = "insert into Secao (nome_secao) values(?)";
			pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setString(1, secao.getNomeSecao());
			
			int auxIncluiu  = pstmt.executeUpdate();
			if(auxIncluiu > 0) secao.setMensagemErro("Se��o incluida com sucesso!");
			else secao.setMensagemErro("Erro! Se��o n�o foi inserida.");
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
		return secao;
	}
		
	//retorna uma se��o com o id passado como parametro
	public Secao getSecaoId(int id) throws SQLException,InterruptedException 
	{
		Secao secao = new Secao();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Secao where id_secao=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			if(rs.next())
			{
				secao = montarSecao(rs);
			}
			else secao.setMensagemErro("Erro! N�o existe Se��o cadastrada com esse id.");
			
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
		return secao;
	}
	
	//retorna uma se��o com o nome IGUAL ao o passado como parametro
	public Secao getSecaoNome(String nome) throws SQLException,InterruptedException
	{
		Secao secao = new Secao();
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Secao where nome_secao=?");
			
			pstmt.setString(1, nome);
			
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				secao = montarSecao(rs);
			}
			else secao.setMensagemErro("Erro! N�o existe Se��o cadastrada com esse nome.");
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
		return secao;
	}
	
	//retorna uma lista de se��es que possuem o nome ou contenha em uma parte do nome 
	//igual ao que esta sendo passado como parametro no metodo
	public ArrayList<Secao> getListaSecaoNome(String nome) throws SQLException,InterruptedException 
	{
		ArrayList<Secao> listaSecao = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Secao secao = null;
		try
		{
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Secao where lower(nome_secao) like ?");
			pstmt.setString(1, "%" + nome.toLowerCase() + "%");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				secao = this.montarSecao(rs);
				listaSecao.add(secao);
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
		return listaSecao;
	}

	//Retorna uma lista de se��es com TODAS as se��es presentes no banco de dados
	public ArrayList<Secao> getTodasSecoes() throws SQLException,InterruptedException
	{
		ArrayList<Secao> listaSecao = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Secao secao = null;
		
		try
		{
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Secao order by nome_secao");
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				secao = this.montarSecao(rs);
				listaSecao.add(secao);
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
			return listaSecao;
	}
	
	//altera a se��o
	public Secao alterar(Secao secao) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try
		{
			conn = this.getConnection();

			if(!this.verificaExisteSecaoId(secao.getIdSecao())) 
			{
				secao.setMensagemErro("Erro! Essa Se��o n�o existe.");
				return secao;
			}
			
			if(verificaExisteSecaoNome(secao))
			{
				secao.setMensagemErro("Erro! J� existe um Se��o cadastrada com esse nome.");
				return secao;
			}
			pstmt = conn.prepareStatement("update Secao set nome_secao = ? where id_secao = ?");
			pstmt.setString(1, secao.getNomeSecao());
			pstmt.setInt(2, secao.getIdSecao());
			
			int conta = pstmt.executeUpdate();
			if(conta > 0)secao.setMensagemErro("Se��o alterada com sucesso!");
			else secao.setMensagemErro("Erro! Se��o n�o foi alterada.");
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
		return secao;
	}
	
	//exclui a se��o, que possui o id passado como parametro no metodo, do banco de dados
	public Secao excluir(Secao secao) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = this.getConnection();
			
			//verifica se existe uma movimenta��o cadastrada com a se��o a ser excluida
			if(this.verificaExisteDependente(secao.getIdSecao()))
			{
				secao.setMensagemErro("Erro! N�o � poss�vel excluir a se��o pois existem movimenta��es cadastradas com essa se��o.");
				return secao;
			}
			pstmt = conn.prepareStatement("delete from secao where id_secao = ?");
			pstmt.setInt(1, secao.getIdSecao());
			int conta = pstmt.executeUpdate();
			if(conta > 0) secao.setMensagemErro("Se��o excluida com sucesso!");
			else secao.setMensagemErro("Erro! Se��o n�o foi excluida.");
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
		return secao;
	}
	
	//verifica se existe uma se��o no banco com o id igual ao passado como parametro
	public boolean verificaExisteSecaoId(int id) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Secao where id_secao=?");
			
			pstmt.setInt(1, id);
			
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				return true;
			}
			return false;
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
	}
	
	//verifica se existe uma se��o no banco com o nome igual ao o passado como parametro
	public boolean verificaExisteSecaoNome(Secao secao) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Secao where nome_secao=? and id_secao != ?");
			
			pstmt.setString(1, secao.getNomeSecao());
			pstmt.setInt(2, secao.getIdSecao());
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
	
	//verifica se existe uma movimenta��o dependente da se��o passada como parametro
	public boolean verificaExisteDependente(int idSecao) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Movimentacao where fk_secao=?");
			
			pstmt.setInt(1, idSecao);
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
