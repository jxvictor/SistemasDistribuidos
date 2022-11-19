package multicast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Locale;

import static config.Config.*;
import static utils.MessengerUtils.printarMensagem;


public class RecebedorMulticast extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buffer = new byte[256];
 
    public void run() {
    	printarMensagem("[MulticastReceiver] - Inicializando...");
    	this.createSocket();
        InetAddress group = this.findGroup();
		this.joinGroup(group);
		
		printarMensagem("[MulticastReceiver] - Aguardando por mensagens...");
        while (true) {
            this.receiveMessage();
            Message receivedMessage = (Message) this.getObjectFromBytes();
            System.out.println(receivedMessage);
            this.sendMessageToAllClients(receivedMessage);
        }

    }
    
    private void createSocket() {
    	printarMensagem("[MulticastReceiver] - Criando Socket na porta " + SOCKET_PORT + ".");
        try {
			socket = new MulticastSocket(SOCKET_PORT);
		} catch (IOException e) {
			printarMensagem("[MulticastReceiver] Erro ao criar socket: " + e.getMessage());
		}
    }
    
    private InetAddress findGroup() {
		printarMensagem("[MulticastReceiver] - Obtendo grupo " + SOCKET_HOST + ".");
		try {
			return InetAddress.getByName(SOCKET_HOST);
		} catch (UnknownHostException e) {
			printarMensagem("[MulticastReceiver] Erro ao tentar obter grupo: " + e.getMessage());
		}
		return null;
    }
    
    private void joinGroup(InetAddress group) {
        printarMensagem("[MulticastReceiver] - Entrando no grupo " + SOCKET_HOST);
        try {
        	socket.joinGroup(group);
		} catch (IOException e) {
			printarMensagem("[MulticastReceiver] Erro ao tentar entrar no grupo: " + e.getMessage());
		}
    }
    
    private void receiveMessage() {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
			socket.receive(packet);
		} catch (IOException e) {
			printarMensagem("[MulticastReceiver] Erro ao receber pacote: " + e.getMessage());
		}
    }
    
    private Object getObjectFromBytes() {
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
		} catch (IOException e) {
			printarMensagem("[MulticastReceiver] " + e.getMessage());
		}
		
        try {
            return ois.readObject();
        } catch (Exception e) {
        	printarMensagem("[MulticastReceiver] " + e.getMessage());
        }
        return null;
    }
   
    private void sendMessageToAllClients(Message message) {
    	String[] services = this.findRegisteredServices();
		for (int i = 0; i < services.length; i++) {
			if (services[i].contains(NOME_SERVICO_CLIENTE)) {
				this.sendMessageToClient(message, services[i]);
			}
		}
    }
    
    private String[] findRegisteredServices() {
		try {
			Registry registry = LocateRegistry.getRegistry(9915);
			return registry.list();
		} catch (Exception e) {
			printarMensagem("Erro ao buscar servicos registrados " + e.getMessage());
		}
		return null;
    }
    
	private void sendMessageToClient(Message message, String client) {
		Printer service = this.getPrinter(client);
		try {
			service.print(message);
		} catch (RemoteException e) {
			printarMensagem("[MulticastReceiver] Falha ao enviar mensagem para: " + client + ".");
			printarMensagem("[MulticastReceiver] Removendo este client do RMI Registry.");
			this.unbindFromRegistry(client);
		}
	}
	
    private void unbindFromRegistry(String client) {
		try {
			Naming.unbind(client);
		} catch (Exception e) {
			printarMensagem("Houve um erro no processo: " + e.getMessage());
		}
    }
	
    private Printer getPrinter(String client) {
        try {
			Registry registry = LocateRegistry.getRegistry(9915);
			String nome = registry.list()[1];
			return (Printer) registry.lookup(nome);
        } catch (Exception e) {
			printarMensagem("[MulticastReceiver] Erro ao iniciar cliente RMI do servidor: " + e.getMessage());
        }
        return null;
    }

}