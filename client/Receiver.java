package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.PrivateKey;
import api.ClientAPI;

/**
 * Receives file from another client's sender
 */
public class Receiver extends Thread {
    SocketChannel receiverChannel;
    PrivateKey privateKey;
    boolean running = true;
    int fileSize;
    int fileBytesReceivedTotal;

    /**
     * Initializer
     * @param receiverChannel channel receiver was assigned
     * @param privateKey unique private key used in RSA decryption
     */
    public Receiver(SocketChannel receiverChannel, PrivateKey privateKey) {
        this.receiverChannel = receiverChannel;
        this.privateKey = privateKey;

        fileBytesReceivedTotal = 0;
    }

    @Override
    public void run() {
        System.out.println("Running receiver");
        try {
            receiverChannel.configureBlocking(true);

            //Waits for client to properly connect with server
            while (!receiverChannel.finishConnect()) {
                System.out.println("Waiting");
            }
            System.out.println("finnish connecting");

            //Reads initial data (filename + size) from channel
            ByteBuffer data = ByteBuffer.allocate(256);
            receiverChannel.read(data);

            //Decrypts Read data
            // byte[] decData = encrypt.decryptData(data, privateKey);
            // ByteBuffer decBuff = ByteBuffer.wrap(decData);

            //Gets file's size
            fileSize = data.getInt();
            ClientAPI.setDownloadTotal(fileSize);
            
            //Gets file's name (or more accurately path)
            byte[] stringData = new byte[data.array().length - 4];
            data.get(4, stringData, 0, data.array().length - 4);
            String filename = new String(stringData);

            //Splits path from name
            //TODO fix for multiple platforms
            String[] finalFilename = filename.split("/");

            System.out.println("received name: " + filename);
            receiverChannel.configureBlocking(false);
            FileChannel fileChannel = FileChannel.open(Paths.get("received_files/" + finalFilename[finalFilename.length - 1]), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            while (running) {
                ByteBuffer bufFile = ByteBuffer.allocate(256);
                int bytesReadFile = receiverChannel.read(bufFile);

                if (bytesReadFile != 0) {
                    if (bytesReadFile == -1) {
                        System.out.println("got to end");
                        break;
                    } else {
                        // ByteBuffer decFile = ByteBuffer.wrap(encrypt.decryptData(bufFile, privateKey));
                        fileChannel.write(bufFile);
                        // decFile.clear();
                        bufFile.clear();
                        fileBytesReceivedTotal = fileBytesReceivedTotal + bytesReadFile - 8;
                        ClientAPI.updateDownloadBar(fileBytesReceivedTotal);
                    }
                }
            }
            fileChannel.close();
            receiverChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPause(boolean p) {
       ByteBuffer buf = ByteBuffer.allocate(5);
       byte data = (byte)((p) ? (1) : (0));
       buf.position(0);
       buf.put(data);
       buf.putInt(Float.floatToIntBits((float)Math.random()));
       //TODO
       //buf = buf.AESEncrypt(buf, key);
       try {
        buf.rewind();
        receiverChannel.write(buf);
       } catch (Exception e) {
        // TODO: handle exception
       }
       
    }

}
