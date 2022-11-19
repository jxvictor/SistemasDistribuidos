
import multicast.Message;
import multicast.PublicadorMulticast;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;

import static utils.MessengerUtils.printarMensagem;

public class MessengerImp extends UnicastRemoteObject implements Messenger {

	private static final long serialVersionUID = 1L;

	private PublicadorMulticast publisher = null;
	
	protected MessengerImp() throws RemoteException {
        super();
		try {
			this.publisher = new PublicadorMulticast();
		} catch (IOException e) {
			printarMensagem("[Messenger] Erro ao instanciar publisher: " + e.getMessage());
		}
    }

    public void send(Message multicastMessage) throws RemoteException {
    	try {
			this.publisher.multicast(multicastMessage);
		} catch (IOException e) {
			printarMensagem("[Messenger] Erro ao enviar mensagem multicast: " + e.getMessage());
		}
    }
}