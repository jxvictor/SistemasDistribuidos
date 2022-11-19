import multicast.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static config.Config.*;
import static utils.MessengerUtils.printarMensagem;


public class ClienteMessenger {

    public static void main(String[] args) throws RemoteException {

		BufferedReader inData = new BufferedReader(new InputStreamReader(System.in));
		
		String clientName = iniciarCliente(inData);

    	while (entradaConsole(inData, clientName));

		printarMensagem("[Cliente-Messenger] Finalizando...");
    	System.exit(0);
    }

    private static String iniciarCliente(BufferedReader imput) throws RemoteException {
    	String hostName = getHostName();
    	String emailCliente = "";
    	String nomeCliente = "";
		
    	do {
			emailCliente = lerLoginDoCliente(imput);
			if (!hostName.isBlank()) {
				nomeCliente = hostName + '/' + emailCliente;
			} else {
				nomeCliente = emailCliente;
			}
    		
    	} while (validarUsuario(nomeCliente));
		ligarAoRegistro(nomeCliente);
		sendMessage(new Message(MENSAGEM_ENTRADA, nomeCliente));
		return nomeCliente;
    }
    
    private static String getHostName() {
    	try {
    		return InetAddress.getLocalHost().getHostName();
    	} catch (UnknownHostException e) {
			printarMensagem("Erro ao consultar HostName " + e.getMessage());
    	}
    	return "";
    }
    
    private static String lerLoginDoCliente(BufferedReader inData) {
		printarMensagem("[Cliente-Messenger] Digite o nome deste cliente:");
		try {
			return inData.readLine();
		} catch (IOException e) {
			printarMensagem("[Cliente-Messenger] Erro ao ler nome do cliente no console: " + e.getMessage());
		}
		return null;
    }
    
    private static void ligarAoRegistro(String clientName) {
        try { 
            Naming.rebind(getServicoDoCliente(clientName), new PrinterImp());
        } catch (Exception e) {
			printarMensagem("[Cliente-Messenger] Erro ao iniciar servidor RMI do cliente: " + e.getMessage());
			System.exit(0);
        }
    }

	private static boolean validarUsuario(String email) {
		List<String> services = buscarServicosRegistrados();
		List<String> usedServices = services.stream()
				.filter(service -> service.equals(getServicoDoCliente(email).substring(4)))
				.collect(Collectors.toList());
		if(!usedServices.isEmpty()){
			printarMensagem("[Cliente-Messenger] E-mail ja em uso, escolha outro.");
			return true;
		}
		return false;
	}

	private static List<String> buscarServicosRegistrados() {
		try {
			return Arrays.asList(Naming.list(NOME_RMI_REGISTRY));
		} catch (Exception e) {
			printarMensagem("[Cliente-Messenger] Erro ao buscar RMI. " + e.getMessage());
		}
		return Collections.emptyList();
	}
    
	private static boolean entradaConsole(BufferedReader inData, String clientName) throws RemoteException {
		String input = leitorConsole(inData);
		if (input.equals(SAIR)) {
			sendMessage(new Message(MENSAGEM_SAIDA, clientName));
			desfazerDoRegistro(clientName);
			return false;
		}
		sendMessage(new Message(input, clientName));
		return true;
	}
	
    private static String leitorConsole(BufferedReader inData) {
		try {
			return inData.readLine();
		} catch (IOException e) {
			printarMensagem("[Cliente-Messenger] Erro ao ler mensagem do console: " + e.getMessage());
		}
		return null;
    }
    
	private static void sendMessage(Message message) throws RemoteException {
		Messenger service = getMessenger();
		try {
			service.send(message);
		} catch (RemoteException e) {
			printarMensagem("[Cliente-Messenger] Falha ao enviar mensagem para o servidor: " + e.getMessage());
		}
	}
    
    private static void desfazerDoRegistro(String clientName) {
		try {
			Naming.unbind(getServicoDoCliente(clientName));
		} catch (Exception e) {
			printarMensagem("[Cliente-Messenger] Erro ao remover serviço RMI " + e.getMessage());
		}
    }
    
    private static Messenger getMessenger() throws RemoteException {
        try {
			Registry registry = LocateRegistry.getRegistry(9915);
			Messenger messenger = (Messenger) registry.lookup(MessengerServer.SERVICO);
        	return messenger;
        } catch (Exception e) {
			printarMensagem("[Cliente-Messenger] Erro ao iniciar cliente RMI: " + e.getCause());
        }
        return new MessengerImp();
    }
    
	private static String getServicoDoCliente(String clientName) {
		return MessengerServer.SERVICO + "-" + clientName;
	}
}