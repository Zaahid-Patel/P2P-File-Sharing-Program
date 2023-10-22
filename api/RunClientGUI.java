package api;

import GUI.ClientGUI;

/**
 * Unique thread for running the client's GUI seperately.
 */
public class RunClientGUI extends Thread {
    String[] args;
    /**
     * Initializer
     * @param args
     */
    public RunClientGUI(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        ClientGUI.launchGUI(args);

        ClientAPI.closeProgram();
    }
}
