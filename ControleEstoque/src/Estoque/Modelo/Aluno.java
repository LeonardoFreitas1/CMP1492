package Estoque.Modelo;

public class Aluno {
	
	private int idAluno;
	private int idUsuario;
	private String Matricula;
	private String DataMatricula;
	
	public int getIdAluno() {
		return idAluno;
	}
	public void setIdAluno(int idAluno) {
		this.idAluno = idAluno;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getMatricula() {
		return Matricula;
	}
	public void setMatricula(String matricula) {
		Matricula = matricula;
	}
	public String getDataMatricula() {
		return DataMatricula;
	}
	public void setDataMatricula(String dataMatricula) {
		DataMatricula = dataMatricula;
	}
	
}
