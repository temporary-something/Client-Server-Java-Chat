package view.impl;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import model.MessageContainer;

public class ChatListCell extends ListCell<MessageContainer> {

    public ChatListCell() {
        this.setWrapText(true);
        this.setPrefWidth(200);
    }

    @Override
    protected void updateItem(MessageContainer item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            setText(item.getMessageContent().getMessage());
            if (item.getUser() == null) {
                setAlignment(Pos.CENTER_RIGHT);
            } else {
                setAlignment(Pos.CENTER_LEFT);
            }
        }
    }
}
