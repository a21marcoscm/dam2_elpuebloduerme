import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Hilo_Servidor extends Thread{
	ServerSocket servidor;
	El_Pueblo_Duerme pueblo;
	boolean continuar;
	public Hilo_Servidor(El_Pueblo_Duerme pueblo) {
		this.pueblo = pueblo;
		try {
			servidor = new ServerSocket(7031);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		continuar = true;
		while(continuar) {
			try {
				Socket cliente = servidor.accept();
				if(continuar) {
					Hilo_Pueblo_Duerme hiloCliente = new Hilo_Pueblo_Duerme(cliente, pueblo);
					pueblo.add(hiloCliente);
					hiloCliente.start();
				}
			} catch (IOException e) {
				System.err.println("Error conectando a un usuario.");
			}
		}
	}
	public void pausar() {
		continuar = false;
		try {
			Socket stop = new Socket("localhost",7031);
		} catch (Exception e) {
		}
		System.exit(0);
	}
}