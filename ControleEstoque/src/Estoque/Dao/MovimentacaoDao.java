package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import Estoque.Modelo.Movimentacao;
import Estoque.Modelo.Produto;
import Estoque.Modelo.Secao;
import Estoque.Persistencia.Conexao;
public class MovimentacaoDao extends Conexao{

	//monta e retorna a movimentação
	public Movimentacao montarMovimentacao(ResultSet rs) throws SQLException,InterruptedException
	{
		//recebe os dados da tabela movimentacao e constroi o objeto movimentacao
		//insere no objeto o id e seção
		Movimentacao movimentacao = new Movimentacao();
		SecaoDao sDao = new SecaoDao();

		movimentacao.setIdMovimentacao(rs.getInt("id_movimentacao"));
		movimentacao.setSecao(sDao.getSecaoId(rs.getInt("fk_secao")));
		
		//recebe os dados da tabela movimentacao_produto e continua construindo o objeto movimentacao
		//insere a lista de produtos
		ProdutoDao prodDao = new ProdutoDao();
		ArrayList<Produto> listaProduto =  movimentacao.getListaProduto();
		Produto produto = prodDao.getProdutoId(rs.getInt("fk_produto"));
		listaProduto.add(produto);
		while(rs.next())
		{	
			produto = prodDao.getProdutoId(rs.getInt("fk_produto"));
			listaProduto.add(produto);
		}
		movimentacao.setListaProduto(listaProduto);
		return movimentacao;
	}
	
	//inclui uma movimentação no banco de dados
	public Movimentacao incluir(Movimentacao movimentacao) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		try
		{			
			//verifica se ja existe uma movimentação com o mesmo id
			if(this.verificaExisteMovimentacaoId(movimentacao.getIdMovimentacao()))
			{
				movimentacao.setMensagemErro("Erro! Já existe uma movimentação cadastrada com esse ID.");
				return movimentacao;
			}
			
			//inclui os registros na tabela Movimentacao
			conn = this.getConnection();
			sql = "insert into Movimentacao (fk_secao) values(?) ";
			pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setInt(1, movimentacao.getSecao().getIdSecao());
			int auxIncluiu  = pstmt.executeUpdate();
			
			//se não incluiu, retorna uma mensagem de erro
			if(auxIncluiu <= 0) 
			{
				movimentacao.setMensagemErro("Erro! Movimentação não foi inserida.");
				return movimentacao;
			}
			
			//inclui os registros na tabela Movimentacao_Produto       
	        ResultSet rs = pstmt.getGeneratedKeys();
	        if(rs.next()){
	            movimentacao.setIdMovimentacao(rs.getInt(1));
	        }
			for(int i = 0; i < movimentacao.getListaProduto().size(); i++)
			{
				sql = "insert into Movimentacao_Produto (fk_movimentacao,fk_produto) values(?, ?)";
				pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				
				pstmt.setInt(1, movimentacao.getIdMovimentacao());
				pstmt.setInt(2, movimentacao.getListaProduto().get(i).getIdProduto());
				auxIncluiu = pstmt.executeUpdate();
				if(auxIncluiu <= 0) 
				{
					movimentacao.setMensagemErro("Erro! Um produto da movimentação não foi inserido.");
					return movimentacao;
				}				
			}
			movimentacao.setMensagemErro("Movimentação e seus produtos incluidos com sucesso!");					
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
		return movimentacao;
	}
	
	//retorna uma movimentação com o id passado como parametro
	public Movimentacao getMovimentacaoId(int id) throws SQLException,InterruptedException 
	{
		Movimentacao movimentacao = new Movimentacao();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			//pesquisa no banco de dados
			pstmt = conn.prepareStatement("SELECT * FROM Movimentacao JOIN Movimentacao_Produto WHERE id_Movimentacao = ? AND id_Movimentacao = fk_Movimentacao ");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			if(rs.next())
			{
				//se achou monta a movimentação
				movimentacao = this.montarMovimentacao(rs);
			}
			
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
		return movimentacao;
	}

