package charliek.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents one chat message with a text label and an optional avatar slot.
 */
public class DialogBox extends HBox {
    /**
     * Displays the message text.
     */
    @FXML
    private Label dialog;

    /**
     * Displays the speaker's avatar when one is supplied.
     */
    @FXML
    private ImageView displayPicture;

    /**
     * Builds a dialog box from its FXML view.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box view.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        getStyleClass().add("dialog-box");
    }

    /**
     * Creates a right-aligned dialog for user input.
     *
     * @param text the message to display.
     * @param image the user's avatar, or {@code null} when no avatar is available.
     * @return a right-aligned user dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for chatbot output.
     *
     * @param text the message to display.
     * @param image the chatbot's avatar, or {@code null} when no avatar is available.
     * @return a left-aligned chatbot dialog box.
     */
    public static DialogBox getChatbotDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.getStyleClass().add("bot-dialog");
        return dialogBox;
    }

    /**
     * Places the avatar before the text and aligns the message to the left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
