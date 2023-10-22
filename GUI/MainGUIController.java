package GUI;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import api.ClientAPI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main GUI for program
 */
public class MainGUIController implements Initializable {
    Scene mainScene;
    Stage mainStage;

    @FXML
    Pane mainPane;
    @FXML
    ListView<String> UserList;

    @FXML
    TextArea mainTextArea;
    @FXML
    TextField mainTextField;
    @FXML
    Button mainSend;
    
    /**
     * Initializer
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
                    mainPane.getStylesheets().clear();
                    mainPane.getStylesheets().setAll(getClass().getResource("Styles\\1.css").toExternalForm());
                    break;
                //Spring Affair
                case 2:
                    mainPane.getStylesheets().clear();
                    mainPane.getStylesheets().setAll(getClass().getResource("Styles\\2.css").toExternalForm());
                    break;
                //Strawberry Lemon
                case 3:
                    mainPane.getStylesheets().clear();
                    mainPane.getStylesheets().setAll(getClass().getResource("Styles\\3.css").toExternalForm());
                    break;
                //PAGNOTA
                default:
                    mainPane.getStylesheets().clear();
                    mainPane.getStylesheets().setAll(getClass().getResource("Styles\\default.css").toExternalForm());
                    break;
            }
        });
    }
    /**
     * Sets scene
     * @param scene
     */
    public void setMainScene(Scene scene) {
        mainScene = scene;
    }

    /**
     * Shows Main GUI
     */
    public void Show() {
        Platform.runLater(() -> {
        mainStage = new Stage();
        mainStage.setScene(mainScene);
        Image logo = new Image("GUI/Styles/LOGO_P2P.png");
        mainStage.getIcons().add(logo);
        mainStage.show();
        });
    }

    /**
     * Button that closes program
     * @param e
     */
    public void Exit(ActionEvent e) {
        ClientAPI.closeProgram();
    }

    /**
     * Button that opens Search GUI
     * @param e
     */
    public void Search(ActionEvent e) {
        mainStage.close();
        ClientGUI.searchFileGUIController.Show();
    }

    /**
     * Button that sends a message to all other clients
     * @param e
     */
    public void Send(ActionEvent e) {
        String text = mainTextField.getText();
        mainTextField.clear();

        ClientAPI.SendText(text);
    }

    public void enterSend(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            String text = mainTextField.getText();
            mainTextField.clear();

            ClientAPI.SendText(text);
        }
    }

    /**
     * Fills Userlist with usernames
     * @param names
     */
    public void FillUsernames(List<String> names) {
        Platform.runLater(() -> {
            UserList.getItems().removeAll(UserList.getItems());
            UserList.getItems().addAll(names);
        });
    }

    /**
     * Prints text out to GUI
     * @param text
     */
    public void Print(String text) {
        Platform.runLater(() -> {
            mainTextArea.appendText(text + "\n");
        });
    }

    /**
     * Adds username to list
     * @param name
     */
    public void AddUsername(String name) {
        Platform.runLater(() -> {
            UserList.getItems().add(name);
        });
    }
}
