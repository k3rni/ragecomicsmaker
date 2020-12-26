package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.apache.commons.io.IOUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import pl.koziolekweb.ragecomicsmaker.model.Snippet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pl.koziolekweb.ragecomicsmaker.model.Snippet.loadSnippet;

public class StylesheetEditorController {
    public ComboBox<Snippet> presetsDropdown;
    public Button saveBtn;
    public CodeArea codeArea;
    private Path targetDir;

    public void initialize() {
        presetsDropdown.setButtonCell(cellBuilder(null));
        presetsDropdown.setCellFactory(this::cellBuilder);
        presetsDropdown.getSelectionModel().selectedItemProperty().addListener(this::onDropdownSelected);
        presetsDropdown.getItems().addAll(defaultSnippets());

        saveBtn.setOnMouseClicked(this::onSaveClicked);

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        final Pattern whiteSpace = Pattern.compile("^\\s+");
        codeArea.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() != KeyCode.ENTER) return;
            int caret = codeArea.getCaretPosition();
            int para = codeArea.getCurrentParagraph();
            Matcher m = whiteSpace.matcher(codeArea.getParagraph(para - 1).getSegments().get(0));
            if (m.find()) Platform.runLater(() -> {
                codeArea.insertText(caret, m.group());
            });
        });
    }

    private List<Snippet> defaultSnippets() {
        return List.of(
                new Snippet("Empty", ""),
                loadSnippet("/translate50.css", "CSS translate()"),
                loadSnippet("/flex-center.css", "Flexbox Center"),
                loadSnippet("/table-centered.css", "CSS display:table")
        );
    }

    private void onDropdownSelected(Observable observable, Snippet oldTemplate, Snippet newTemplate) {
        if (newTemplate == null) return;
        codeArea.replaceText(newTemplate.getCode());
    }

    public void onComicLoaded(Path targetDir) {
        this.targetDir = targetDir;

        Path page = targetDir.resolve("style.css");
        if (page.toFile().canRead()) {
            loadSource(page);
            saveBtn.setText("Overwrite");
        }
    }

    public void onSaveClicked(MouseEvent e) {
        String text = codeArea.getText();

        Optional.ofNullable(targetDir).ifPresent(path -> {
            try {
                IOUtils.write(text, new FileOutputStream(path.resolve("style.css").toFile()), Charset.defaultCharset());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    private void loadSource(Path page) {
        try {
            String code = IOUtils.toString(new FileInputStream(page.toFile()), Charset.defaultCharset());
            Snippet loadedItem = new Snippet(page.getFileName().toString(), code);
            presetsDropdown.getItems().add(0, loadedItem);
            presetsDropdown.getSelectionModel().select(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ListCell<Snippet> cellBuilder(ListView<Snippet> items) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Snippet item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (empty) setText(null);
                else setText(item.getName());
            }
        };
    }
}
