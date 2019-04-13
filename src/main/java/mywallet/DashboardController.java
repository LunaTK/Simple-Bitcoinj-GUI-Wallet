package mywallet;

import mywallet.helper.*;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.Threading;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.core.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class DashboardController implements Initializable {

    @FXML
    ImageView qrImage;
    @FXML
    Label labelAddress;
    @FXML
    StackPane stackPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("hi");

        new Thread(() -> {
            initWallet();
        }).start();
    }

    @FXML
    private void exitApp(ActionEvent event) {
        new DialogBuilder("종료하시겠습니까?").build(stackPane, (e) -> {
            System.exit(0);
        }, null).show();
    }

    private void initWallet() {
        BriefLogFormatter.initWithSilentBitcoinJ();
        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";

        WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                System.out.println(wallet().getKeyChainSeed().getMnemonicCode());
                if (wallet().getKeyChainGroupSize() < 1) {
                    wallet().importKey(new ECKey());
                }

                Platform.runLater(() -> {
                    labelAddress.setText(wallet().currentReceiveAddress().toString());
                    new QRRenderer("mke14L8w3gP46wuKE9186ZrsxUedr7XyE7").displayIn(qrImage);
                });
                System.out.println(wallet().currentReceiveAddress());
                System.out.println(wallet().getIssuedReceiveKeys());
                System.out.println(wallet().getBalance());
                System.out.println(wallet().getRecentTransactions(10, true));

            }
        };
        kit.startAsync();
        kit.awaitRunning();

        kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                // Runs in the dedicated "user thread".
                //
                // The transaction "tx" can either be pending, or included into a block (we
                // didn't see the broadcast).
                Coin value = tx.getValueSentToMe(w);
                System.out.println("Received tx for " + value.toFriendlyString() + ": " + tx);
                System.out.println("Transaction will be forwarded after it confirms.");

                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText("Incoming Transaction");
                alert.setContentText("Received tx for " + value.toFriendlyString() + ": " + tx);

                alert.showAndWait();
                // Wait until it's made it into the block chain (may run immediately if it's
                // already there).
                //
                // For this dummy app of course, we could just forward the unconfirmed
                // transaction. If it were
                // to be double spent, no harm done.
                // Wallet.allowSpendingUnconfirmedTransactions() would have to
                // be called in onSetupCompleted() above. But we don't do that here to
                // demonstrate the more common
                // case of waiting for a block.

                Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
                    @Override
                    public void onSuccess(TransactionConfidence result) {
                        // "result" here is the same as "tx" above, but we use it anyway for clarity.
                        // forwardCoins(result);
                        System.out.println(result.toString());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                }, Threading.SAME_THREAD);
            }
        });
    }

}
