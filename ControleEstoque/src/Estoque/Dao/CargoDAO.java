package Estoque.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Estoque.Modelo.Cargo;
import Estoque.Persistencia.Conexao;

public class CargoDAO extends Conexao {

	public Cargo montarCargo(ResultSet rs) throws SQLException,InterruptedException {
		Cargo cargo = new Cargo();
		
		cargo.setIdCargo(rs.getInt("id_cargo"));
		cargo.setNomeCargo(rs.getString("nome_cargo"));
		return cargo;
	}
	
	//inclui um usuário no banco de dados
	public boolean incluir(Cargo cargo) throws SQLException, InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			conn = this.getConnection();
			
			sql.append("INSERT INTO Cargos      "); 
			sql.append("       (id_cargo,      "); 
			sql.append("        nome_cargo)    ");
			sql.append("VALUES (?, ?)          ");
			
			pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setInt(1, cargo.getIdCargo());
			pstmt.setString(2, cargo.getNomeCargo());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e) {
			return false;
		}
		
	}
		
	//retorna o usuario com o id igual ao passado como parametro
	public Cargo getCargoId(int id) throws SQLException,InterruptedException  {
		Cargo cargo = new Cargo();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try
		{
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("SELECT * FROM Cargo WHERE id_cargo=?");
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				cargo = montarCargo(rs);
			}
			
			return cargo;
		} catch(Exception e) {
			return null;
		}
		
	}
	
	//Retorna uma lista de usuario com todos os usuarios presentes no banco de dados
	public ArrayList<Cargo> getTodosCargos() throws SQLException,InterruptedException {
		ArrayList<Cargo> listaCargos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Cargo cargo = null;
		
		try {
			conn = this.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Cargo ORDER BY id_cargo");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				cargo = this.montarCargo(rs);
				listaCargos.add(cargo);
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
			return listaCargos;
	}
	
	//altera o usuario
	public boolean alterar(Cargo cargo) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql;
		try
		{
			conn = this.getConnection();
			sql = new StringBuilder();
			
			sql.append("UPDATE Cargos    ");
			sql.append("SET nome_cargo=? ");
			sql.append("WHERE id_cargo=? ");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, cargo.getIdCargo());
			pstmt.setString(2, cargo.getNomeCargo());
			
			pstmt.execute();
			
			return true;
		}catch(Exception e){
			return false;
			
		}

	}
	
	//exclui o usuário, que possui o id passado como parametro no metodo, do banco de dados
	public boolean excluir(Cargo cargo) throws SQLException,InterruptedException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = this.getConnection();
			
			pstmt = conn.prepareStatement("DELETE FROM Cargo WHERE id_cargo = ?");
				pstmt.setInt(1, cargo.getIdCargo());
				
				pstmt.execute();
				
				return true;
			}
			catch(Exception e) {
				return false;
			}
			
		}
	
}
