
import multicast.Message;

import java.rmi.Remote;
import java.rmi.RemoteException; 
    
public interface Messenger extends Remote {

    void send(Message multicastMessage) throws RemoteException;
}
