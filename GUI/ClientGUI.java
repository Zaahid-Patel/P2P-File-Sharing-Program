//Colour palettes obtained from https://www.color-hex.com/color-palettes/
package GUI;
import java.util.List;
import api.ClientAPI;
import api.FileData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Central GUI client that handles all other GUI controllers
 */
public class ClientGUI extends Application {
    public static StartGUIController startController;
    public static MainGUIController mainController;
    public static InvalidUsernameGUIController invalidUsernameController;
    public static SearchFileGUIController searchFileGUIController;
    public static DownloadFileGUIController downloadFileGUIController;
    public static UploadGUI uploadGUIController;

    /**
     * Initializer. Sends ClientGUI instance to the api
     */
    public ClientGUI() {
        ClientAPI.setGUI(this);
    }

    /**
     * Sets style of GUI
     * @param key key determining style
     */
    public void setStyle(int key) {

        startController.setStyle(key);
        mainController.setStyle(key);
        invalidUsernameController.setStyle(key);
        searchFileGUIController.setStyle(key);
        downloadFileGUIController.setStyle(key);
        uploadGUIController.setStyle(key);
    }

    /**
     * Launches the GUI
     * @param args
     */
    public static void launchGUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader startLoader = new FXMLLoader(getClass().getResource("StartGUI.fxml"));
            Parent startRoot = startLoader.load();
            Scene startScene = new Scene(startRoot);
            startController = startLoader.getController();
            startController.setStartScene(startScene);

            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("MainGUI.fxml"));
            Parent mainRoot = mainLoader.load();
            Scene mainScene = new Scene(mainRoot);
            mainController = mainLoader.getController();
            mainController.setMainScene(mainScene);

            FXMLLoader invalidLoader = new FXMLLoader(getClass().getResource("InvalidUsernameGUI.fxml"));
            Parent invalidRoot = invalidLoader.load();
            Scene invalidScene = new Scene(invalidRoot);
            invalidUsernameController = invalidLoader.getController();
            invalidUsernameController.setInvalidUsernameScene(invalidScene);

            FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("SearchFileGUI.fxml"));
            Parent searchRoot = searchLoader.load();
            Scene searchScene = new Scene(searchRoot);
            searchFileGUIController = searchLoader.getController();
            searchFileGUIController.setSearchFileScene(searchScene);

            FXMLLoader downloadLoader = new FXMLLoader(getClass().getResource("DownloadFileGUI.fxml"));
            Parent downloadRoot = downloadLoader.load();
            Scene downloadScene = new Scene(downloadRoot);
            downloadFileGUIController = downloadLoader.getController();
            downloadFileGUIController.setDownloadFileScene(downloadScene);

            FXMLLoader uploadLoader = new FXMLLoader(getClass().getResource("UploadGUI.fxml"));
            Parent uploadRoot = uploadLoader.load();
            Scene uploadScene = new Scene(uploadRoot);
            uploadGUIController = uploadLoader.getController();
            uploadGUIController.setUploadGUIScene(uploadScene);

            startController.Show();
            ClientAPI.setReady(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param username
     * @param host
     * @param sPort
     * @param fPort
     */
    public void setDefaultStartGUI(String username, String host, int sPort, int fPort) {
        startController.setDefaultStartGUI(username, host, sPort, fPort);
    }

    /**
     * Opens Main GUI
     */
    public void OpenMainMenu() {
        startController.OpenMainMenu();
    }

    /**
     * Print [text] to the main gui
     * @param text
     */
    public void Print(String text) {
        mainController.Print(text);
    } 

    /**
     * Opens the invalid GUI
     */
    public void InvalidUsername() {
        invalidUsernameController.Show();
    }

    /**
     * Updates filelist in File GUI
     * @param files
     */
    public void UpdateFileList(List<FileData> files) {
        searchFileGUIController.UpdateFileList(files);
    }

    /**
     * Opens download GUI
     * @param fileData data used for text
     */
    public void openDownload(FileData fileData) {
        downloadFileGUIController.Show(fileData);
    }

    /**
     * Sets download total
     * @param total amount
     */
    public void setDownloadTotal(double total) {
        downloadFileGUIController.setDownloadTotal(total);
    }

    /**
     * Updates download bar
     * @param current total downloaded so far
     */
    public void updateDownloadBar(double current) {
        downloadFileGUIController.updateDownloadBar(current);
    }

    /**
     * Opens Upload GUI
     * @param file
     */
    public void openUpload(FileData file) {
        uploadGUIController.Show(file);
    }

    /**
     * Sets upload total
     * @param total amount
     */
    public void setUploadTotal(double total) {
        uploadGUIController.setUploadTotal(total);
    }

    /**
     * Updates upload bar
     * @param current total amoun uploaded so far
     */
    public void updateUploadBar(double current) {
        uploadGUIController.updateUploadBar(current);
    }

    /**
     * Fills list with username on main gui
     * @param names list of names
     */
    public void FillUsernames(List<String> names) {
        mainController.FillUsernames(names);
    }

    /**
     * Adds a single username to GUI
     * @param name name to add
     */
    public void AddUsername(String name) {
        mainController.AddUsername(name);
    }

    /**
     * Changes the upload text
     * @param text new text
     */
    public void changeUploadText(String text) {
        uploadGUIController.changeUploadText(text);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
