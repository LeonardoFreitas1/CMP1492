package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Estoque.Modelo.Grupo;
import Estoque.Persistencia.Conexao;
public class GrupoDao extends Conexao{
		
	//Monta e retorna o objeto grupo
	public Grupo montarGrupo(ResultSet rs) throws SQLException,InterruptedException
	{
		Grupo grupo = new Grupo();

		grupo.setIdGrupo(rs.getInt("id_grupo"));
		grupo.setNomeGrupo(rs.getString("nome_grupo"));
		return grupo;
	}
	
	//inclui uma seção no banco de dados
	public Grupo incluir(Grupo grupo) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		try
		{
			
			//verifica se existe grupo com o mesmo nome
			//para não incluir um grupo repetido
			if(verificaExisteGrupoNome(grupo))
			{
				grupo.setMensagemErro("Erro! Já existe um grupo cadastrado com esse nome.");
				return grupo;
			}
			
			conn = this.getConnection();
			sql = "insert into Grupo (nome_grupo) values(?)";
			pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setString(1, grupo.getNomeGrupo());
			
			int auxIncluiu  = pstmt.executeUpdate();
			if(auxIncluiu > 0) grupo.setMensagemErro("Grupo incluido com sucesso!");
			else grupo.setMensagemErro("Erro! Grupo não foi inserido.");
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
		return grupo;
	}
	
	//retorna um grupo que possui o id passado como parametro
	public Grupo getGrupoId(int id) throws SQLException,InterruptedException 
	{
		Grupo grupo = new Grupo();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Grupo where id_grupo=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			if(rs.next())
			{
				grupo = montarGrupo(rs);
			}
			else grupo.setMensagemErro("Erro! Não existe grupo cadastrado com esse id.");
			
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
		return grupo;
	}
	
	//retorna uma lista de grupos que possuem o nome
	// ou uma parte do nome igual o que esta sendo passado como parametro no metodo
	public ArrayList<Grupo> getListaGrupoNome(String nome) throws SQLException,InterruptedException 
	{
		ArrayList<Grupo> listaGrupo = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Grupo grupo = null;
		try
		{
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Grupo where lower(nome_grupo) like ?");
			pstmt.setString(1, "%" + nome.toLowerCase() + "%"); // "%" para informar que pode ter mais caracteres antes e depois da string
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				grupo = this.montarGrupo(rs);
				listaGrupo.add(grupo);
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
		return listaGrupo;
	}
	
	//retorna uma movimentação com o nome IGUAL ao o passado como parametro
	public Grupo getGrupoNome(String nome) throws SQLException,InterruptedException 
	{
		Grupo grupo = new Grupo();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Grupo where nome_grupo like ?");
			pstmt.setString(1, nome);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				grupo = this.montarGrupo(rs);
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
		return grupo;
	}
	
	//Retorna uma lista de grupo com TODOS os grupos presentes no banco de dados
	public ArrayList<Grupo> getTodosGrupos() throws SQLException,InterruptedException
	{
		ArrayList<Grupo> listaGrupo = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Grupo grupo = null;
		
		try
		{
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Grupo order by nome_grupo");
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				grupo = this.montarGrupo(rs);
				listaGrupo.add(grupo);
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
			return listaGrupo;
	}
	
	//Altera o grupo
	public Grupo alterar(Grupo grupo) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try
		{
			conn = this.getConnection();
			
			//verifica se o grupo passado como parametro existe no banco de dados
			if(!this.verificaExisteGrupoId(grupo.getIdGrupo())) 
			{
				grupo.setMensagemErro("Erro! Esse grupo não existe.");
				return grupo;
			}
			
			//verifica se exixste um grupo no banco com o mesmo novo nome do grupo a ser alterado
			if(verificaExisteGrupoNome(grupo))
			{
				grupo.setMensagemErro("Erro! Já existe um grupo cadastrado com esse nome.");
				return grupo;
			}
			//altera o nome do grupo no banco
			pstmt = conn.prepareStatement("update Grupo set nome_grupo = ? where id_grupo = ?");
			pstmt.setString(1, grupo.getNomeGrupo());
			pstmt.setInt(2, grupo.getIdGrupo());
			
			int conta = pstmt.executeUpdate();
			if(conta > 0)grupo.setMensagemErro("Grupo alterado com sucesso!");
			else grupo.setMensagemErro("Erro! Grupo não foi alterado.");
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
		return grupo;
	}
	
	//exclui o grupo, que possui o id igual ao passado como parametro no metodo, do banco de dados
	public Grupo excluir(Grupo grupo) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = this.getConnection();
			if(this.verificaExisteDependente(grupo.getIdGrupo()))
			{
				grupo.setMensagemErro("Erro! Não é possível excluir esse grupo pois existem produtos cadastrados com esse grupo.");
				return grupo;
			}
			pstmt = conn.prepareStatement("delete from grupo where id_grupo = ?");
			pstmt.setInt(1, grupo.getIdGrupo());
			int conta = pstmt.executeUpdate();
			if(conta > 0) grupo.setMensagemErro("Grupo excluido com sucesso!");
			else grupo.setMensagemErro("Erro! Grupo não foi excluido.");
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
		return grupo;
	}
	

	//verifica se o grupo com o id passado como parametro existe
	public boolean verificaExisteGrupoId(int id) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Grupo where id_grupo=?");
			
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
	
	//verifica se existe um grupo no banco de dados cadastrado com o nome passado como parametro 
	public boolean verificaExisteGrupoNome(Grupo grupo) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Grupo where nome_grupo=? and id_grupo != ?");
			
			pstmt.setString(1, grupo.getNomeGrupo());
			pstmt.setInt(2, grupo.getIdGrupo());
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return true;
			}
			return false;
		}
		finally
			{if(pstmt != null)
			{
				pstmt.close();
			}
			if(pstmt != null)
			{
				conn.close();
			}
		}
	}
	
	//verifica se existe um produto que pertence ao grupo passado como parametro 
	public boolean verificaExisteDependente(int idGrupo) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Produto where fk_grupo=?");
			
			pstmt.setInt(1, idGrupo);
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				return true;
			}
			return false;
		}
		finally
			{if(pstmt != null)
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
