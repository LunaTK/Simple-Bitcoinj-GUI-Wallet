package mywallet;

import org.bitcoinj.core.Transaction;

import javafx.scene.control.ListCell;

public class HistoryCell extends ListCell<Transaction> {

    @Override
    protected void updateItem(Transaction tx, boolean empty) {
        super.updateItem(tx, empty);
        if (tx != null) {
            HistoryCellController hcc = new HistoryCellController();
            hcc.updateDisplayedInfo(tx);
            setGraphic(hcc.getRootPane());
        }
    }

}