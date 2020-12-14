package pl.koziolekweb.ragecomicsmaker.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.Stylesheet;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.ComicCompiler;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEvent;
import pl.koziolekweb.ragecomicsmaker.SaveCommand;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RootController {
    @FXML public Region spring;
    @FXML public SplitPane leftPane;
    @FXML public TabPane tabPane;
    @FXML public Button browseBtn;

    @FXML MetadataController metadataTabController;
    @FXML ImageEditorController editorTabController;
    @FXML LeftPaneController leftPaneController;
    @FXML Button saveBtn;
    @FXML Button generateBtn;

    Path targetDir;
    Comic comic = null;
    Screen currentScreen;

    ObservableList<Screen> screens;

    private GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    private String darkMode = getClass().getResource("/darkMode.css").toExternalForm();

    @FXML
    void initialize() {
        App.EVENT_BUS.register(this);
        App.EVENT_BUS.register(editorTabController); // to receive AddFrameEvent

        // No easy way to set this from fxml
        saveBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.SAVE));
        browseBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.FOLDER));
        generateBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.BOOK));

        // Splitter is fixed in place, and does not resize automatically with window. Manual resize works.
        SplitPane.setResizableWithParent(leftPane, false);
        // Fill empty space in toolbar
        HBox.setHgrow(spring, Priority.ALWAYS);
        spring.setMinWidth(Region.USE_PREF_SIZE);
        spring.setMaxWidth(Integer.MAX_VALUE);

        editorTabController.newFrameCallback = this::onNewFrame;
        editorTabController.highlightFrame.bind(leftPaneController.hoverFrameProperty);

        leftPaneController.screenSelectedCallback = this::onScreenSelected;
        leftPaneController.framesReorderedCallback = this::onFramesReordered;

        this.screens = FXCollections.observableArrayList();
        leftPaneController.bindScreens(screens);

    }

    void loadComic() {
        Path xml = targetDir.resolve("comic.xml");
        if (!xml.toFile().canRead()) {
            this.comic = new Comic();
            saveBtn.setDisable(false);
            generateBtn.setDisable(false);
            comic.initDefaults();
            loadScreens();
        } else {
            this.comic = loadComicXml(xml);
            saveBtn.setDisable(false);
            generateBtn.setDisable(false);
            verifyScreens();
        }

        this.screens.setAll(comic.getScreens());
        this.metadataTabController.setComic(comic);
    }


    private Comic loadComicXml(Path xml) {
        ObjectMapper mapper = new XmlMapper();
        try {
            return mapper.readValue(xml.toFile(), Comic.class);
        } catch (IOException e) {
            App.EVENT_BUS.post(new ErrorEvent("Looks like comic.xml is corrupted. Consider replacing it with one of the backups", null));
            App.EVENT_BUS.post(new ErrorEvent(e.getMessage(), null));
            e.printStackTrace();
        }
        return null;
    }

    void loadScreens() {
        List<File> images = getImages(Optional.empty());
        Collections.sort(images);
        final String white = "#FFFFFF";
        Streams.mapWithIndex(images.stream(),
                (file, index) -> new Screen(file, white, index))
                .forEach(comic::addScreen);
    }

    void verifyScreens() {
        String pattern = comic.getImages().getNamePattern();
        List<File> images;

        if (pattern == null || pattern.isBlank())
            images = getImages(Optional.empty());
        else
            images = getImages(Optional.of(pattern));

        Collections.sort(images); // ?

        if (images.size() != comic.getScreens().size()) {
            // TODO: pop a warning message
            App.EVENT_BUS.post(new ErrorEvent("Screen count differs from saved version", null));
        }
    }

    List<File> getImages(Optional<String> namePattern) {
        final String digits = "[0-9]+";
        final String extPattern = "(jpg|png)";

        if (targetDir == null)
            return Collections.emptyList();

        String pattern = namePattern.orElse("screen")
                .split("@")[0] +
                digits + "\\." + extPattern;

        File dir = targetDir.toFile();
        return Arrays.stream(
                dir.listFiles((pathname ->
                        pathname.getName().matches(pattern))))
                .collect(Collectors.toList());
    }

    @FXML
    void showDirectoryDialog(Event e) {
        File dir = new DirectoryChooser().showDialog(null);
        if (dir == null) return;

        this.targetDir = dir.toPath();
        loadComic();
        App.EVENT_BUS.post(new DirSelectedEvent(dir));
    }

    @FXML
    void save() {
        try {
            new SaveCommand(this.comic, this.targetDir.toFile()).save();
            infoPopup("Success", "Saved comic definition XML");
        } catch (IOException e) {
            errorPopup(e);

        }
    }

    @FXML
    void generateBook() {
        try {
            new ComicCompiler(targetDir.toFile(), comic).save();
            infoPopup("Success", "Comic saved as `book.eupb`");
        } catch (IOException e) {
            errorPopup(e);
        }
    }

    @FXML
    void onKeyPressed(KeyEvent e) {
        if (e.isShortcutDown() && e.getCode() == KeyCode.S && this.comic != null) {
            save();
            e.consume();
        } else if (e.isShortcutDown() && e.getCode() == KeyCode.O) {
            showDirectoryDialog(e);
            e.consume();
        } else if (e.isShortcutDown() && e.getCode() == KeyCode.M) {
            tabPane.getSelectionModel().select(1);
            e.consume();
        } else if (e.getCode() == KeyCode.F1) {
            tabPane.getSelectionModel().select(0);
            e.consume();
        } else if (e.getCode() == KeyCode.F2) {
            tabPane.getSelectionModel().select(1);
            e.consume();
        } else if (e.getCode() == KeyCode.PAGE_UP) {
            leftPaneController.previousScreen();
            e.consume();
        } else if (e.getCode() == KeyCode.PAGE_DOWN) {
            leftPaneController.nextScreen();
            e.consume();
        } else if (e.isShortcutDown() && e.getCode() == KeyCode.DIGIT0) {
            toggleDark();
            e.consume();
        }
    }

    void toggleDark() {
        Scene scene = browseBtn.getScene();
        List<String> styles = scene.getStylesheets();
        if (styles.contains(darkMode))
            styles.remove(darkMode);
        else
            styles.add(darkMode);
    }

    private void onScreenSelected(Screen newValue) {
        if (newValue == null) return;
        currentScreen = newValue;
        editorTabController.showEditor(currentScreen);
    }

    private void onNewFrame(Frame frame) {
        leftPaneController.framesProperty.setAll(currentScreen.getFrames());
    }

    private void onFramesReordered(List<Frame> frames) {
        // Set screen again so editor replaces frames
        currentScreen.setFrames(frames);
        editorTabController.showEditor(currentScreen);
    }

    @Subscribe
    private void onErrorEvent(ErrorEvent err) {
        if (err.t != null)
            Platform.runLater(() -> errorPopup(err.t));
        else
            Platform.runLater(() -> errorPopup("Error", err.message));
    }

    void infoPopup(String title, String message) {
        Window window = leftPane.getScene().getWindow();
        Notifications
                .create()
                .owner(window)
                .title(title)
                .text(message)
                .showInformation();
    }

    void errorPopup(String title, String message) {
        Window window = leftPane.getScene().getWindow();
        Notifications
                .create()
                .owner(window)
                .title(title)
                .text(message)
                .showError();
    }

    void errorPopup(Throwable e) {
        e.printStackTrace();
        errorPopup("Exception", e.getMessage());
    }
}
