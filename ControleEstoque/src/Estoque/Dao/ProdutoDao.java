package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import Estoque.Modelo.Grupo;
import Estoque.Modelo.Movimentacao;
import Estoque.Modelo.Produto;
import Estoque.Modelo.Secao;
import Estoque.Persistencia.Conexao;
public class ProdutoDao extends Conexao{
		
	//monta e retorna o produto
	public Produto montarProduto(ResultSet rs) throws SQLException,InterruptedException
	{
		Produto produto = new Produto();
		GrupoDao gDao = new GrupoDao();

		produto.setIdProduto(rs.getInt("id_produto"));
		produto.setNomeProduto(rs.getString("nome_produto"));
		produto.setGrupo(gDao.getGrupoId(rs.getInt("fk_grupo")));
		return produto;
	}
	
	//inclui um produto no banco de dados
	public Produto incluir(Produto produto) throws SQLException,InterruptedException, InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		try
		{
			
			//verifica se existe produto com o mesmo nome
			if(verificaExisteProdutoNome(produto))
			{
				produto.setMensagemErro("Erro! Já existe um produto cadastrado com esse nome.");
				return produto;
			}
			conn = this.getConnection();
			sql = "insert into Produto (nome_produto, fk_grupo) values(?,?)";
			pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);

			pstmt.setString(1, produto.getNomeProduto());
			pstmt.setInt(2, produto.getGrupo().getIdGrupo());
			
