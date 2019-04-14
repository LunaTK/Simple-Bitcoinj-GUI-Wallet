package mywallet;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import mywallet.helper.DialogBuilder;
import mywallet.helper.QRRenderer;

public class DashboardController implements Initializable {

    @FXML
    ImageView qrImage;
    @FXML
    Label labelAddress;
    @FXML
    StackPane stackPane;
    @FXML
    Label labelBalance;

    private static WalletAppKit kit;
    private WalletListener walletListener = new WalletListener();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            initWallet();
        }).start();
    }

    private void initWallet() {
        BriefLogFormatter.initWithSilentBitcoinJ();
        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";

        kit = new WalletAppKit(params, new File("."), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1) {
                    wallet().importKey(new ECKey());
                }
                // kit.wallet().allowSpendingUnconfirmedTransactions();
                kit.wallet().addCoinsReceivedEventListener(walletListener);
                kit.wallet().addCoinsSentEventListener(walletListener);
                kit.wallet().addChangeEventListener(walletListener);
                updateDisplayedWalletInfo();
                logTransactions();
            }
        };
        kit.startAsync();
        // kit.awaitRunning();

    }

    @FXML
    private void onSendBitcoin(ActionEvent event) {
        SendBitcoinController.show(getClass());
    }

    @FXML
    private void exitApp(ActionEvent event) {
        new DialogBuilder("Do you want to quit?").build(stackPane, (e) -> {
            Platform.exit();
            System.exit(0);
        }, null).show();
    }

    @FXML
    private void onCopyAddress(ActionEvent event) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(kit.wallet().currentReceiveAddress().toString()), null);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText("Address Copied!");
        alert.setContentText(kit.wallet().currentReceiveAddress().toString());
        alert.showAndWait();
    }

    private void updateDisplayedWalletInfo() {
        Platform.runLater(() -> {
            labelAddress.setText(kit.wallet().currentReceiveAddress().toString());
            new QRRenderer(kit.wallet().currentReceiveAddress().toString()).displayIn(qrImage);
            labelBalance.setText(kit.wallet().getBalance().toFriendlyString());
        });
    }

    private void logTransactions() {
        Set<Transaction> transactions = kit.wallet().getTransactions(true);
        for (Transaction tx : transactions) {
            System.out.println("TXID : " + tx.getTxId());
            System.out.println("Confidence : " + tx.getConfidence());
            System.out.println("Value Sent to Me : " + tx.getValueSentToMe(kit.wallet()));
            System.out.println("Value Sent from Me : " + tx.getValueSentFromMe(kit.wallet()));
            System.out.println("Memo : " + tx.getMemo());
            System.out.println("Purpose : " + tx.getPurpose());
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        }
    }

    public static WalletAppKit getKit() {
        return kit;
    }

    private class WalletListener
            implements WalletCoinsReceivedEventListener, WalletCoinsSentEventListener, WalletChangeEventListener {

        @Override
        public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Notice");
            alert.setHeaderText("Coin Sent");
            alert.setContentText("Previous balance : " + prevBalance.toFriendlyString() + "\n" + "New balance : "
                    + newBalance.toFriendlyString());
            alert.showAndWait();
        }

        @Override
        public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
            Coin value = tx.getValueSentToMe(wallet);
            System.out.println("Received tx for " + value.toFriendlyString() + ": " + tx);
            System.out.println("Transaction will be forwarded after it confirms.");

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Notice");
            alert.setHeaderText("Coin Received");
            alert.setContentText(
                    "Received tx for " + tx.getTxId() + "\n" + "Received Value : " + value.toFriendlyString());
            alert.showAndWait();
        }

        @Override
        public void onWalletChanged(Wallet wallet) {
            updateDisplayedWalletInfo();
        }

    }
}
