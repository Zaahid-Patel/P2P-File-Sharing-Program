package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.PrivateKey;

/**
 * Listens for incoming sender connections to start a Receiver counterpart
 */
public class ReceiverListener extends Thread {
    PrivateKey privateKey;
    int portNumber;
    boolean running = true;
    Receiver receiver;

    /**
     * Initializer
     * @param port port that we listen for sender connections
     * @param privateKey private key used in RSA decryption
     */
    ReceiverListener(int port, PrivateKey privateKey) {
        portNumber = port;  
        this.privateKey = privateKey;
    }

    @Override
    public void run() {
        //No functional use. Purely to obtain server ip address
        System.out.println("running receiver listener");
        try {
            System.out.println(InetAddress.getLocalHost() + " : " + portNumber);
        } catch (UnknownHostException e) {
            System.out.println("Could not find host address");
        }
        
        try (ServerSocketChannel receiverListenChannel = ServerSocketChannel.open()) {
            receiverListenChannel.socket().bind(new InetSocketAddress(portNumber));
            receiverListenChannel.configureBlocking(false);
            while (running) {
                SocketChannel socketChannel = receiverListenChannel.accept();
                //Creates a Receiver thread for a client when a sender connects
                if (socketChannel != null) {
                    System.out.println("Client Connected");
                    receiver = new Receiver(socketChannel, privateKey);
                    receiver.start();
                    //Only accepts single connection
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Stops thread
     */
    synchronized void stopRunning() {
        running = false;
    }

    public void setPaused(boolean b) {
        receiver.sendPause(b);
    }
}
