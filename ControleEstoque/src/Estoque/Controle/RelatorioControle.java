package Estoque.Controle;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import Estoque.Dao.SecaoDao;
import Estoque.Modelo.Secao;
import Estoque.Persistencia.Conexao;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperRunManager;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import Estoque.Dao.SecaoDao;
import Estoque.Modelo.Secao;
import Estoque.Persistencia.Conexao;
import net.sf.jasperreports.engine.JasperRunManager;


public class RelatorioControle extends SelectorComposer<Window>{
	@Wire
	Combobox combFiltroSecao;
	@Wire
	Iframe report;
	@Wire
	Window Relatorio;
	@Wire
	Button btnRelatorio;
	
	@Listen("onCreate=#Relatorio")
	public void inicio() throws SQLException, InterruptedException, IOException, JRException
	{
		this.preencheCombSecaoMovimentacao();
		this.combFiltroSecao.setSelectedIndex(0);
	}
	
	//gera o relatorio e o apresenta no iframe
	@Listen("onClick=#btnRelatorio")
	public void gerarRelatorio()
	{
		InputStream is = null;
		try				
		{
			HashMap<String,Object> parametro = new HashMap<String, Object>();   
			String caminho;
			//se não estiver selecionado uma seção especifica
			//o caminho é o relatorio simples			
			if(this.combFiltroSecao.getText().toString().length() > 10)
			{
				caminho = "C:/Users/larac/Desktop/RelatorioMovimentacao";
			}
			
			//se houver uma seção selecionada
			//o caminho é para o relatorio complexo
			else 
			{
				caminho ="C:/Users/larac/Desktop/Complexo";
				//coloca o parametro no sql para filtar pela seção selecionada
	            parametro.put("nome_secao", this.combFiltroSecao.getText().toString());
	            SecaoDao secaoDao = new SecaoDao();
	            int id = secaoDao.getSecaoNome(this.combFiltroSecao.getText().toString()).getIdSecao();
	            parametro.put("id_secao", id);			
			}
			
			Conexao conexao = new Conexao();
			is = new FileInputStream(caminho+".jasper");
			final byte[] buf = JasperRunManager.runReportToPdf(is, parametro, conexao.getConnection());
			
			final InputStream mediais = new ByteArrayInputStream(buf);
			final AMedia amedia = new AMedia(caminho+".pdf", "pdf", "application/pdf", mediais);
			
			report.setContent(amedia);
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	//preenche o combobox de seções com todas as seções cadastradas no sistema
	public void preencheCombSecaoMovimentacao()throws SQLException, InterruptedException,IOException, JRException
	{
		//limpa o combobox
		combFiltroSecao.getItems().clear();
		
		SecaoDao secaoDao = new SecaoDao();
		ArrayList<Secao> listaSecao = new ArrayList<Secao>();
		
		listaSecao = secaoDao.getTodasSecoes();
		
		//adiciona as seções no combobox
		this.combFiltroSecao.appendItem("Todas as Seções");
		this.combFiltroSecao.setSelectedIndex(0);
		while(!listaSecao.isEmpty())
		{
			this.combFiltroSecao.appendItem(listaSecao.remove(0).getNomeSecao());
		}
	}
}
