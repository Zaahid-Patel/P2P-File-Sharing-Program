package GUI;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import api.ClientAPI;
import api.FileData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SearchFileGUIController implements Initializable {
    Scene SearchFileScene;
    Stage seachFileStage;

    @FXML
    Pane downloadPane;
    @FXML
    TableView<FileData> fileList;
    @FXML
    TableColumn<FileData, String> userColumn;
    @FXML
    TableColumn<FileData, String> fileNameColumn;

    @FXML
    TextField searchFile;
    
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
                    downloadPane.getStylesheets().clear();
                    downloadPane.getStylesheets().setAll(getClass().getResource("Styles\\1.css").toExternalForm());
                    break;
                //Spring Affair
                case 2:
                    downloadPane.getStylesheets().clear();
                    downloadPane.getStylesheets().setAll(getClass().getResource("Styles\\2.css").toExternalForm());
                    break;
                //Strawberry Lemon
                case 3:
                    downloadPane.getStylesheets().clear();
                    downloadPane.getStylesheets().setAll(getClass().getResource("Styles\\3.css").toExternalForm());
                    break;
                //PAGNOTA
                default:
                    downloadPane.getStylesheets().clear();
                    downloadPane.getStylesheets().setAll(getClass().getResource("Styles\\default.css").toExternalForm());
                    break;
            }
        });
    }

    /**
     * Sets scene
     * @param scene scene
     */
    public void setSearchFileScene(Scene scene) {
        SearchFileScene = scene;
    }

    /**
     * Shows Search file gui
     */
    public void Show() {
        Platform.runLater(() -> {
        System.out.println("Search GUI");
        seachFileStage = new Stage();
        seachFileStage.setScene(SearchFileScene);
        Image logo = new Image("GUI/Styles/LOGO_P2P.png");
        seachFileStage.getIcons().add(logo);
        seachFileStage.show();
        });
    }

    /**
     * Updates file list
     * @param files list of file data
     */
    public void UpdateFileList(List<FileData> files) {
        fileList.getItems().removeAll(fileList.getItems());

        fileList.getItems().addAll(files);

        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
    }

    /**
     * Button to filter files
     * @param e
     */
    public void SearchFile(KeyEvent e) {
        String searchFilename = searchFile.getText();

        ClientAPI.searchFile(searchFilename);
    }

    /**
     * Button to go back to Main GUI
     * @param e
     */
    public void BackToMain(ActionEvent e) {
        ClientGUI.mainController.Show();
        
        seachFileStage.close();
    }

    /**
     * Button to open Download File GUI
     * @param e
     */
    public void DownloadFile(ActionEvent e) {
        FileData currentlySelectedFile = fileList.getSelectionModel().getSelectedItem();   

        if (currentlySelectedFile != null) {
            downloadPane.setDisable(true);
            System.out.println(currentlySelectedFile.getFileName());
            ClientAPI.downloadFile(currentlySelectedFile);
        } else {
            System.out.println("null");
        }  
    }
}
