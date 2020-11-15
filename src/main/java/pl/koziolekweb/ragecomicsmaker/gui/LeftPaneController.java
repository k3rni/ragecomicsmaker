package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.collect.Streams;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.coobird.thumbnailator.Thumbnailator;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.lang.model.type.NullType;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class LeftPaneController {
    @FXML ListView<Screen> filePane;
    @FXML public VBox framesList;
    public Consumer<Screen> screenSelectedCallback;
    public Consumer<Object> framesReorderedCallback;
    ObservableList<Screen> screens = FXCollections.observableArrayList();
    private Screen currentScreen;
    private boolean setupDone = false;
    
    private WeakHashMap<Frame, Image> frameThumbnails = new WeakHashMap<>();

    @FXML
    void initialize() {
        /* NOTE: we're using this controller for both the left pane AND the
        small frame reorder/delete views. We only want to initialize once,
        but subsequent FXMLLoader.load() calls WILL call initialize multiple times.
         */
        if (setupDone) return;
        setupFilePane();
        setupDone = true;
    }

    public void bindScreens(ObservableList<Screen> screens) {
        Bindings.bindContent(this.screens, screens);
        filePane.setItems(this.screens);
    }

    public void touchUI(Set<Frame> frames) {
        if (frames != null) updateFrameControls(frames);
        Platform.runLater(() -> filePane.refresh());
    }

    private void setupFilePane() {
        filePane.setCellFactory((screen) -> new ListCell<>() {
            @Override
            protected void updateItem(Screen item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getLabel());
                    setGraphic(null); // TODO: a thumbnail?
                }
            }
        });
        filePane.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        filePane.getSelectionModel().selectedItemProperty().addListener(this::onScreenSelected);
    }

    private void onScreenSelected(Observable observable, Screen oldValue, Screen newValue) {
        this.currentScreen = newValue;
        screenSelectedCallback.accept(newValue);
    }

    public void updateFrameControls(Set<Frame> frames) {
        ObservableList<Node> children = framesList.getChildren();
        children.clear();

        for (Frame f : frames) {
            children.add(createFrameNode(f));
        }

        children.stream().findFirst().ifPresent((v) -> v.lookup("#up").setDisable(true));
        Streams.findLast(children.stream()).ifPresent((v) -> v.lookup("#down").setDisable(true));
    }

    private Node createFrameNode(Frame frame) {
        URL res = getClass().getResource("/frame-controls.fxml");
        FXMLLoader loader = new FXMLLoader(res);
        loader.setController(this);

        try {
            HBox box = new HBox();
            loader.setRoot(box);
            Node n = loader.load();
            n.getProperties().put("frame-id", frame.getId());
            Label label = (Label) n.lookup("#label");
            label.setText(frame.getLabel());
            ImageView img = (ImageView) n.lookup("#image");
            img.setImage(lookupImageFor(currentScreen, frame));
            return box;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private Image lookupImageFor(Screen screen, Frame frame) {
        Image cached = frameThumbnails.get(frame);
        if (cached != null) return cached;

        try {
            BufferedImage img = Thumbnailator.createThumbnail(screen.crop(frame), 90, 90);
            Image thumb = SwingFXUtils.toFXImage(img, null);
            frameThumbnails.put(frame, thumb);
            return thumb;
        } catch (IOException err) {
            err.printStackTrace();
            return null;
        }
    }

    // NOTE: don't have to be on this controller, could be a different object
    @FXML
    void reorderUp(ActionEvent e) {
        System.out.println(e);
        Map<Object, Object> props = ((Button) e.getTarget()).getParent().getProperties();
        int frameId = (Integer) props.get("frame-id");
        Frame f = currentScreen.findFrame(frameId);
        currentScreen.moveFrameUp(f);
        touchUI(currentScreen.getFrames());
        framesReorderedCallback.accept(f);
    }

    @FXML
    void reorderDown(ActionEvent e) {
        Map<Object, Object> props = ((Button) e.getTarget()).getParent().getProperties();
        int frameId = (Integer) props.get("frame-id");
        Frame f = currentScreen.findFrame(frameId);
        currentScreen.moveFrameDown(f);
        touchUI(currentScreen.getFrames());
        framesReorderedCallback.accept(f);
    }

    @FXML
    void deleteFrame(Event e) {
        Map<Object, Object> props = ((Button) e.getTarget()).getParent().getProperties();
        int frameId = (Integer) props.get("frame-id");
        Frame f = currentScreen.findFrame(frameId);
        currentScreen.removeFrame(f);
        touchUI(currentScreen.getFrames());
        framesReorderedCallback.accept(f);
    }
}
