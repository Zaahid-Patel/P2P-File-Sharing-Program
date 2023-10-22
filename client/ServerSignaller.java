package client;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import api.ClientAPI;
import api.FileData;

/**
 * Thread that interacts with the server
 */
public class ServerSignaller extends Thread {

    SocketChannel socket;
    String serverName;
    int serverPort;

    PublicKey serverPublicKey;
    SecretKey aesKey;

    String username;
    boolean usernameACKReceived;
    boolean usernameAccepted;

    boolean running = true;

    boolean connected = false;

    String usernames[];
    String filenames[];
    List<FileData> searchFiles = new ArrayList<FileData>();

    String uploadFolder = "send_files/";
    String downloadFolder = "received_files/";

    ReceiverListener receiver;

    // How close do two filenames need to be?
    int fuzz = 3;

    /**
     * Constructor
     * @param sName Hostname of server
     * @param sPort Port of server
     * @param pubFile File for public key of server
     */
    public ServerSignaller(String sName, int sPort, String pubFile) {
        serverName = sName;
        serverPort = sPort;
        //TODO: Read servers public key from file
    }

    @Override
    public void run() {
        try {
            socket = SocketChannel.open();
            socket.configureBlocking(false);
            System.out.println(serverName + ":" + serverPort);
            
            socket.connect(new InetSocketAddress(InetAddress.getByName(serverName), serverPort));

            while (!socket.finishConnect()) {}
            System.out.println("finished connecting");
            connected = true;

            while (running) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = socket.read(buffer);

                if (bytesRead == -1) {
                    running = false;
                }
                if (bytesRead > 0) {
                    buffer = encrypt.AESDecrypt(buffer, aesKey);
                    byte code = buffer.get(0);
                    char next;

                    //TODO: [low priority] need code for user connected/disconnected
                    //TODO: [low priority] need code for broadcasting message
                    switch (code) {
                        // ACK Username
                        case 3:
                            usernameACKReceived = true;
                            byte accepted = buffer.get(1);
                            System.out.println(accepted);
                            usernameAccepted = accepted == 0;
                            if (usernameAccepted) {
                                System.out.println("Username accepted");
                            } else {
                                System.out.println("Username rejected");
                            }
                            ClientAPI.validUsername(usernameAccepted);
                            break;

                        // Search request
                        case 5:
                            String fName = "";
                            buffer.position(1);
                            next = (char) buffer.get();
                            while (next != '\0') {
                                fName += next;
                                next = (char) buffer.get();
                            }
                            String sName = "";
                            buffer.position(33);
                            next = (char) buffer.get();
                            while (next != '\0') {
                                sName += next;
                                next = (char) buffer.get();
                            }
                            System.out.println("Got search request for " +
                                               fName + " from " + sName);
                            sendSearchResults(fName, sName);
                            break;

                        // Search result
                        case 7:
                            buffer.position(1);
                            short num = buffer.getShort();
                            searchFiles.clear();
                            System.out.println("Got " + num + " search results");

                            String username = "";
                            buffer.position(3);
                            char next2 = (char) buffer.get();
                            while (next2 != '\0') {
                                username += next2;
                                next2 = (char) buffer.get();
                            }
                            for (int i = 0; i < num; i++) {

                                String filename = "";
                                buffer.position(35 + 32*i);
                                next2 = (char) buffer.get();
                                while (next2 != '\0') {
                                    filename += next2;
                                    next2 = (char) buffer.get();
                                }
                                searchFiles.add(new FileData(0, username, filename));
                                System.out.println("\t-" + filename + " from  "
                                                   + username);

                            }
                            //TODO: Get searchfiles to GUI somehow
                            
                            ClientAPI.updateFileList(searchFiles);
                            break;

                        // Download request from server
                        case 9:
                            // Get filename
                            buffer.position(1);
                            String filename = "";
                            next = (char) buffer.get();
                            while (next != '\0') {
                                filename += next;
                                next = (char) buffer.get();
                            }

                            // // Get AES key
                            // buffer.position(33);
                            // byte[] keyBytes = new byte[32];
                            // buffer.get(keyBytes);
                            // SecretKey key = new SecretKeySpec(
                            //                         keyBytes, 0,
                            //                         keyBytes.length, "AES"
                            //                     );

                            // Get port and IP
                            buffer.position(65);
                            int port = buffer.getShort();
                            byte[] ip = new byte[4];
                            buffer.get(ip);
                            String ipString = InetAddress.getByAddress(ip).getHostAddress();

                            //TODO: Handle SecretKey, probably if ==, start sender, else exit
                            // System.out.println("Got download request for " + filename +
                            //                    " from " + ipString + ":" + port + 
                            //                    " with key " +
                            //                    bytesToHex(aesKey.getEncoded()));
                                               
                            //Starts upload GUI
                            
                            FileData uploadFileData = new FileData(0, ipString, filename);
                            ClientAPI.uploadFile(uploadFileData);

                            Sender sender = new Sender(ipString, port, 
                                                       serverPublicKey, filename);
                            sender.start();
                            System.out.println("Sender started");

                            break;

                    }
                }
            }
            System.out.println("Closing server channel");
            socket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean getConnected() {
        return connected;
    }

    /**
     * Generates AES key and sends it to server; Uses code 1
     */
    synchronized void sendAESKey() {
        try {
            aesKey = KeyGenerator.getInstance("AES").generateKey();
            ByteBuffer buf = ByteBuffer.allocate(33);
            buf.position(0);
            buf.put((byte) 1);
            buf.put(aesKey.getEncoded());
            //TODO RSA encrypt
            buf.rewind();
            socket.write(buf);

            System.out.println("Sent AES key to server:" +
                               bytesToHex(aesKey.getEncoded()));
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }

    /**
     * Helper function to convert byte array to hex
     * @param bytes Array to convert
     * @return string returned
     */
    String bytesToHex(byte[] bytes) {
        String res = "";
        for (int i = 0; i < bytes.length; i++) {
            res += String.format("%02X", bytes[i]);
        }
        return res;
    }

    /**
     * Sets username and send to file; Uses code 2
     * @param uName Username
     */
    public synchronized void sendUserName(String uname) {
        try {
            username = uname;
            ByteBuffer buf = ByteBuffer.allocate(33);
            buf.position(0);
            buf.put((byte) 2);
            buf.put(uname.getBytes(StandardCharsets.UTF_8));
            buf.put((byte) '\0');
            usernameACKReceived = false;
            buf = encrypt.AESEncrypt(buf, aesKey);

            buf.rewind();
            socket.write(buf);
            System.out.println("Username sent: " + username);
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }

    /**
     * Search for file; Uses code 4
     * @param uName Username
     */
    public void sendSearch(String fname) {
        try {
            ByteBuffer buf = ByteBuffer.allocate(33);
            buf.position(0);
            buf.put((byte) 4);
            buf.put(fname.getBytes(StandardCharsets.UTF_8));
            buf.put((byte) '\0');
            buf = encrypt.AESEncrypt(buf, aesKey);

            buf.rewind();
            socket.write(buf);
            System.out.println("Sent search request for " + fname);
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }

    /**
     * Send filelist to server; Uses code 6
     * @param fName Filename used in search for files
     */
    public void sendSearchResults(String fname, String uname) {
        String[] filenames = (new File(uploadFolder)).list();
        short count = (short)filenames.length;
        for (int i = 0; i < filenames.length; i++) {
            if (!isClose(filenames[i], fname)) {
                filenames[i] = "";
                count--;
            }
        }

        ByteBuffer buf = ByteBuffer.allocate(35 + 32*count);
        buf.position(0);
        buf.put((byte) 6);
        buf.put(uname.getBytes());
        buf.put((byte) '\0');
        buf.position(33);
        buf.putShort(count);
        int j = 0;
        System.out.println("Searching for files close to " + fname);
        for (int i = 0; i < filenames.length; i++) {
            if (!filenames[i].equals("")) {
                System.out.println("Found " + filenames[i]);
                buf.position(35 + 32*j);
                buf.put(filenames[i].getBytes(StandardCharsets.UTF_8));
                buf.put((byte) '\0');
                j++;
            }
        }

        try {
            buf = encrypt.AESEncrypt(buf, aesKey);
            buf.rewind();
            socket.write(buf);
            System.out.println("Filenames sent");
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }

    /**
     * Uses Damerau-Levensthein distance to compare filenames; Based on pseudocode
     * from https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance
     * @param fname1 First filename
     * @param fname2 Second filename
     * @return Are the filenames close enough?
     */
    boolean isClose(String fname1, String fname2) {
        fname1 = fname1.toLowerCase().replaceAll("\\s+","");
        fname2 = fname2.toLowerCase().replaceAll("\\s+","");
        int d[][] = new int[fname1.length() + 1][fname2.length() + 1];
        for (int i = 0; i <= fname1.length(); i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= fname2.length(); j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < fname1.length(); i++) {
            for (int j = 1; j < fname2.length(); j++) {
                int cost;
                if (fname1.charAt(i-1) == fname2.charAt(j-1)) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                d[i][j] = Math.min(
                            Math.min(d[i-1][j] + 1, d[i][j-1] + 1),
                            d[i-1][j-1] + cost);
                if (i > 1 && j > 1 && fname1.charAt(i-1) == fname2.charAt(j-2)
                          && fname1.charAt(i-2) == fname2.charAt(j-1)) {
                    d[i][j] = Math.min(d[i][j], d[i-2][j-2] + 1);
                }
            }
        }
        // Number chosen arbitrarily, might need tweeking
        int a = Math.max(fname1.length(), fname2.length());
        return d[fname1.length()][fname2.length()] < fuzz;
    }

    public void setFuzz(int fuzzitude) {
        fuzz = fuzzitude;
    }

    /**
     * Request file from other client through server; Uses code 8
     * @param uname Username of client to request file from
     * @param fname Name of file to request
     * @param key AES key to send to sending client
     * @param port Port opened by us for sender to connect to
     */
    public void requestFile(String uname, String fname) {
        System.out.println("Request file download");
        // Put username in buf
        ByteBuffer buf = ByteBuffer.allocate(99);
        buf.position(0);
        buf.put((byte) 8);
        buf.put(uname.getBytes(StandardCharsets.UTF_8));
        buf.put((byte) '\0');

        // Put file name in buf
        buf.position(33);
        buf.put(fname.getBytes(StandardCharsets.UTF_8));
        buf.put((byte) '\0');

        // Put AES key and port into buf
        buf.position(65);
        buf.put((byte) 1);
        // buf.put((byte)0);
        buf.position(97);
        buf.putShort((short)ClientAPI.getFilePort());

        buf = encrypt.AESEncrypt(buf, aesKey);
        try {
            buf.rewind();
            socket.write(buf);
            // System.out.println("Requested file "+ fname + " from user " + uname +
            //                    " with key " +  (key == null) ? 
            //                                      ("empty") :
            //                                      (bytesToHex(aesKey.getEncoded()))
            //                    + " and expect results on port " + port);
        } catch (Exception e) {
            // TODO: handle exception
        }

        //Starts download GUI
        receiver = new ReceiverListener(ClientAPI.getFilePort(), null);
        receiver.start();
    }

    public synchronized void stopRunning() {
        running = false;
    }

    public void setPause(boolean p) {
        receiver.setPaused(p);
    }
}
