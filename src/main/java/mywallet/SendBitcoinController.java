package mywallet;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextField;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.Wallet.SendResult;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SendBitcoinController implements Initializable {
    @FXML
    JFXTextField labelTo;
    @FXML
    JFXTextField labelAmount;
    @FXML
    JFXTextField labelMemo;

    public static void show(Class<?> kClass) {
        Parent root;
        try {
            root = FXMLLoader.load(kClass.getResource("fxml/send_bitcoin.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Send Bitcoin");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void onSendBitcoin(ActionEvent event) {
        String destination = labelTo.getText();
        String value = labelAmount.getText();
        String memo = labelMemo.getText();
        try {
            sendBitcoin(destination, value, memo);
        } catch (InsufficientMoneyException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Alert");
            alert.setHeaderText("Insufficient Bitcoin Balance");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void sendBitcoin(String strDest, String strValue, String strMemo) throws InsufficientMoneyException {
        Wallet wallet = DashboardController.getKit().wallet();
        NetworkParameters params = TestNet3Params.get();
        Address destination = Address.fromString(params, strDest);
        Coin value = Coin.parseCoin(strValue);
        SendResult result = wallet.sendCoins(SendRequest.to(destination, value));
        result.tx.setMemo(strMemo);
    }

}