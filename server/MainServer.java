package server;

/**
 * Main method and only way to run a server.
 */
public class MainServer {
    public static void main(String[] args) {
        if (args.length > 1 || args.length < 1) {
            System.err.println("Incorrect number of arguments!\n" +
                    "Please provide port or type /help for more information.");
            System.exit(1);
        }
        if (args[0].equals("/help")) {
            System.out.println("Usage: java MainServer <port number>");
        }
        int port = Integer.parseInt(args[0]);
        Listener server = new Listener(port);
        System.out.println("Starting Server...");
        server.start();
        System.out.println("Server started successfully.");
    }
}
