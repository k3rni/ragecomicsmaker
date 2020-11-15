package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pl.koziolekweb.ragecomicsmaker.model.Comic;

import java.util.List;

public class MetadataController {
    @FXML TextField isbn;
    @FXML TextField publisher;
    @FXML TextField pubdate;
    @FXML TextArea rights;
    @FXML TextArea description;
    @FXML TextField authors;
    @FXML TextField title;
    @FXML TextField illustrators;

    @FXML
    void initialize() {
        System.out.println("Hello");
        List.of(publisher, pubdate, rights, description, authors, title, illustrators, isbn).forEach((control) ->
                control.textProperty().addListener(this::onFieldsUpdated));
    }

    void onFieldsUpdated(Observable v, String oldValue, String newValue) {
        Comic comic = this.comic.get();
        if (oldValue == null || oldValue.isEmpty()) return;

        comic.title = title.getText();
        comic.isbn = isbn.getText();
        comic.description = description.getText();
        comic.author = authors.getText();
        comic.illustrator = illustrators.getText();
        comic.rights = rights.getText();
        comic.publicationDate = pubdate.getText();
        comic.publisher = publisher.getText();
    }

    SimpleObjectProperty<Comic> comic = new SimpleObjectProperty<>();

    public void setComic(Comic comic) {
        this.comic.set(comic);

        title.setText(comic.title);
        description.setText(comic.description);
        authors.setText(comic.author);
        illustrators.setText(comic.illustrator);
        rights.setText(comic.rights);
        pubdate.setText(comic.publicationDate);
        publisher.setText(comic.publisher);
        isbn.setText(comic.isbn);
    }

}
