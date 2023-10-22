package client;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Temp class that just runs file sending/receiving
 * TODO: DO NOT USE THIS CLASS IN FINAL PRODUCT
 */
public class MainEncrypt extends Thread {
    ReceiverListener listener;
    Sender sender;

    KeyPair keyPair;
    boolean running = true;

    @Override
    public void run() {
        System.out.println("Running encrypt");
        //Reviewed notes, realized mistake, Keypair+verification handled by Signallers
        keyPair = encrypt.generateRSAKeys();
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        System.out.println("Public key: " + Base64.getEncoder().encodeToString(publicKeyBytes));
        String test = "test";

        //NOTE: buffer cannot be larger than 245, otherwise encryption wont run
        ByteBuffer dat = ByteBuffer.wrap(test.getBytes());

        ByteBuffer buf = encrypt.encryptData(dat, keyPair.getPublic());
        byte[] dec = encrypt.decryptData(buf, keyPair.getPrivate());

        String decTest = new String(dec);
        System.out.println(decTest);
        while (running) {}
    }

    //NOTE: only start receiver when server asks
    //TODO: sending client should receive ACK before it tries connecting
    /**
     * Starts receiver Listener thread on [port]
     * @param port port it listens for connections
     */
    public void startReceiver(int port) {
        listener = new ReceiverListener(port, keyPair.getPrivate());
        listener.start();
    }

    /**
     * Starts sender thread connected to [hostname] on [port]
     * @param hostname host to connect to
     * @param port port to connect to
     */
    public void startSender(String hostname, int port) {
        sender = new Sender(hostname, port, keyPair.getPublic(), "send_files/test.txt");
        sender.start();
    }

    public void startSender(String hostname, int port, PublicKey publicKey) {
        sender = new Sender(hostname, port, publicKey, "send_files/test.txt");
        sender.start();
    }

    /**
     * Pauses sender. Should be triggered when receiver wills it
     * @param set Boolean setting paused
     */
    public void setPaused(boolean set) {
        if (sender != null) {
            sender.setPaused(set);
        }
    }
}
