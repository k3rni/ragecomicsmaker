package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;
import pl.koziolekweb.ragecomicsmaker.gui.settings.BoolProp;
import pl.koziolekweb.ragecomicsmaker.gui.settings.StringProp;
import pl.koziolekweb.ragecomicsmaker.model.Comic;

public class MetadataController {
    @FXML PropertySheet propsheet;
    SimpleObjectProperty<Comic> comic = new SimpleObjectProperty<>();

    @FXML
    void initialize() {
        this.propsheet.setPropertyEditorFactory(this::selectPropertyEditor);
    }

    private PropertyEditor<?> selectPropertyEditor(PropertySheet.Item item) {
        if (item.getType() == Boolean.class)
            return Editors.createCheckEditor(item);
        switch (item.getName()) {
            case "Description":
                return createTextareaEditor(item);
            default:
                return Editors.createTextEditor(item);
        }
    }

    public void setComic(Comic comic) {
        this.comic.set(comic);
        this.propsheet.getItems().setAll(getPropertyItems(this.comic));
    }

    private ObservableList<PropertySheet.Item> getPropertyItems(SimpleObjectProperty<Comic> comicProp) {
        Comic com = comic.get();
        final String OUTPUT = "Output preferences";
        final String BOOK = "E-book metadata";
        ObservableList<PropertySheet.Item> props = FXCollections.observableArrayList();
        props.add(new StringProp<>("Title", com.title, "Primary title", BOOK) {});
        props.add(new StringProp<>("Description", com.description, "Description or blurb", BOOK));
        props.add(new StringProp<>("Author", com.author, "Primary author", BOOK));
        props.add(new StringProp<>("Illustrator", com.illustrator, "Primary illustrator", BOOK));
        props.add(new StringProp<>("ISBN", com.isbn, "ISBN/ISSN number", BOOK));
        props.add(new StringProp<>("Publisher", com.publisher, "", BOOK));
        props.add(new StringProp<>("Publication Date", com.publicationDate,
                "Format: YYYY, YYYY-MM or YYYY-MM-DD", BOOK));
        props.add(new StringProp<>("Copyright", com.rights, "Copyright year(s) and owner(s)", BOOK));
        props.add(new StringProp<>("Language", com.language, "Two-letter code", BOOK));
        props.add(new BoolProp<>("Full pages", com.insertFullPages,
                "Insert full page image before any clips of that page.",
                OUTPUT));
        return props;
    }

    public final <T> PropertyEditor<?> createTextareaEditor(PropertySheet.Item property) {
        TextArea textArea = new TextArea();
        textArea.setStyle("-fx-border-color: black");
        return new AbstractPropertyEditor<String, TextArea>(property, textArea) {

            @Override
            protected ObservableValue<String> getObservableValue() {
                return (ObservableValue<String>) this.getProperty().getObservableValue().orElse(null);
            }

            @Override
            public void setValue(String text) {
                getEditor().setText(text);
            }
        };
    }

}
