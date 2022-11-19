
import multicast.RecebedorMulticast;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static utils.MessengerUtils.printarMensagem;

public class MessengerServer {

    public static final String SERVICO = "rmi://localhost:9915/ServicoDeMensagens";
    
    public static void main(String[] args) {
        try {
        	Messenger mensagem = new MessengerImp();
            Registry registry = LocateRegistry.createRegistry(9915);

            registry.rebind(SERVICO, mensagem);
            printarMensagem("[ServerMessenger] Escutando em " + SERVICO + ".");
        } catch (Exception e) {
            printarMensagem("[ServerMessenger] Erro ao iniciar servidor RMI: " + e.getMessage());
        }
        RecebedorMulticast receiver = new RecebedorMulticast();
		receiver.start();
    }
}
