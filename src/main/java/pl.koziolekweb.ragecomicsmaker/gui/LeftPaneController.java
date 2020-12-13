package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import net.coobird.thumbnailator.Thumbnailator;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class LeftPaneController {
    @FXML ListView<Screen> filePane;
    @FXML public ListView<Frame> framesList;

    public Consumer<Screen> screenSelectedCallback;
    public Consumer<Object> framesReorderedCallback;

    SimpleObjectProperty<ObservableList<Screen>> screenListProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    SimpleObjectProperty<Screen> screenProperty = new SimpleObjectProperty<>();
    ObservableList<Frame> framesProperty = FXCollections.observableArrayList();
    public SimpleObjectProperty<Frame> hoverFrameProperty = new SimpleObjectProperty<>();

    private boolean setupDone = false;
    
    private WeakHashMap<Frame, Image> frameThumbnails = new WeakHashMap<>();

    @FXML
    void initialize() {
        /* NOTE: we're using this controller for both the left pane AND the
        small frame reorder/delete views. We only want to initialize once,
        but subsequent FXMLLoader.load() calls (used for framelist items)
        WILL call initialize multiple times.
         */
        if (setupDone) return;

        setupFilePane();
        setupFramesPane();
        setupDone = true;
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
        filePane.itemsProperty().bind(screenListProperty);
        filePane.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        this.screenProperty.bind(filePane.getSelectionModel().selectedItemProperty());
        this.screenProperty.addListener(this::onScreenSelected);
    }

    private void setupFramesPane() {
        framesList.setCellFactory((frame) -> {
            ListCell<Frame> c = new ListCell<>() {
                @Override
                protected void updateItem(Frame item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (item == null || empty) setGraphic(null);
                    else setGraphic(createFrameNode(item));
                }
            };
            c.setOnMouseEntered((e) -> trackFrameListMouse(c, e));
            c.setOnMouseExited((e) -> trackFrameListMouse(null, e));
            return c;
        });

        this.framesProperty.addListener((ListChangeListener<Frame>) c -> {
            framesList.setItems((ObservableList<Frame>) c.getList());
        });
    }

    public void trackFrameListMouse(ListCell<Frame> f, MouseEvent e) {
        if (f == null)
            hoverFrameProperty.set(null);
        else
            hoverFrameProperty.set(f.getItem());
    }

    public void bindScreens(ObservableList<Screen> screens) {
        Bindings.bindContent(this.screenListProperty.get(), screens);
    }

    public void touchUI(Set<Frame> frames) {
        this.framesProperty.setAll(frames);
    }

    private void onScreenSelected(Observable observable, Screen oldScreen, Screen newScreen) {
        screenSelectedCallback.accept(newScreen);
        framesProperty.setAll(newScreen.getFrames());
    }

    URL frameControlFXML = getClass().getResource("/frame-controls.fxml");
    FXMLLoader loader = new FXMLLoader(frameControlFXML);

    private Node createFrameNode(Frame frame) {
        loader.setController(this);

        try {
            BorderPane box = new BorderPane();
            loader.setRoot(box);
            Node n = loader.load();
            n.getProperties().put("frame-id", frame.getId());
            Label label = (Label) n.lookup("#label");
            label.setText(frame.getLabel());
            ImageView img = (ImageView) n.lookup("#image");
            img.setImage(lookupImageFor(screenProperty.get(), frame));
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
        Screen currentScreen = screenProperty.get();
        Frame f = hoverFrameProperty.get();
        currentScreen.moveFrameUp(f);
        touchUI(currentScreen.getFrames());
        framesReorderedCallback.accept(f);
    }

    @FXML
    void reorderDown(ActionEvent e) {
        Screen currentScreen = screenProperty.get();
        Frame f = hoverFrameProperty.get();
        currentScreen.moveFrameDown(f);
        touchUI(currentScreen.getFrames());
        framesReorderedCallback.accept(f);
    }

    @FXML
    void deleteFrame(Event e) {
        Screen currentScreen = screenProperty.get();
        Frame f = hoverFrameProperty.get();
        currentScreen.removeFrame(f);
        touchUI(currentScreen.getFrames());
        framesReorderedCallback.accept(f);
    }
}
