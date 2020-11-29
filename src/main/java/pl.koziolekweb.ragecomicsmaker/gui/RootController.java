package pl.koziolekweb.ragecomicsmaker.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Streams;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import org.controlsfx.control.Notifications;
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
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RootController {
    @FXML public Region spring;
    @FXML public SplitPane leftPane;

    @FXML MetadataController metadataTabController;
    @FXML FrameEditorController editorTabController;
    @FXML LeftPaneController leftPaneController;

    Path targetDir;
    Comic comic;
    Screen currentScreen;

    ObservableList<Screen> screens;

    @FXML
    void initialize() {
        App.EVENT_BUS.register(this);
        App.EVENT_BUS.register(editorTabController); // to receive AddFrameEvent

        SplitPane.setResizableWithParent(leftPane, false);
        HBox.setHgrow(spring, Priority.ALWAYS);
        spring.setMinWidth(Region.USE_PREF_SIZE);
        spring.setMaxWidth(Integer.MAX_VALUE);

        editorTabController.newFrameCallback = this::onNewFrame;

        leftPaneController.screenSelectedCallback = this::onScreenSelected;
        leftPaneController.framesReorderedCallback = this::onFramesReordered;

        this.screens = FXCollections.observableArrayList();
        leftPaneController.bindScreens(screens);
    }

    void loadComic() {
        Path xml = targetDir.resolve("comic.xml");
        if (!xml.toFile().canRead()) {
            this.comic = new Comic();
            comic.initDefaults();
            loadScreens();
        } else {
            this.comic = loadComicXml(xml);
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
            // TODO: pop an error message
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
            Notifications
                    .create()
                    .title("Success")
                    .text("Saved comic definition")
                    .showInformation();
        } catch (IOException e) {
            showError(e);

        }
    }

    @FXML
    void generateBook() {
        try {
            new ComicCompiler(targetDir.toFile(), comic).save();
            Notifications
                    .create()
                    .title("Success")
                    .text("Comic saved as `book.epub`")
                    .showInformation();
        } catch (IOException e) {
            showError(e);
        }
    }

    @FXML
    void onKeyTyped(KeyEvent e) {
        // TODO: do we handle keys?
        System.out.println(e);
    }

    private void touchUI() {
        editorTabController.touchUI();
        leftPaneController.touchUI(null);
    }


    private void onScreenSelected(Screen newValue) {
        if (newValue == null) return;
        currentScreen = newValue;
        editorTabController.showEditor(currentScreen);
        // NOTE: not reactive. Must also be redrawn when handling events
        Set<Frame> frames = currentScreen.getFrames();
        Platform.runLater(() -> leftPaneController.updateFrameControls(frames));
    }

    private void onNewFrame(Frame frame) {
        touchUI();
        Set<Frame> frames = currentScreen.getFrames();
        Platform.runLater(() -> leftPaneController.updateFrameControls(frames));
    }

    private void onFramesReordered(Object o) {
        touchUI();
    }

    void showError(Exception e) {
        e.printStackTrace();
        Notifications
                .create()
                .title("Error")
                .text(e.getMessage())
                .showError();
    }
}
