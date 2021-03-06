package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import org.controlsfx.control.Notifications;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.LoadCommand;
import pl.koziolekweb.ragecomicsmaker.SaveCommand;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEvent;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;
import pl.koziolekweb.ragecomicsmaker.output.CBZCompiler;
import pl.koziolekweb.ragecomicsmaker.output.EpubCompiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class RootController {
    @FXML
    public Region spring;
    @FXML
    public SplitPane leftPane;
    @FXML
    public TabPane tabPane;
    @FXML
    public Button browseBtn;
    @FXML
    public Button generateCBZ;

    @FXML
    MetadataController metadataTabController;
    @FXML
    ImageEditorController editorTabController;
    @FXML
    LeftPaneController leftPaneController;
    @FXML
    TemplateEditorController templateTabController;
    @FXML
    StylesheetEditorController stylesheetTabController;
    @FXML
    Button saveBtn;
    @FXML
    Button generateEPUB;

    Path targetDir;
    Comic comic = null;
    Screen currentScreen;

    ObservableList<Screen> screens;

    private final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    private final String darkMode = getClass().getResource("/themes/modena_dark.css").toExternalForm();

    @FXML
    void initialize() {
        App.EVENT_BUS.register(this);

        // No easy way to set this from fxml
        saveBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.SAVE));
        browseBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN));
        generateEPUB.setGraphic(fontAwesome.create(FontAwesome.Glyph.BOOK));
        generateCBZ.setGraphic(fontAwesome.create(FontAwesome.Glyph.FILE_ZIP_ALT));
        tabPane.getTabs().get(0).setGraphic(fontAwesome.create(FontAwesome.Glyph.EDIT));
        tabPane.getTabs().get(1).setGraphic(fontAwesome.create(FontAwesome.Glyph.TAGS));
        tabPane.getTabs().get(2).setGraphic(fontAwesome.create(FontAwesome.Glyph.HTML5));
        tabPane.getTabs().get(3).setGraphic(fontAwesome.create(FontAwesome.Glyph.CSS3));

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


    @FXML
    void showDirectoryDialog(Event ev) {
        File dir = new DirectoryChooser().showDialog(null);
        if (dir == null) return;

        this.targetDir = dir.toPath();
        this.comic = new LoadCommand(dir.toPath()).load();
        this.screens.setAll(comic.getScreens());
        this.metadataTabController.setComic(comic);
        templateTabController.onComicLoaded(targetDir);
        stylesheetTabController.onComicLoaded(targetDir);
        saveBtn.setDisable(false);
        generateEPUB.setDisable(false);
        generateCBZ.setDisable(false);
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
    void exportEPUB() {
        try {
            Path result = new EpubCompiler(targetDir.toFile(), comic).save();
            infoPopup("Success", String.format("Comic exported as `%s`", result.getFileName()));
        } catch (IOException e) {
            errorPopup(e);
        }
    }

    @FXML
    void exportCBZ() {
        try {
            Path result = new CBZCompiler(targetDir, comic).save();
            infoPopup("Success", String.format("Comic exported as `%s`", result.getFileName()));
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
        } else if (e.isShortcutDown() && e.getCode() == KeyCode.DIGIT1) {
            tabPane.getSelectionModel().select(0);
            e.consume();
        } else if (e.isShortcutDown() && e.getCode() == KeyCode.DIGIT2) {
            tabPane.getSelectionModel().select(1);
            e.consume();
        } else if (e.isShortcutDown() && e.getCode() == KeyCode.F) {
            editorTabController.fit();
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
        Notifications
                .create()
                .owner(null)
                .title(title)
                .text(message)
                .showInformation();
    }

    void errorPopup(String title, String message) {
        Notifications
                .create()
                .owner(null)
                .title(title)
                .text(message)
                .showError();
    }

    void errorPopup(Throwable e) {
        e.printStackTrace();
        errorPopup("Exception", e.getMessage());
    }
}