	//retorna uma lista de movimentações que possuem um produto e/ou secao que tenha o nome
	// ou contenha em uma parte do nome o que esta sendo passado como parametro no metodo
	public ArrayList<Movimentacao> getListaMovimentacaoProdutoSecao(String nome) throws SQLException,InterruptedException 
	{		
		ArrayList<Movimentacao> listaMovimentacao = new ArrayList<Movimentacao>(); //cria a lista de movimentação
		ArrayList<Movimentacao> listaMovimentacao2 = new ArrayList<Movimentacao>(); //cria a lista de movimentação2
		Set<Integer> listaidMovimentacoes = new LinkedHashSet<Integer>();//não aceita valores repetidos
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = this.getConnection();
			
			//adiciona na lista listaidMovimentacoes todas as movimentações desejadas
			listaMovimentacao = this.getListaMovimentacaoProduto(nome);
			listaMovimentacao2 = this.getListaMovimentacaoSecao(nome);
			while(!listaMovimentacao.isEmpty())
			{
				listaidMovimentacoes.add(listaMovimentacao.remove(0).getIdMovimentacao());
			}
			while(!listaMovimentacao2.isEmpty())
			{
				listaidMovimentacoes.add(listaMovimentacao2.remove(0).getIdMovimentacao());
			}
			
			ArrayList<Integer> listaId = new ArrayList<Integer>();
			listaId.addAll(listaidMovimentacoes);
			
			//adiciona na lista de movimentações todas as movimentacoes que tem um produto e/ou 
			//seção com o mesmo nome ou uma parte do nome digitado
			while(!listaId.isEmpty())
			{
				listaMovimentacao.add(this.getMovimentacaoId(listaId.remove(0)));
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
		return listaMovimentacao;
	}
	
	//retorna uma lista de movimentações que possuem um produto que tenha o nome
	// ou contenha em uma parte do nome o que esta sendo passado como parametro no metodo
	public ArrayList<Movimentacao> getListaMovimentacaoProduto(String nome) throws SQLException,InterruptedException 
	{		
		ArrayList<Produto> listaProduto = new ArrayList<Produto>(); // cria a lista de produtos
		ArrayList<Movimentacao> listaMovimentacao = new ArrayList<Movimentacao>(); //cria a lista de movimentação
		Set<Integer> listaidMovimentacoes = new LinkedHashSet<Integer>();//não aceita valores repetidos
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			ProdutoDao produtoDao = new ProdutoDao();
			listaProduto = produtoDao.getListaProdutoNomes(nome); //preenche a lista de produto com os produtos desejados
			conn = this.getConnection();
			
			while(!listaProduto.isEmpty())
			{
				//Seleciona todos os ids das movimentacoes que tem um produto com o mesmo nome ou uma parte do nome digitado
				pstmt = conn.prepareStatement("select fk_movimentacao from Movimentacao_Produto where fk_produto = ?");
				pstmt.setInt(1, listaProduto.remove(0).getIdProduto());
				rs = pstmt.executeQuery();
				while(rs.next())
				{
					//adiciona o id da movimentação desejada na lista de id de movimentações
					listaidMovimentacoes.add(rs.getInt("fk_movimentacao"));
				}
			}
			
			ArrayList<Integer> listaId = new ArrayList<Integer>();
			listaId.addAll(listaidMovimentacoes);
			
			//adiciona na lista de movimentações todas as movimentacoes que tem um produto 
			//com o mesmo nome ou uma parte do nome digitado
			while(!listaId.isEmpty())
			{
				listaMovimentacao.add(this.getMovimentacaoId(listaId.remove(0)));
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
		return listaMovimentacao;
	}
	
	//retorna uma lista de movimentações que possuem a seção que tenha o nome
	// ou contenha em uma parte do nome o que esta sendo passado como parametro no metodo
	public ArrayList<Movimentacao> getListaMovimentacaoSecao(String nome) throws SQLException,InterruptedException 
	{		
		ArrayList<Secao> listaSecao = new ArrayList<Secao>(); // cria a lista de seção
		ArrayList<Movimentacao> listaMovimentacao = new ArrayList<Movimentacao>(); //cria a lista de movimentação
		Set<Integer> listaidMovimentacoes = new LinkedHashSet<Integer>();//não aceita valores repetidos
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			SecaoDao secaoDao = new SecaoDao();
			listaSecao = secaoDao.getListaSecaoNome(nome); //preenche a lista de seção com as seções desejadas
			conn = this.getConnection();
			
			while(!listaSecao.isEmpty())
			{
				//Seleciona todos os ids das movimentacoes que tem um produto com o mesmo nome ou uma parte do nome digitado
				pstmt = conn.prepareStatement("select id_movimentacao from Movimentacao where fk_secao = ?");
				pstmt.setInt(1, listaSecao.remove(0).getIdSecao());
				rs = pstmt.executeQuery();
				while(rs.next())
				{
					//adiciona o id da movimentação desejada na lista de id de movimentações
					listaidMovimentacoes.add(rs.getInt("id_movimentacao"));
				}
			}
			
			ArrayList<Integer> listaId = new ArrayList<Integer>();
			listaId.addAll(listaidMovimentacoes);
			
			//adiciona na lista de movimentações todas as movimentacoes que tem uma seção 
			//com o mesmo nome ou uma parte do nome digitado
			while(!listaId.isEmpty())
			{
				listaMovimentacao.add(this.getMovimentacaoId(listaId.remove(0)));
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
		return listaMovimentacao;
	}

	//altera a movimentação
	public Movimentacao alterarMovimentacao(Movimentacao movimentacao) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = this.getConnection();
			
			//altera os dados(Seção), da movimentação na tabela Movimentação
			pstmt = conn.prepareStatement("update Movimentacao set fk_secao = ? where id_movimentacao = ?");
			pstmt.setInt(1, movimentacao.getSecao().getIdSecao());
			pstmt.setInt(2, movimentacao.getIdMovimentacao());
			int conta = pstmt.executeUpdate();
			if(conta > 0)
			{
				//Altera os dados(lista de produtos), da movimentação da tabela movimentação_produto
				//Primeiro exclui todas os dados da tabela movimentação_produto que possuem o id da movimentação passada como parametro no metodo
				//Depois inclui os registros atualizados 
				pstmt.close();
				pstmt = conn.prepareStatement("delete from movimentacao_produto where fk_movimentacao = ?");
				
				pstmt.setInt(1, movimentacao.getIdMovimentacao());
				conta = pstmt.executeUpdate();
				if(conta>0)
				{
					for(int i = 0; i < movimentacao.getListaProduto().size(); i++)
					{
						String sql = "insert into Movimentacao_Produto (fk_movimentacao,fk_produto) values(?, ?)";
						pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
						
						pstmt.setInt(1, movimentacao.getIdMovimentacao());
						pstmt.setInt(2, movimentacao.getListaProduto().get(i).getIdProduto());
						conta = pstmt.executeUpdate();
						if(conta <= 0) 
						{
							movimentacao.setMensagemErro("Erro! Movimentação não foi alterada.");
							return movimentacao;
						}
						movimentacao.setMensagemErro("Movimentação alterada com sucesso!");
					}
				}
				else movimentacao.setMensagemErro("Erro! Movimentação não foi alterada.");
			}
			else movimentacao.setMensagemErro("Erro! Movimentação não foi alterada.");
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
		return movimentacao;
	}

	//exclui a movimentação, que possui o id passado como parametro no metodo, do banco de dados
	public Movimentacao excluirMovimentacao(Movimentacao movimentacao) throws SQLException,InterruptedException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = this.getConnection();
			
			//deleta os dados da tabela filha movimentacao_produto
			pstmt = conn.prepareStatement("delete from movimentacao_produto where fk_movimentacao = ?");
			pstmt.setInt(1, movimentacao.getIdMovimentacao());
			int conta = pstmt.executeUpdate();
			if(conta > 0)
			{
				//se os dados da tabela filha foram excluidos
				//exclui os dados da tabela pai movimentacao
				pstmt.close();
				pstmt = conn.prepareStatement("delete from movimentacao where id_movimentacao = ?");
				pstmt.setInt(1, movimentacao.getIdMovimentacao());
				conta = pstmt.executeUpdate();
				
				if(conta>0) movimentacao.setMensagemErro("Movimentação excluida com sucesso!");
				else movimentacao.setMensagemErro("Erro! Movimentação não foi excluida.");
			}
			else movimentacao.setMensagemErro("Erro! Movimentação não foi excluida.");
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
		return movimentacao;
	}
	
	//verifica se existe uma movimentação com o id passado como parametro
	public boolean verificaExisteMovimentacaoId(int id) throws SQLException,InterruptedException
	{
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("select * from Movimentacao where id_movimentacao=?");
			
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
	
}
