import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Hilo_Pueblo_Duerme extends Thread{
	PrintWriter envCliente;
	BufferedReader recCliente;
	El_Pueblo_Duerme pueblo;
	
	Socket socCliente;
	int idVotacion = 0;
	String nombre = "user";
	int rol = 0;
	boolean vivo = true;
	boolean jugando = true;
	
	public Hilo_Pueblo_Duerme(Socket cliente,El_Pueblo_Duerme pueblo) {
		socCliente = cliente;
		this.pueblo = pueblo;
		try {
			envCliente = new PrintWriter(socCliente.getOutputStream(),true);
			recCliente = new BufferedReader(new InputStreamReader(socCliente.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		envCliente.println("Conectado correctamente");
	}
	
	@Override
	public synchronized void run() {
		String nick;
		try {
			envCliente.println("Nombra al aldeano (default = user): ");
			nick = recCliente.readLine();
			if(!nick.trim().isEmpty())
				nombre = nick;
			wait();
			
			if(rol==0) envCliente.println("ERES ALDEANO");
			else envCliente.println("ERES LOBO");
			
			while(jugando) {
				if(!jugando) break;
				envCliente.println(pueblo.getListaAldeadosyEstados());
				int voto;
				boolean aceptado;
				switch (rol) {
				case 1:
					voto = -1;
					do {
						aceptado = false;
						envCliente.println("Elige jugador a matar (nada para saltar): ");
						String recibido = recCliente.readLine();
						if(recibido.trim().isEmpty()) {
							envCliente.println("Saltado con exito");
							break;
						}
						try {voto=Integer.parseInt(recibido);}catch(Exception ex) {envCliente.println("No se reconocio un número");}
						if(voto == idVotacion) envCliente.println("No se puede matar a uno mismo");
						else if(voto > 0) {
							aceptado = pueblo.accionLobo(voto);
							if(aceptado) envCliente.println("Se mató correctamente");
							else envCliente.println("No estás matando a un usuario válido");
						}
					}while((voto < 0 || voto == idVotacion) || !aceptado);
					if(voto == -1) pueblo.accionLobo(voto);
					break;
				}
				if(rol!=1) {
					voto = -1;
					do {
						aceptado = false;
						envCliente.println("Elige jugador a votar: ");
						try {voto=Integer.parseInt(recCliente.readLine());}catch(Exception ex) {envCliente.println("No se reconocio un número");}
						if(voto == idVotacion) envCliente.println("No se puede votar a uno mismo");
						else if(voto >= 0) {
							aceptado = pueblo.votar(voto);
							if(aceptado) envCliente.println("Se votó correctamente");
							else envCliente.println("No estás votando a un usuario válido");
						}
					}while((voto < 0 || voto == idVotacion)|| !aceptado);
				}
				wait();
			
			}
			
		}catch (IOException | InterruptedException e) {
			envCliente.println("Error");
		}
	}
	public synchronized void notificar() {
		notifyAll();
	}
	
	public void murio(String mensaje) {
		if(mensaje==null) envCliente.println("Nadie murió");
		else envCliente.println("Murió " + mensaje);
	}
	
	public int getRol() {
		return rol;
	}

	public void setRol(int rol) {
		this.rol = rol;
	}

	public boolean isVivo() {
		return vivo;
	}

	public void setVivo(boolean vivo) {
		this.vivo = vivo;
		if(!vivo) envCliente.println("Has muerto");
		else envCliente.println("Has revivido");
	}

	public int getIdVotacion() {
		return idVotacion;
	}

	public void setIdVotacion(int idVotacion) {
		this.idVotacion = idVotacion;
	}

	public String getNombre() {
		return nombre;
	}

	public void finalizar(int ganador) {
		jugando=false;
		if(ganador == 2)envCliente.println("JUGADORES INSUFICIENTES!!");
		else {
			envCliente.println("JUEGO FINALIZADO");
			if(ganador==rol) {
				envCliente.println("GANASTE (" + (vivo?"VIVO":"MUERTO") + ")" );
			}else envCliente.println("PERDISTE (" + (vivo?"VIVO":"MUERTO") + ")" );
		}
		envCliente.println(".end");
		notificar();
	}
}
