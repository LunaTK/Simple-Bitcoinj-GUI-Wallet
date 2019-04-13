package mywallet;

import java.io.File;
import java.util.concurrent.Executor;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.Threading;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Dashboard extends Application {
    double xOffset, yOffset;
    Parent root;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        // URL myFxmlURL = ClassLoader.getSystemResource("sample.fxml");
        // root = FXMLLoader.load(myFxmlURL);
        setRunningThread();
        root = FXMLLoader.load(getClass().getResource("fxml/sample.fxml"));

        Scene scene = new Scene(root);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Bitcoin Wallet");
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

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

                System.out.println(wallet().currentReceiveAddress());
                System.out.println(wallet().getIssuedReceiveKeys());
                System.out.println(wallet().getBalance());
                System.out.println(wallet().getRecentTransactions(10, true));
            }
        };
        kit.startAsync();
        kit.awaitRunning();
    }

    private void setRunningThread() {
        Threading.USER_THREAD = (Runnable runnable) -> {
            Platform.runLater(runnable);
        };
    }

    public static void main(String[] args) {
        launch(args);
        // initWallet();
    }

}
