package GUI;

import java.net.URL;
import java.util.ResourceBundle;
import api.ClientAPI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartGUIController implements Initializable {
    Scene startScene;
    Stage startStage;
    
    @FXML
    public SplitPane startGUIPlane;
    @FXML
    private Button submitButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField hostnameField;
    @FXML
    private TextField serverPortField;
    @FXML
    private TextField filePortField;

    /**
     * Initialize
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {}

     /**
     * Sets style of GUI
     * @param key determines style
     */
    public void setStyle(int key) {
        Platform.runLater(() -> {
            switch (key) {
                //bloody rose
                case 1:
                    startGUIPlane.getStylesheets().clear();
                    startGUIPlane.getStylesheets().setAll(getClass().getResource("Styles\\1.css").toExternalForm());
                    System.out.println("11111111");
                    break;
                //Spring Affair
                case 2:
                    startGUIPlane.getStylesheets().clear();
                    startGUIPlane.getStylesheets().setAll(getClass().getResource("Styles\\2.css").toExternalForm());
                    break;
                //Strawberry Lemon
                case 3:
                    startGUIPlane.getStylesheets().clear();
                    startGUIPlane.getStylesheets().setAll(getClass().getResource("Styles\\3.css").toExternalForm());
                    break;
                //PAGNOTA
                default:
                    startGUIPlane.getStylesheets().clear();
                    startGUIPlane.getStylesheets().setAll(getClass().getResource("Styles\\default.css").toExternalForm());
                    break;
            }
        });
    }

    /**
     * Sets scene
     * @param scene
     */
    public void setStartScene(Scene scene) {
        startScene = scene;
    }

    /**
     * Sets default values
     * @param username default username
     * @param host default server hostname
     * @param sPort default server port
     * @param fPort default client file port
     */
    public void setDefaultStartGUI(String username, String host, int sPort, int fPort) {
        Platform.runLater(() -> {
            nameField.setText(username);
            hostnameField.setText(host);
            serverPortField.setText(String.valueOf(sPort));
            filePortField.setText(String.valueOf(fPort));
        });
    }

    /**
     * Shows Start GUI
     */
    public void Show() {
        Platform.runLater(() -> {
        startStage = new Stage();
        startStage.setScene(startScene);
        Image logo = new Image("GUI/Styles/LOGO_P2P.png");
        startStage.getIcons().add(logo);
        startStage.show();
        });
    }

    /**
     * Opens Main GUI
     */
    public void OpenMainMenu() {
        Platform.runLater(() -> {
        try {
            startStage.close();
            ClientGUI.mainController.Show();
            
        } catch (Exception error) {
            error.printStackTrace();
        }
    });
    }

    /**
     * Button that exits program
     * @param e
     */
    public void Exit(ActionEvent e) {
        ClientAPI.closeProgram();
    }

    /**
     * Button that submits info for review
     * @param e
     */
    public void SubmitName(ActionEvent e) {
        String username = nameField.getText();
        String hostname = hostnameField.getText();
        int hostPort = Integer.parseInt(serverPortField.getText());
        int filePort = Integer.parseInt(filePortField.getText());
        System.out.println("Submit Name: " + username);
        nameField.clear();
        System.out.println(hostPort);
        ClientAPI.setHostnameAndPort(hostname, hostPort);
        ClientAPI.setFilePort(filePort);
        ClientAPI.submitUsername(username);

        startGUIPlane.setDisable(true);
    }
}
