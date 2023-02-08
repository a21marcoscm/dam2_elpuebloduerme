import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor_Pueblo_Duerme {
	Scanner sc = new Scanner(System.in);
	El_Pueblo_Duerme pueblo = new El_Pueblo_Duerme();
	public static void main(String[] args) {
		new Servidor_Pueblo_Duerme();
	}
	public Servidor_Pueblo_Duerme() {
		
		Hilo_Servidor hiloServidor = new Hilo_Servidor(pueblo);
		hiloServidor.start();
		System.out.println("Para iniciar la partida escriba \"start\"");
		while(true) {
			if(sc.nextLine().toLowerCase().equals("start")) 
				break;
			else
				System.out.println("Para iniciar la partida escriba \"start\"");
		}
		pueblo.start();
		System.out.println("Se inicio");
	}
}