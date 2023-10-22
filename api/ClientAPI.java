package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import GUI.ClientGUI;
import client.MainEncrypt;
import client.ServerSignaller;
//TODO: import client here
import temp_Server.SearchFile;

/**
 * API for between Client and GUI
 */
public class ClientAPI {
    static ClientGUI clientGUI;
    static RunClientGUI runClientGUI;
    //Only used for testing encryption
    static MainEncrypt _encrypt;
    static ServerSignaller signaller;

    static boolean ready = false;
    private static boolean dev = false;
    static List<FileData> data = new ArrayList<FileData>();
    static String clientUsername;
    static String hostname;
    static int serverPort;
    static int filePort;

    //TODO: add client as static here
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("dev")) {
                System.out.println("gui dev mode");
                dev = true;
            }
        }
        runClientGUI = new RunClientGUI(args);
        runClientGUI.start();
        while (!getReady()) {
            //Wait for clientGUI
            // System.out.print("w");
        }
        System.out.println("Program ready");
        Print("Type /help for commands");
        try {
            String[] address = InetAddress.getLocalHost().toString().split("/");
            setDefaultStartGUI("/true", address[1], 9999, 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //TESTING
        if (dev) {
            _encrypt = new MainEncrypt();
            _encrypt.start();

            FileData item = new FileData(1, "bob", "homework");
            data.add(item);

            item = new FileData(2, "bob", "hi");
            data.add(item);

            item = new FileData(3, "john", "he");
            data.add(item);
            updateFileList(data);

            List<String> names = new ArrayList<String>();
            names.add("bob");
            FillUsernames(names);

            System.out.println("debug commands:");
            System.out.println("/addUser:[username]      --Adds username to main gui");
            System.out.println("/upload                  --Opens upload gui");
            System.out.println("/dBar:[amount]           --Sets download bar to [amount] out of 100");
            System.out.println("/uBar:[amount]           --Sets upload bar to [amount] out of 100");
            System.out.println("");
            System.out.println("//startReceiver:[port]                            --Starts receiver listening for sender on [port]");
            System.out.println("//startSender:[hostname];;[port];;[publicKey]     --Starts sender connected to client [hostname] on [port], encrypting data using [publicKey]");
            System.out.println("//r                                               --[Shortcut] Starts receiver listening for sender on 9999");
            System.out.println("//s                                               --[Shortcut] Starts sender connected to client localhost on 9999");
            System.out.println("");

            //Reads input from terminal
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String commands;
                while ((commands = reader.readLine()) != null) {

                    if (commands.contains("/addUser:")) {
                        String user = commands.substring(9);
                        System.out.println("Added: " + user);
                        AddUsername(user);
                    }

                    if (commands.contains("/upload")) {
                        changeUploadText("bob", "meep");
                        FileData data = new FileData(0, "randomGuy", "meep");
                        uploadFile(data);
                    }

                    if (commands.contains("/dBar:")) {
                        double current = Double.parseDouble(commands.substring(6));
                        setDownloadTotal(100);

                        updateDownloadBar(current);
                    }

                    if (commands.contains("/uBar:")) {
                        double current = Double.parseDouble(commands.substring(6));
                        setUploadTotal(100);

                        updateUploadBar(current);
                    }

                    if (commands.contains("//startReceiver:")) {
                        int port = Integer.parseInt(commands.substring(16));
                        _startReceiver(port);
                    }

                    if (commands.contains("//startSender:")) {
                        String info = commands.substring(14);
                        String[] split = info.split(";;");

                        String hostname = split[0];
                        int port = Integer.parseInt(split[1]);
                        PublicKey publicKey = null;
                        try {
                            byte[] publicKeyBytes = Base64.getDecoder().decode(split[2]);
                            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                            publicKey = keyFactory.generatePublic(keySpec);

                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                        

                        _startSender(hostname, port, publicKey);
                    }

                    if (commands.equals("//r")) {
                        int port = 9999;
                        _startReceiver(port);
                    }

                    if (commands.equals("//s")) {
                        String hostname = InetAddress.getLocalHost().toString().split("/")[1];
                        System.out.println(hostname);
                        int port = 9999;
                        _startSender(hostname, port);
                    }
                    
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

    /**
     * Sets the hostname and port for connecting to the server
     * @param host Server hostname
     * @param port Server port
     */
    public static void setHostnameAndPort(String host, int port) {
        hostname = host;
        serverPort = port;

        signaller = new ServerSignaller(hostname, serverPort, "");
        signaller.start();
    }

    /**
     * Sets client port for file
     * @param port
     */
    public static void setFilePort(int port) {
        filePort = port;
    }

    /**
     * Returns Server's hostname
     * @return hostname
     */
    public static String getServerHostname() {
        return hostname;
    }

    /**
     * Returns Server's port
     * @return serverPort
     */
    public static int getServerPort() {
        return serverPort;
    }

    /**
     * Returns Client's file port
     * @return filePort
     */
    public static int getFilePort() {
        return filePort;
    }

    /**
     * Sets default values for start GUI
     * @param username default username
     * @param host default server hostname
     * @param sPort default server port
     * @param fPort default client file port
     */
    public static void setDefaultStartGUI(String username, String host, int sPort, int fPort) {
        clientGUI.setDefaultStartGUI(username, host, sPort, fPort);
    }

    /**
     * Sets ready variable, used for waiting for gui to complete
     * @param r setting ready
     */
    public static void setReady(boolean r) {
        ready = r;
    }

    /**
     * Returns ready varaible
     * @return ready
     */
    public static synchronized boolean getReady() {
        return ready;
    }

    /**
     * Properly closes all threads
     */
    public static void closeProgram() {
        //TODO stop client thread

        System.out.println("Exit");
        System.exit(0);
    }

    /**
     * Sets client GUI varaible
     * @param gui the GUI parameter
     */
    public static void setGUI(ClientGUI gui) {
        clientGUI = gui;
    }

    /**
     * Submits username to client
     * @param username username to submit
     */
    public static void submitUsername(String username) {
        if (dev) {
            if (username.equals("/true")) {
                validUsername(true);
            } else if (username.equals("/false")) {
                validUsername(false);
            }
        } else {
            //TODO: Add client
            while (!signaller.getConnected()) {}
            signaller.sendUserName(username);
        }
        clientUsername = username;
    }

    /**
     * Client determines if username was valid, triggers this to open appropriate GUI
     * @param valid True if username was valid, false otherwise
     */
    public static void validUsername(boolean valid) {
        if (valid) {
            System.out.println("valid username");
            clientGUI.OpenMainMenu();

        } else {
            System.out.println("invalid username");
            clientGUI.InvalidUsername();
            signaller.stopRunning();
        }
    }

    /**
     * Fills usernames onto list on main gui (Erases old usernames)
     * @param names list of names
     */
    public static void FillUsernames(List<String> names) {
        clientGUI.FillUsernames(names);
    }

    /**
     * Adds username onto list on main gui (Keeps old usernames)
     * @param name
     */
    public static void AddUsername(String name) {
        clientGUI.AddUsername(name);
    }

    /**
     * Sends text message to other user + accepts commands
     * @param text
     */
    public static void SendText(String text) {
        System.out.println("Send: " + text);
        if (text.contains("/")) {
            if (text.contains("/setStyle:")) {
                int key = Integer.parseInt(text.substring(10));
                clientGUI.setStyle(key);
                switch (key) {
                    case 1:
                        //bloody rose
                        Print("[STYLE SET]: bloody rose");
                        break;

                    case 2:
                        //Spring Affair
                        Print("[STYLE SET]: Spring Affair");
                        break;

                    case 3:
                        //Strawberry Lemon
                        Print("[STYLE SET]: Strawberry Lemon");
                        break;
                
                    default:
                        //PAGNOTA
                        Print("[STYLE SET]: PAGNOTA");
                        break;
                }
            } else if (text.contains("/listStyles") || text.contains("/ls")) {
                Print("[STYLES]");
                Print("default (0):   PAGNOTA");
                Print("1:             bloody rose");
                Print("2:             Spring Affair");
                Print("3:             Strawberry Lemon");
                Print("");
            } else if (text.contains("/help") || text.contains("/h")) {
                Print("[COMMANDS]");
                Print("(/help) || (/h)        Lists commands");
                Print("(/listStyles) || (/ls) List GUI styles");
                Print("(/setStyle:[key])      Sets GUI style");
                Print("(/askRTM:[question])   Ask me any question, and I shall answer!");
                Print("");
            } else if (text.contains("/askRTM:")) {
                String question = text.substring(8);
                Print("[askRTM]");
                Print("As a Random text Model, I cannot guarantee the validity of my answers");
                Print("However I can say for certain that the answer to this question:");
                Print("-" + question);
                Print("is");
                if (question.equals("") || !question.contains("?")) {
                    Print("...nothing. You did not ask anything.");
                } else {
                    Double random = Math.random();
                    System.out.println(random);
                    if (random > 0 && random < 0.5) {
                        Print("42");
                    } else if (random > 0.5 && random < 1) {
                        Print("...idk :)");
                    }
                }
                Print("");

            }
        } else {
            //TODO: add client
            Print( "[" + clientUsername + "] : " + text);
        }
    }

    /**
     * Prints out message to main gui
     * @param text
     */
    public static void Print(String text) {
        clientGUI.Print(text);
    }

    /**
     * Function asking for files to be filtered according to given term
     * @param item Term given
     */
    public static void searchFile(String item) {
        if (dev) {
            List<FileData> newData = SearchFile.searchFile(data, item);
            updateFileList(newData);
            
        } else {
            signaller.sendSearch(item);
        }
    }

    /**
     * Updates list of files shown (Erases old files on list)
     * @param files list of file datas
     */
    public static void updateFileList(List<FileData> files) {
        clientGUI.UpdateFileList(files);
    }

    /**
     * Command to download spesific file
     * @param fileData
     */
    public static void downloadFile(FileData fileData) {
        if (dev) {
            clientGUI.openDownload(fileData);
        } else {
            signaller.requestFile(fileData.getUsername(), fileData.getFileName());
            clientGUI.openDownload(fileData);
        }
    }

    /**
     * Sets total size of file expected to be downloaded
     * Used for download's loading bar
     * @param total Total amount needed
     */
    public static void setDownloadTotal(double total) {
        System.out.println("total: " + total);
        clientGUI.setDownloadTotal(total);
    }

    /**
     * Updates amount that has been downloaded
     * Used for download's loading bar
     * @param current amount total downloaded
     */
    public static void updateDownloadBar(double current) {
        clientGUI.updateDownloadBar(current);
    }

    /**
     * Command to open upload GUI with spesific file data
     * @param fileData file data of file being uploaded
     */
    public static void uploadFile(FileData fileData) {
        if (dev) {
            clientGUI.openUpload(fileData);
        } else {
            clientGUI.openUpload(fileData);
            
        }
    }

    /**
     * Sets total size of file to be uploaded
     * @param total
     */
    public static void setUploadTotal(double total) {
        clientGUI.setUploadTotal(total);
    }

    /**
     * Updates amount that has been uploaded
     * @param current
     */
    public static void updateUploadBar(double current) {
        clientGUI.updateUploadBar(current);
    }

    /**
     * Command to pause download.
     * Downloading client should send command through server to uploading client to pause uploading
     * @param fileData
     */
    public static void pauseDownload(FileData fileData) {
        System.out.println("pause download: " + fileData.getID());
        if (dev) {
            _encrypt.setPaused(true);
        } else {
            signaller.setPause(true);
        }
    }

    /**
     * Command to resume download.
     * Downloading client should send command through server to uploading client to resume uploading
     * @param fileData
     */
    public static void resumeDownload(FileData fileData) {
        System.out.println("resume download: " + fileData.getID());
        if (dev) {
            _encrypt.setPaused(false);
        } else {
            signaller.setPause(false);
        }
    }

    /**
     * Command to cancel download
     * @param fileData
     */
    public static void cancelDownload(FileData fileData) {
        System.out.println("Cancel download: " + fileData.getID());
    }

    /**
     * By default upload is paused. acceptUpload sets pause to false
     */
    public static void acceptUpload() {
        System.out.println("Accept Upload");
        if (dev) {
            _encrypt.setPaused(false);
        } else {
            signaller.setPause(false);
        }
    }

    /**
     * Cancels upload
     */
    public static void cancelUpload() {
        System.out.println("Cancel Upload");
    }

    /**
     * Command to change upload text
     * @param username username of client file is being sent
     * @param file filename
     */
    public static void changeUploadText(String username, String file) {
        clientGUI.changeUploadText(file + " upload to " + username);
    }

    /**
     * [DEBUGGER COMMAND] Starts a client file receiver on port
     * @param port port 
     */
    public static void _startReceiver(int port) {
        _encrypt.startReceiver(port);
    }

    /**
     * [DEBUGGER COMMAND] Starts a client file sender connecting to [hostname] + [port]
     * [NOTE] Currently only works localhost
     * @param hostname uploading client's hostname
     * @param port uploading client's port
     */
    public static void _startSender(String hostname, int port) {
        _encrypt.startSender(hostname, port);
    }

    public static void _startSender(String hostname, int port, PublicKey publicKey) {
        _encrypt.startSender(hostname, port, publicKey);
    }
}
