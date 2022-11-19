package multicast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static config.Config.SOCKET_HOST;
import static config.Config.SOCKET_PORT;

public class PublicadorMulticast {

    private MulticastSocket socket;
    private InetAddress group;
    private byte[] buffer;
 
    public PublicadorMulticast() throws IOException {
    	this.socket = new MulticastSocket(SOCKET_PORT);
    	this.group = InetAddress.getByName(SOCKET_HOST);
    }
    
    public void multicast(Message sendingMessage) throws IOException {
        buffer = this.getBytesOfObject(sendingMessage);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, SOCKET_PORT);
        socket.send(packet);
    }

    public void multicastFile(File file) throws IOException {
        buffer = this.getBytesOfObject(file);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, SOCKET_PORT);
        socket.send(packet);
    }
    
    private byte[] getBytesOfObject(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        return baos.toByteArray();
    }

}