			int auxIncluiu  = pstmt.executeUpdate();
			if(auxIncluiu > 0) produto.setMensagemErro("Produto incluido com sucesso!");
			else produto.setMensagemErro("Erro! Produto não foi inserido.");
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
		return produto;
	}
	
	//retorna um produto com o id passado como parametro
	public Produto getProdutoId(int id) throws SQLException,InterruptedException , InterruptedException
	{		
		Produto produto = new Produto();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Produto where id_produto=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			if(rs.next())
			{
				produto = montarProduto(rs);
			}
			else produto.setMensagemErro("Erro! Não existe produto cadastrado com esse id.");
			
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
		return produto;
	}
	
	//retorna um produto com o nome IGUAL ao o passado como parametro
	public Produto getProdutoNome(String nome) throws SQLException,InterruptedException, InterruptedException
	{
		Produto produto = new Produto();
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Produto where nome_produto=?");
			
			pstmt.setString(1, nome);
			
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				produto = montarProduto(rs);
			}
			else produto.setMensagemErro("Erro! Não existe Produto cadastrado com esse nome.");
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
		return produto;
	}
	
	//retorna uma lista de produtos que possuem o nome ou o nome do grupo a qual pertecem 
	// ou uma parte do nome igual ao que esta sendo passado como parametro no metodo
	public ArrayList<Produto> getListaProdutoNomeGrupo(String nome) throws SQLException,InterruptedException , InterruptedException
	{
		ArrayList<Produto> listaProduto = new ArrayList<Produto>(); //cria a lista de produto	
		ArrayList<Grupo> listaGrupo = new ArrayList<Grupo>(); //cria a lista de grupo	
		Set<Integer> listaidProduto = new LinkedHashSet<Integer>();//não aceita valores repetidos
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			GrupoDao grupoDao = new GrupoDao();
			listaGrupo = grupoDao.getListaGrupoNome(nome); //preenche a lista de grupo com os grupos desejados
			conn = this.getConnection();
			
			while(!listaGrupo.isEmpty())
			{
				//Seleciona todos os ids dos produtos que o grupo tem o mesmo nome ou uma parte do nome digitado
				pstmt = conn.prepareStatement("select id_produto from Produto where fk_grupo = ?");
				pstmt.setInt(1, listaGrupo.remove(0).getIdGrupo());
				rs = pstmt.executeQuery();
				while(rs.next())
				{
					//adiciona o id do produto desejada na lista de id de produtos
					listaidProduto.add(rs.getInt("id_produto"));
				}
			}
			
			//seleciona os ids dos produtos que o nome é igual ou contem uma parte igual nome passado como parametro
			pstmt = conn.prepareStatement("select * from Produto where lower(nome_produto) like ?");
			pstmt.setString(1, "%" + nome.toLowerCase() + "%");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				listaidProduto.add(rs.getInt("id_produto"));
			}
			ArrayList<Integer> listaId = new ArrayList<Integer>();
			listaId.addAll(listaidProduto);
			
			//adiciona na lista de produtos todos os produtos que tem o nome e/ou 
			//pertence a um grupo com o mesmo nome ou uma parte do nome digitado
			while(!listaId.isEmpty())
			{
				listaProduto.add(this.getProdutoId(listaId.remove(0)));
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
		return listaProduto;
	}
	
	//retorna uma lista de produtos que possuem o nome ou uma parte do nome 
	//igual ao parametro passado
	public ArrayList<Produto> getListaProdutoNomes(String nome) throws SQLException,InterruptedException, InterruptedException 
	{
		ArrayList<Produto> listaProduto = new ArrayList<Produto>(); //cria a lista de produto
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Produto where lower(nome_produto) like ?");
			pstmt.setString(1, "%" + nome.toLowerCase() + "%");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				listaProduto.add(this.montarProduto(rs));
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
		return listaProduto;
	}

	//Retorna uma lista de produtos com TODOS os produtos presentes no banco de dados
	public ArrayList<Produto> getTodosProdutos() throws SQLException,InterruptedException, InterruptedException
	{
		ArrayList<Produto> listaProduto = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Produto produto = null;
		
		try
		{
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Produto order by fk_grupo,nome_produto");
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				produto = this.montarProduto(rs);
				listaProduto.add(produto);
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
			return listaProduto;
	}
	
	//altera o produto
	public Produto alterar(Produto produto) throws SQLException,InterruptedException, InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try
		{
			conn = this.getConnection();

			if(!this.verificaExisteProduto(produto.getIdProduto())) 
			{
				produto.setMensagemErro("Erro! Esse produto não existe.");
				return produto;
			}
			
			if(verificaExisteProdutoNome(produto))
			{
				produto.setMensagemErro("Erro! Já existe um produto cadastrado com esse nome.");
				return produto;
			}
			pstmt = conn.prepareStatement("update Produto set nome_produto = ? where id_produto = ?");
			pstmt.setString(1, produto.getNomeProduto());
			pstmt.setInt(2, produto.getIdProduto());
			
			int conta = pstmt.executeUpdate();
			if(conta > 0)produto.setMensagemErro("Produto alterado com sucesso!");
			else produto.setMensagemErro("Erro! Produto não foi alterado.");
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
		return produto;
	}
	
	//exclui o produto, que possui o id passado como parametro no metodo, do banco de dados
	public Produto excluir(Produto produto) throws SQLException,InterruptedException, InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = this.getConnection();
			
			//verifica se existe movimentação com o produto a ser excluido
			if(this.verificaExisteDependente(produto.getIdProduto()))
			{
				//se houver uma movimentação dependente de produto
				//emite uma mensagem de erro que não é possivel excluir
				produto.setMensagemErro("Erro! Não é possível excluir esse produto pois existem movimentações cadastradas com esse produto.");
				return produto;
			}
			pstmt = conn.prepareStatement("delete from produto where id_produto = ?");
			pstmt.setInt(1, produto.getIdProduto());
			int conta = pstmt.executeUpdate();
			if(conta > 0) produto.setMensagemErro("Produto excluido com sucesso!");
			else produto.setMensagemErro("Erro! Produto não foi excluido.");
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
		return produto;
	}
	
	//verifica se existe um produto com o id passado como parametro
	public boolean verificaExisteProduto(int id) throws SQLException,InterruptedException, InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Produto where id_produto=?");
			
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

	//verifica se existe um produto no banco com o nome passado como parametro
	public boolean verificaExisteProdutoNome(Produto produto) throws SQLException,InterruptedException, InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Produto where nome_produto=? and id_produto != ?");
			
			pstmt.setString(1, produto.getNomeProduto());
			pstmt.setInt(2, produto.getIdProduto());
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
	
	//verifica se exixte uma movimentação dependente do produto
	public boolean verificaExisteDependente(int idProduto) throws SQLException,InterruptedException, InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			
			conn = this.getConnection();
			pstmt = conn.prepareStatement("select * from Movimentacao_Produto where fk_produto=?");
			
			pstmt.setInt(1, idProduto);
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
