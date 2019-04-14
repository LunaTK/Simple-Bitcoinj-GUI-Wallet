package mywallet;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.EventObject;

import com.jfoenix.controls.JFXTextField;

import org.bitcoinj.core.Address;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
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

    private QRRenderer qrRenderer;

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
            qrRenderer = new QRRenderer(uri);
            qrRenderer.displayIn(qrImage);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Alert");
            alert.setHeaderText("Failed to generate QR Code");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onDownloadQRImage(ActionEvent event) {
        Stage stage = (Stage) ((Node) ((EventObject) event).getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();

        // Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("bitcoin-request");

        // Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                qrRenderer.saveAsFile(file);
            } catch (IOException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to save QR Image");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }
}