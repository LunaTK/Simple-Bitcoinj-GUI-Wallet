package mywallet.helper;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.EventHandler;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class DialogBuilder {
    private String title = "Alert";
    private String content = "Content";

    public DialogBuilder(String title, String content) {
        this(content);
        this.title = title;
    }

    public DialogBuilder(String content) {
        this.content = content;
    }

    public DialogBuilder() {
    }

    public JFXDialog buildYesNo(StackPane stackPane, EventHandler<? super MouseEvent> onOk,
            EventHandler<? super MouseEvent> onCancel) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text(title));
        content.setBody(new Text(this.content));
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton btnOk = new JFXButton("OK");
        btnOk.setOnMouseClicked(onOk == null ? (e) -> {
            dialog.close();
        } : onOk);
        btnOk.setStyle("-fx-text-fill: green;");
        JFXButton btnCancel = new JFXButton("CANCEL");
        btnCancel.setOnMouseClicked(onCancel == null ? (e) -> {
            dialog.close();
        } : onCancel);
        btnCancel.setStyle("-fx-text-fill: red;");
        content.setActions(btnOk, btnCancel);
        return dialog;
    }

    public static TextInputDialog buildPasswordInputDialog(String headerText) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Wallet Password");
        dialog.setHeaderText(headerText);
        dialog.setContentText("Wallet Password");
        return dialog;
    }

    public static TextInputDialog buildPasswordInputDialog() {
        return buildPasswordInputDialog("Wallet is encrypted, password required");
    }
}