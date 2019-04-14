package mywallet;

import java.io.IOException;
import java.net.URLEncoder;

import com.jfoenix.controls.JFXTextField;

import org.bitcoinj.core.Address;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mywallet.helper.QRRenderer;

public class RequestBitcoinController {
    @FXML
    JFXTextField tfAmount;
    @FXML
    JFXTextField tfLabel;
    @FXML
    JFXTextField tfMessage;
    @FXML
    ImageView qrImage;

    public static void show(Class<?> kClass) {
        Parent root;
        try {
            root = FXMLLoader.load(kClass.getResource("fxml/request_bitcoin.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Request Bitcoin");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onGenerate(ActionEvent event) {
        try {
            Address address = DashboardController.getKit().wallet().currentReceiveAddress();
            String amount = tfAmount.getText();
            String encodedLabel = URLEncoder.encode(tfLabel.getText(), "UTF-8");
            String encodedMessage = URLEncoder.encode(tfMessage.getText(), "UTF-8");
            String uri = String.format("bitcoin:%s?amount=%s&label=%s&message=%s", address.toString(), amount,
                    encodedLabel, encodedMessage);
            new QRRenderer(uri).displayIn(qrImage);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Alert");
            alert.setHeaderText("Failed to generate QR Code");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}