import java.util.ArrayList;
import java.util.Random;

class El_Pueblo_Duerme {
	Random rd = new Random();
	ArrayList<Hilo_Pueblo_Duerme> jugadoresList = new ArrayList<Hilo_Pueblo_Duerme>();
	ArrayList<Integer> votaciones = new ArrayList<Integer>();
	boolean jugando = true;
	int loboVoto = -1;
	int puebloVoto = -1;
	int votosPueblo = 0;
	public void add(Hilo_Pueblo_Duerme hilo) {
		jugadoresList.add(hilo);
	}
	public synchronized void start() {
		
		jugadoresList.get(rd.nextInt(jugadoresList.size())).setRol(1);
		for(int i = 0; i < jugadoresList.size(); i++) {
			Hilo_Pueblo_Duerme hilo = jugadoresList.get(i);
			hilo.setIdVotacion(i);
		}
		int vivos = 0;
		for(Hilo_Pueblo_Duerme hilo:jugadoresList) if(hilo.isVivo()) vivos++;
		if(vivos <3) {acabado(2);}
		while(jugando) {
			vivos = 0;
			for(Hilo_Pueblo_Duerme hilo:jugadoresList) if(hilo.isVivo()) vivos++;
			if(vivos <3) {acabado(1);break;}
			votosPueblo = 0;
			for(Hilo_Pueblo_Duerme hilo:jugadoresList) hilo.notificar();
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	public String getListaAldeadosyEstados() {
		String respuesta = "";
		for(Hilo_Pueblo_Duerme hilo:jugadoresList) {
			respuesta = respuesta + hilo.getIdVotacion() + " | " + hilo.getNombre() + " | " + (hilo.isVivo()?"VIVO":"MUERTO") + "\n";
		}
		return respuesta;
	}
	public synchronized boolean accionLobo(int accion) {
		if(!(accion <-1 )&& accion<jugadoresList.size()) {
			if(jugadoresList.get(accion).isVivo()) {
				loboVoto = accion;
				notificar();
				return true;
			}
			else return false;
			
		}else return false;
	}
	public synchronized boolean votar(int voto) {
		if(!(voto<-1) && voto<jugadoresList.size()) {
			if(jugadoresList.get(voto).isVivo()) {
				votaciones.add(voto);
				votosPueblo ++;
				if(votosPueblo == jugadoresList.size()-1) calcularVotos();
				return true;
			}
			else return false;
		}else return false;
	}
	public void calcularVotos() {
		puebloVoto = -1;
		int mayor = -1;
		int maxCount = 0;
		int count = 0;
		boolean repe=true; 
		for(Hilo_Pueblo_Duerme hilo:jugadoresList) {
			count = 0;
				int id = hilo.getIdVotacion();
				for(int i:votaciones) {
					if(i==id)count++;
				}
				if(count>maxCount) {
					maxCount = count;
					mayor = id;
					repe = false;
				}else if(count==maxCount) repe=true;
		}
		if(!repe)puebloVoto = mayor;
		notificar();
	}
	public void kill(int votado) {
		if(votado<0) {
			for(Hilo_Pueblo_Duerme hilo : jugadoresList) {
				hilo.murio(null);
			}
		}else{
			for(Hilo_Pueblo_Duerme hilo:jugadoresList) {
				if(votado==hilo.getIdVotacion()) hilo.setVivo(false);
				else hilo.murio(hilo.getNombre() + "(" + hilo.getIdVotacion() +")" );
			}
		}
		if(jugadoresList.get(votado).rol==1) {
			acabado(0);
		}
	}
	private synchronized void acabado(int juegoRematado) {
		jugando = false;
		try {notifyAll();}catch(Exception e) {};
		for(Hilo_Pueblo_Duerme hilo : jugadoresList) {
			hilo.finalizar(juegoRematado);
		}
		notifyAll();
	}
	private synchronized void notificar() {
		System.out.println("Llega");
		System.out.println(puebloVoto + " " + loboVoto);
		if(puebloVoto>-1 && loboVoto>-1) {
			System.out.println("Llega 2");
			kill(puebloVoto);
			jugadoresList.get(loboVoto).setVivo(false);
			puebloVoto = -1;
			loboVoto = -1;
			notifyAll();
		}
	}
}