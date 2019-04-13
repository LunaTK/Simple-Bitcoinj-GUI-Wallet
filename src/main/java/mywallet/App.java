package mywallet;

import org.bitcoinj.utils.Threading;

import javafx.application.Platform;
import mywallet.MyBitcoinWallet;

public class App {
    public static void main(String args[]) {
        Threading.USER_THREAD = (Runnable runnable) -> {
            Platform.runLater(runnable);
        };
        MyBitcoinWallet.main(args);
    }
}