package multicast;

import java.rmi.Remote;
import java.rmi.RemoteException; 
    
public interface Printer extends Remote {

    void print(Message receivedMessage) throws RemoteException;
}
