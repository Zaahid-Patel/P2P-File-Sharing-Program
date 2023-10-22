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
 * 
 */
public class DownloadFileGUIController implements Initializable {
    Scene downloadFileScene;
    Stage downloadFileStage;
    FileData fileData;
    double downloadTotal;

    @FXML
    Pane downloadFileGUI;
    @FXML
    ProgressBar downloadBar;
    @FXML
    Label downloadText;
    @FXML
    Button downloadPause;
    @FXML
    Button downloadPlay;
    @FXML
    Button downloadCancel;
    
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
                    downloadFileGUI.getStylesheets().clear();
                    downloadFileGUI.getStylesheets().setAll(getClass().getResource("Styles\\1.css").toExternalForm());
                    break;
                //Spring Affair
                case 2:
                    downloadFileGUI.getStylesheets().clear();
                    downloadFileGUI.getStylesheets().setAll(getClass().getResource("Styles\\2.css").toExternalForm());
                    break;
                //Strawberry Lemon
                case 3:
                    downloadFileGUI.getStylesheets().clear();
                    downloadFileGUI.getStylesheets().setAll(getClass().getResource("Styles\\3.css").toExternalForm());
                    break;
                //PAGNOTA
                default:
                    downloadFileGUI.getStylesheets().clear();
                    downloadFileGUI.getStylesheets().setAll(getClass().getResource("Styles\\default.css").toExternalForm());
                    break;
            }
        });
    }

    /**
     * Sets download gui scene
     * @param scene scene
     */
    public void setDownloadFileScene(Scene scene) {
        downloadFileScene = scene;
    }

    /**
     * Shows File download GUI
     * @param fileData data used to infuence text
     */
    public void Show(FileData fileData) {
        Platform.runLater(() -> {
        this.fileData = fileData;

        downloadFileStage = new Stage();
        downloadFileStage.setScene(downloadFileScene);
        Image logo = new Image("GUI/Styles/LOGO_P2P.png");
        downloadFileStage.getIcons().add(logo);
        downloadFileStage.show();

        changeDownloadText(fileData.getFileName() + " downloading from " + fileData.getUsername());
        downloadBar.progressProperty().set(0);
        });
    }

    /**
     * Button triggers client to pause download
     * @param e
     */
    public void pauseDownload(ActionEvent e) {
        ClientAPI.pauseDownload(fileData);
    }

    /**
     * Button triggers client to resume download
     * @param e
     */
    public void resumeDownload(ActionEvent e) {
        ClientAPI.resumeDownload(fileData);
    }

    /**
     * Button triggers client to cancel download and close download GUI
     * @param e
     */
    public void cancelDownload(ActionEvent e) {
        ClientAPI.cancelDownload(fileData);
        ClientGUI.searchFileGUIController.downloadPane.setDisable(false);
        downloadFileStage.close();
    }

    /**
     * Changes download GUI's text
     * @param text
     */
    public void changeDownloadText(String text) {
        Platform.runLater(() -> {
            System.out.println("GUI: update text: " + text);
            downloadText.setText(text);
        });
    }

    /**
     * Sets File total size
     * @param total
     */
    public void setDownloadTotal(double total) {
        downloadTotal = total;
    }

    /**
     * Updates download bar
     * @param current amount downloaded
     */
    public void updateDownloadBar(double current) {
        Platform.runLater(()->{
            if (current <= downloadTotal) {
                if (fileData != null) {
                    // System.out.println(fileData.getFileName() + " download progress: " + ((current / downloadTotal) * 100) + " %");
                }
                
                downloadBar.progressProperty().set(current / downloadTotal);
            } else {
                downloadBar.progressProperty().set(downloadTotal / downloadTotal);
            }
        });
    }
}
