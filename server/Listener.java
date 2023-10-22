package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Thread that implements TCP connection for server
 */
public class Listener extends Thread {

    int port;
    boolean running = true;
    String fileName;

    ArrayList<ServerTCP> connections = new ArrayList<ServerTCP>();

    public Listener(int p) {
        port = p;
    }

    @Override
    public void run() {
        // Opens a socket that listens for client connections
        try (ServerSocketChannel socket = ServerSocketChannel.open()) {
            socket.socket().bind(new InetSocketAddress(port));
            socket.configureBlocking(false);

            while (running) {
                SocketChannel s = socket.accept();
                if (s != null) {
                    System.out.println("Client connected successfully.");

                    ServerTCP tcpserver = new ServerTCP(s, connections);
                    tcpserver.start();
                    connections.add(tcpserver);
                }
            }
            socket.close();
            System.out.println("Server socket properly closed:" + !socket.isOpen());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * Returns the list of active connected clients
    *
    */
    public synchronized ArrayList<ServerTCP> getConnections() {
        return this.connections;
    }

}
