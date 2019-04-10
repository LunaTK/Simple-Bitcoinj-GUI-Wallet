import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    ImageView coinImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("hi");
    }

    @FXML
    private void printLog(ActionEvent event) {
        System.out.println("printLog");
    }
}
