package mywallet.helper;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.EventHandler;
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

    public JFXDialog build(StackPane stackPane, EventHandler<? super MouseEvent> onOk,
            EventHandler<? super MouseEvent> onCancel) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text(title));
        content.setBody(new Text(this.content));
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton btnOk = new JFXButton("OK");
        btnOk.setOnMouseClicked(onOk == null ? (e) -> {
            dialog.close();
        } : onOk);
        JFXButton btnCancel = new JFXButton("CANCEL");
        btnCancel.setOnMouseClicked(onCancel == null ? (e) -> {
            dialog.close();
        } : onCancel);
        content.setActions(btnOk, btnCancel);
        return dialog;
    }
}