package Estoque.Persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import Estoque.Util.Utilidade;

public class Conexao {
	
	private String login = "root";
	private String senha = "";
	private String url = "jdbc:mysql://localhost/zk";
	
	public Connection conexao = null;
	
	//Conecta com o banco de dados
	public Connection getConnection() throws InterruptedException
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			this.conexao = DriverManager.getConnection(url,login,senha);
		}
		catch(SQLException ex)
		{
			Utilidade.mensagem("Erro! Não conectou com o banco de dados");
		}
		return this.conexao;
	}
}
