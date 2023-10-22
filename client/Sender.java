package client;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.PublicKey;
import java.util.Arrays;
import api.ClientAPI;

/**
 * Sends an encrypted file to another client's Receiver
 */
public class Sender extends Thread {
    SocketChannel senderChannel;
    String hostname;
    int port;
    PublicKey publicKey;

    String filename;
    FileInputStream file;
    int fileSize;
    int fileBytesSentTotal;

    boolean running = true;
    boolean paused = true;

    /**
     * Initializer
     * @param hostname Hostname of client file should be sent
     * @param port port of client file should be sent
     * @param publicKey public key to encrypt data
     * @param filename name of file
     */
    public Sender(String hostname, int port, PublicKey publicKey, String filename) {
        this.hostname = hostname;
        this.port = port;
        this.publicKey = publicKey;

        fileBytesSentTotal = 0;

        try {
            this.filename = filename;
            if (!filename.contains("/")) {
                filename = "send_files/" + filename;
            } 
            file = new FileInputStream(filename);
            fileSize = (int)file.getChannel().size();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        System.out.println("running sender");
        try {
            senderChannel = SocketChannel.open();
            senderChannel.configureBlocking(true);
            System.out.println(hostname + " : " + port);
            
            senderChannel.connect(new InetSocketAddress(InetAddress.getByName(hostname), port));            
            while (!senderChannel.finishConnect()) {
                System.out.println("waiting...");
            }
            sendInfo(filename, fileSize);
            ClientAPI.setUploadTotal(fileSize);

            senderChannel.configureBlocking(false);
            while (running) {
                if (!getPaused()) {
                    byte[] buffer = new byte[245];

                    int bufferSize = file.read(buffer);
                    fileBytesSentTotal = fileBytesSentTotal + bufferSize;
                    ClientAPI.updateUploadBar(fileBytesSentTotal);
                    if (bufferSize == -1) {
                        break;
                    }

                    ByteBuffer data = ByteBuffer.allocate(bufferSize);
                    data.position(0);
                    data.put(buffer, 0, bufferSize);
                    ByteBuffer newDat = encrypt.encryptData(data, publicKey);
                    // data.flip();
                    senderChannel.write(newDat);
                }

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                ByteBuffer buf = ByteBuffer.allocate(5);
                int bytesRead = senderChannel.read(buf);
                if (bytesRead == -1) {
                    running = false;
                }
                if (bytesRead > 0) {
                	//TODO
                    //buf = encrypt.AESDecrypt(buf, key);
                    byte pause = buf.get(0);
                    setPaused(pause != 0);
                }
            }
            System.out.println("Closing channel");
            file.close();
            senderChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends initial info that receiver needs
     * @param name Name of file
     * @param fileSize size of file
     */
    public void sendInfo(String name, int fileSize) {
        System.out.println("Sending filename");
        ByteBuffer message = ByteBuffer.allocate(4 + name.length());
        System.out.println(fileSize);
        message.putInt(fileSize);
        System.out.println(Arrays.toString(name.getBytes()));
        message.put(name.getBytes());
        
        // ByteBuffer encryptedMessage = encrypt.encryptData(message, publicKey);
        try {
            senderChannel.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns value of paused.
     * Paused is used to pause file uploading to other client
     * @return paused
     */
    synchronized boolean getPaused() {
        return paused;
    }

    /**
     * Sets value of paused
     * Paused is used to pause file uploading to other client
     * @param set paused
     */
    public synchronized void setPaused(boolean set) {
        if (paused) {
            System.out.println("Receiver paused");
        } else {
            System.out.println("Receiver unpaused");
        }
        paused = set;
    }
}
