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
import pl.koziolekweb.ragecomicsmaker.model.Comic;

public class MetadataController {
    @FXML PropertySheet propsheet;
    SimpleObjectProperty<Comic> comic = new SimpleObjectProperty<>();

    @FXML
    void initialize() {
        this.propsheet.setPropertyEditorFactory(this::selectPropertyEditor);
    }

    private PropertyEditor<?> selectPropertyEditor(PropertySheet.Item item) {
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
        ObservableList<PropertySheet.Item> props = FXCollections.observableArrayList();
        props.add(new StringProp("Title", com.title) {});
        StringProp desc = new StringProp("Description", com.description);
        props.add(desc);
        props.add(new StringProp("Author", com.author));
        props.add(new StringProp("Illustrator", com.illustrator));
        props.add(new StringProp("ISBN", com.isbn));
        props.add(new StringProp("Publisher", com.publisher));
        props.add(new StringProp("Publication Date", com.publicationDate, "Format: YYYY, YYYY-MM or YYYY-MM-DD"));
       props.add(new StringProp<>("Copyright", com.rights));
        return props;
    }

    public final <T> PropertyEditor<?> createTextareaEditor(PropertySheet.Item property) {
        TextArea textArea = new TextArea();
        textArea.setStyle("-fx-border-color: black");
        return new AbstractPropertyEditor<String, TextArea>(property, textArea) {

            @Override
            protected ObservableValue<String> getObservableValue() {
                return (ObservableValue<String>) this.getProperty().getObservableValue().get();
            }

            @Override
            public void setValue(String text) {
                getEditor().setText(text);
            }
        };
    }

}
