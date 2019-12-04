package domain;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageAlert {
    public static void showMessage(Stage owner, Alert.AlertType type, String header, String msg){
        Alert message =new Alert(type);
        message.setTitle(header);
        message.setContentText(msg);
        message.initOwner(owner);
        message.showAndWait();
    }

    public static Alert getConfirmationAlert(Stage owner,String header,String msg){
        Alert message =new Alert(Alert.AlertType.CONFIRMATION);
        message.setTitle(header);
        message.setContentText(msg);
        message.initOwner(owner);
        return message;
    }
}
