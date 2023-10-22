package GUI;

import java.net.URL;
import java.util.ResourceBundle;
import api.ClientAPI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * GUI for when an invalid username was submitted
 */
public class InvalidUsernameGUIController implements Initializable {
    Scene invalidUsernameScene;
    Stage invalidUsernameStage;

    @FXML
    SplitPane invalidUsername;
     
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
                    invalidUsername.getStylesheets().clear();
                    invalidUsername.getStylesheets().setAll(getClass().getResource("Styles\\1.css").toExternalForm());
                    break;
                //Spring Affair
                case 2:
                    invalidUsername.getStylesheets().clear();
                    invalidUsername.getStylesheets().setAll(getClass().getResource("Styles\\2.css").toExternalForm());
                    break;
                //Strawberry Lemon
                case 3:
                    invalidUsername.getStylesheets().clear();
                    invalidUsername.getStylesheets().setAll(getClass().getResource("Styles\\3.css").toExternalForm());
                    break;
                //PAGNOTA
                default:
                    invalidUsername.getStylesheets().clear();
                    invalidUsername.getStylesheets().setAll(getClass().getResource("Styles\\default.css").toExternalForm());
                    break;
            }
        });
    }

    /**
     * Sets scene
     * @param scene
     */
    public void setInvalidUsernameScene(Scene scene) {
        invalidUsernameScene = scene;
    }

    /**
     * Shows Invalid username GUI
     */
    public void Show() {
        System.out.println("GUI:Invalid Username");
        Platform.runLater(() -> {
            invalidUsernameStage = new Stage();
            invalidUsernameStage.setScene(invalidUsernameScene);
            Image logo = new Image("GUI/Styles/LOGO_P2P.png");
            invalidUsernameStage.getIcons().add(logo);
            invalidUsernameStage.show();
        }); 
    }

    /**
     * Button causes api to close program
     * @param e
     */
    public void Exit(ActionEvent e) {
        ClientAPI.closeProgram();
    }

    /**
     * Button to return to start GUI
     * @param e
     */
    public void TryUsenameAgain(ActionEvent e) {
        try {
            ClientGUI.startController.startGUIPlane.setDisable(false);
            invalidUsernameStage.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
