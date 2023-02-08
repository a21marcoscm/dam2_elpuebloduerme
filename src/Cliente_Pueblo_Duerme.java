import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente_Pueblo_Duerme {
	final int port = 7031;
	final String host = "localhost";
	public static void main(String[]args) {
		new Cliente_Pueblo_Duerme();
	}
	private Cliente_Pueblo_Duerme() {
		BufferedReader systemReader = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader rd = null;
		Hilo_Cliente hiloEscritura = null;
		try {
			Socket cl = new Socket(host,port);
			PrintWriter pw = new PrintWriter(cl.getOutputStream(),true);
			rd = new BufferedReader(new InputStreamReader(cl.getInputStream()));
			hiloEscritura = new Hilo_Cliente(pw,systemReader);
			hiloEscritura.start();
		}catch(Exception e) {
			System.err.println("Error general del programa sucedio");
		}
		String men = "";
		while(!men.equals(".end")) {
			try {
				men = rd.readLine();
				if(!men.equals(".end")) {
					System.out.println(men);
				}
			}catch(IOException e) {
				System.err.println("No se pudo leer la respuesta del servidor");
				break;
			}
		}
		hiloEscritura.matar();
		System.out.println("Pulsa enter para cerrar el programa");
	}
}
class Hilo_Cliente extends Thread{
	PrintWriter pw;
	BufferedReader systemReader;
	public Hilo_Cliente(PrintWriter pw, BufferedReader systemReader) {
		this.pw = pw;
		this.systemReader = systemReader;
	}boolean correr = true;
	@Override
	public void run() {
		while(correr) {
			try {
				pw.println(systemReader.readLine());
			}catch (IOException e) {
				System.err.println("No se pudo enviar el mensaje del cliente");
			}
		}
	}
	public void matar() {
		correr = false;
	}
}
