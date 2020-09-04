package Estoque.Persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import Estoque.Util.Utilidade;

public class Conexao {
	
	private String login = "postgre";
	private String senha = "postgre";
	private String url = "jdbc:postgres://localhost/Banco-Leonardo";
	
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
