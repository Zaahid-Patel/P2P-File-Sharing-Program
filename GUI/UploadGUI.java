package GUI;

import java.net.URL;
import java.util.ResourceBundle;
import api.ClientAPI;
import api.FileData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Upload GUI
 */
public class UploadGUI implements Initializable {
    Scene uploadGUIScene;
    Stage uploadGUIStage;
    FileData file;
    double uploadTotal;

    @FXML
    Pane uploadPane;
    @FXML
    Label uploadText;
    @FXML
    ProgressBar uploadBar;
    @FXML
    Button uploadAccept;
    @FXML
    Button uploadCancel;
    
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
                    uploadPane.getStylesheets().clear();
                    uploadPane.getStylesheets().setAll(getClass().getResource("Styles\\1.css").toExternalForm());
                    break;
                //Spring Affair
                case 2:
                    uploadPane.getStylesheets().clear();
                    uploadPane.getStylesheets().setAll(getClass().getResource("Styles\\2.css").toExternalForm());
                    break;
                //Strawberry Lemon
                case 3:
                    uploadPane.getStylesheets().clear();
                    uploadPane.getStylesheets().setAll(getClass().getResource("Styles\\3.css").toExternalForm());
                    break;
                //PAGNOTA
                default:
                    uploadPane.getStylesheets().clear();
                    uploadPane.getStylesheets().setAll(getClass().getResource("Styles\\default.css").toExternalForm());
                    break;
            }
        });
    }
    /**
     * Sets Scene
     * @param scene
     */
    public void setUploadGUIScene(Scene scene) {
        uploadGUIScene = scene;
    }

    /**
     * Changes upload GUI's text
     * @param text
     */
    public void changeUploadText(String text) {
        Platform.runLater(() -> {
            System.out.println("GUI: update text: " + text);
            uploadText.setText(text);
        });
    }

    /**
     * Shows upload gui
     * @param file important data
     */
    public void Show(FileData file) {
        Platform.runLater(() -> {
            System.out.println("Upload GUI");

            uploadGUIStage = new Stage();
            uploadGUIStage.setScene(uploadGUIScene);
            Image logo = new Image("GUI/Styles/LOGO_P2P.png");
            uploadGUIStage.getIcons().add(logo);
            uploadGUIStage.show();
            uploadAccept.setDisable(false);
            
            this.file = file;
            uploadBar.progressProperty().set(0);
        });
    }

    /**
     * Button that allows uploading to start
     * @param e
     */
    public void acceptUpload(ActionEvent e) {
        ClientAPI.acceptUpload();
        uploadAccept.setDisable(true);
    }

    /**
     * Button that cancels upload
     * @param e
     */
    public void cancelUpload(ActionEvent e) {
        ClientAPI.cancelUpload();
        uploadGUIStage.close();
    }

    /**
     * Sets upload total
     * @param total
     */
    public void setUploadTotal(double total) {
        uploadTotal = total;
    }

    /**
     * Updates upload bar with amount downloaded
     * @param current total amount downloaded
     */
    public void updateUploadBar(double current) {
        Platform.runLater(()->{
            if (current <= uploadTotal) {
                if (file != null) {
                    // System.out.println(file.getFileName() + " upload progress: " + ((current / uploadTotal) * 100) + " %");
                }
                
                uploadBar.progressProperty().set(current / uploadTotal);
            } else {
                uploadBar.progressProperty().set(uploadTotal/current);
            }
        });
    }
}
