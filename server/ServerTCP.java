package server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread that implements TCP connection for server
 */
public class ServerTCP extends Thread {
    SocketChannel socket;
    boolean running = true;
    String username;
    String sendUser;
    List<ServerTCP> connections = new ArrayList<ServerTCP>();

    // Constructor method
    public ServerTCP(SocketChannel s, List<ServerTCP> c) {
        socket = s;
        connections = c;
    }

    @Override
    public void run() {
        try {
            socket.configureBlocking(true);
            while (!socket.finishConnect()) {
            }
            while (running) {
                // Read Buffer Bytes
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = socket.read(buffer);
                // buffer = encrypt.AESDecrypt(buffer, null);
                int code = buffer.get(0);
                System.out.println(code);
                char next;
                int index;
                String filename = "";
                String key = "";
                short numFiles;
                short port;
                String[] fileList;
                String fileOwner;
                String chosenFile;

                if (bytesRead == -1) {
                    running = false;
                    socket.close();
                }

                if (bytesRead > 0) {
                    switch (code) {

                        case 1:

                            buffer.position(1);
                            for (int i = 0; i < 32; i++) {
                                next = (char) buffer.get();
                                key += next;
                            }
                            break;

                        case 2:

                            buffer.position(1);
                            username = "";
                            do {
                                next = (char) buffer.get();
                                username += next;
                            } while (next != '\0');
                            usernameAck(username, this);
                            break;

                        case 4:
                            // get code 4 send code 5 to all other users
                            buffer.position(1);
                            do {
                                next = (char) buffer.get();
                                filename += next;
                            } while (next != '\0');
                            System.out.println("Got search request for " + filename);
                            for (int i = 0; i < connections.size(); i++) {
                                if (!connections.get(i).getUsername().equals(username)) {
                                    connections.get(i).searchRequest(filename, username);
                                }
                            }
                            break;

                        case 6:
                            // get code 6 send code 7
                            buffer.position(1);
                            sendUser = "";
                            do {
                                next = (char) buffer.get();
                                sendUser += next;
                            } while (next != '\0');
                            buffer.position(33);
                            numFiles = buffer.getShort();
                            fileList = new String[numFiles];
                            for (int i = 0; i < numFiles; i++) {
                                buffer.position(35 + 32 * i);
                                filename = "";
                                do {
                                    next = (char) buffer.get();
                                    filename += next;
                                } while (next != '\0');
                                fileList[i] = filename;
                            }
                            for (int i = 0; i < connections.size(); i++) {
                                if (connections.get(i).getUsername().equals(sendUser)) {
                                    connections.get(i).sendFileList(numFiles, username, fileList);
                                }
                            }
                            break;

                        case 8:

                            buffer.position(1);
                            fileOwner = "";
                            do {
                                next = (char) buffer.get();
                                fileOwner += next;
                            } while (next != '\0');
                            chosenFile = "";
                            buffer.position(33);
                            do {
                                next = (char) buffer.get();
                                chosenFile += next;
                            } while (next != '\0');
                            buffer.position(65);
                            for (int i = 0; i < 32; i++) {
                                next = (char) buffer.get();
                                key += next;
                            }
                            buffer.position(97);
                            port = (short) buffer.getShort();
                            
                            for (int i = 0; i < connections.size(); i++) {
                                if (connections.get(i).getUsername().equals(fileOwner)) {
                                    InetAddress IP = ((InetSocketAddress) socket.getRemoteAddress()).getAddress();
                                    connections.get(i).fileRequest(chosenFile, key, port, IP);
                                }
                            }

                            

                            break;

                        default:
                            System.err.println("Incorrect code in byte buffer!");
                            break;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if username is unique and sends an acknowledgement if it's accepted or
     * not. Returns 0 if username is found and 1 otherwise
     * 
     * @param uname Username
     */
    public void usernameAck(String uname, ServerTCP askingThread) {
        byte ack;
        boolean contains = false;

        for (int i = 0; i < connections.size(); i++) {
            if (uname.equals(connections.get(i).getUsername()) && !connections.get(i).equals(askingThread)) {
                contains = true;
            }
        }
        if (contains) {
            ack = 1;
        } else {
            username = uname;
            ack = 0;
        }

        ByteBuffer data = ByteBuffer.allocate(2);
        data.position(0);
        data.put((byte) 3);
        data.position(1);
        data.put(ack);
        data.rewind();
        System.out.println("Server sent ack:" + ack + " username is :" + uname);

// Automatic merge failed; fix conflicts and then commit the result.
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Searches from the list of files for the file requested.
     * This is updated upon each character inputted
     * Pretty cool
     * @param fileName Filename
     * @param username Username
     */
    public void searchRequest(String fileName, String username) {
        System.out.println("got request");
        ByteBuffer data = ByteBuffer.allocate(65);
        data.position(0);
        data.put((byte) 5);
        data.position(1);
        data.put(fileName.getBytes());
        data.put((byte) '\0');
        data.position(33);
        data.put(username.getBytes());
        data.put((byte) '\0');
        data.rewind();
        System.out.println("filename sent is :" + fileName + " username sent is :" + username);

        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a list of files available for download to the client
     * 
     * @param fileName Filename
     * @param username Username
     */
    public void sendFileList(short numFiles, String uname, String[] fileList) {

        String filename;

        ByteBuffer data = ByteBuffer.allocate(1024);
        data.position(0);
        data.put((byte) 7);
        data.position(1);
        data.putShort(numFiles);
        data.position(3);
        data.put(uname.getBytes());
        data.put((byte) '\0');
        for (int i = 0; i < numFiles; i++) {
            data.position(35 + 32 * i);
            filename = fileList[i];
            data.put(filename.getBytes());
            data.put((byte) '\0');
        }
        data.rewind();
        System.out.println(
                "numfiles sent is :" + numFiles + " uname sent is :" + uname);
        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Requests the client for a specicfic file
     * 
     * @param fileName Filename
     * @param username Username
     */
    public void fileRequest(String filename, String key, short port, InetAddress IP) {

        ByteBuffer data = ByteBuffer.allocate(71);
        data.position(0);
        data.put((byte) 9);
        data.put(filename.getBytes());
        data.put((byte) '\0');
        data.position(33);
        data.put(key.getBytes());
        data.position(65);
        data.putShort(port);
        data.put(IP.getAddress());
        data.rewind();
        System.out.println("filename sent is :" + filename + " key sent is :" + key + " port sent is :" + port
                + " IP sent is :" + IP);

        try {
            socket.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets the Username of the client
     * 
     * @param fileName Filename
     * @param username Username
     */
    public synchronized void setUsername(String s) {
        this.username = s;
    }

    /**
     * Gets the Username from the client
     * 
     * @param fileName Filename
     * @param username Username
     */
    public synchronized String getUsername() {
        return this.username;
    }
}

