javac api/FileData.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml client/ServerSignaller.java

javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml client/encrypt.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml client/MainEncrypt.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml client/Receiver.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml client/ReceiverListener.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml client/Sender.java

javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/InvalidUsernameGUIController.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/MainGUIController.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/StartGUIController.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/SearchFileGUIController.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/DownloadFileGUIController.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/UploadGUI.java

javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml GUI/ClientGUI.java

javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml api/ClientAPI.java
javac --module-path C:\\javafx-sdk-20.0.1\\lib --add-modules javafx.controls,javafx.fxml api/RunClientGUI.java