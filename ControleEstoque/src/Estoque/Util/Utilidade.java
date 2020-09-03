package Estoque.Util;

import org.zkoss.zul.Messagebox;

public class Utilidade {

	private static String titulo = "Aviso";

	//emite uma messageBox com um botão OK e
	//mostrando o a mensagem passada como parametro
	public static void mensagem(String msg)throws InterruptedException
	{
		Messagebox.show(msg, titulo, Messagebox.OK, Messagebox.INFORMATION);
	}

}
