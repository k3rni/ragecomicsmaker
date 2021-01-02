package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import net.coobird.thumbnailator.Thumbnailator;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class LeftPaneController {
    @FXML ListView<Screen> filePane;
    @FXML public ListView<Frame> framesList;

    public Consumer<Screen> screenSelectedCallback;
    public Consumer<List<Frame>> framesReorderedCallback;

    SimpleObjectProperty<ObservableList<Screen>> screenListProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    SimpleObjectProperty<Screen> screenProperty = new SimpleObjectProperty<>();
    ObservableList<Frame> framesProperty = FXCollections.observableArrayList();

    public SimpleObjectProperty<Frame> hoverFrameProperty = new SimpleObjectProperty<>();

    private boolean setupDone = false;

    private final WeakHashMap<Frame, Image> frameThumbnails = new WeakHashMap<>();
    GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

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
                    setGraphic(fontAwesome.create(FontAwesome.Glyph.FILE_IMAGE_ALT));
                }
            }
        });

        filePane.itemsProperty().bind(screenListProperty);
        filePane.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        screenProperty.bind(filePane.getSelectionModel().selectedItemProperty());
        screenProperty.addListener(this::onScreenSelected);
    }

    private void setupFramesPane() {
        framesList.setCellFactory((frame) -> new FrameListCell(
                this::trackFrameListMouse,
                this::createFrameNode,
                this::xxReorder));

        framesProperty.addListener((ListChangeListener<? super Frame>) change -> {
            System.out.println(this);
            System.out.println(change);
            framesReorderedCallback.accept(framesProperty);
        });
    }

    public void nextScreen() {
        // Called by key handling code in RootController. Advance to next screen if possible
        filePane.getSelectionModel().selectNext();
    }

    public void previousScreen() {
        // Called by key handling code in RootController. Go to previous screen if possible
        filePane.getSelectionModel().selectPrevious();
    }

    private void xxReorder(List<Frame> frames) {
        // Frame list did something to its items that require updating framesProperty
        framesProperty.setAll(frames);
    }

    public void trackFrameListMouse(ListCell<Frame> f) {
        hoverFrameProperty.set(f == null ? null : f.getItem());
    }

    public void bindScreens(ObservableList<Screen> screens) {
        Bindings.bindContent(this.screenListProperty.get(), screens);
    }

    public void touchUI(Set<Frame> frames) {
        framesProperty.setAll(frames);
    }

    private void onScreenSelected(Observable observable, Screen oldScreen, Screen newScreen) {
        screenSelectedCallback.accept(newScreen);
        framesProperty.setAll(newScreen.getFrames());
        framesList.setItems(framesProperty.sorted());
    }

    URL frameControlFXML = getClass().getResource("/ui/frame-controls.fxml");
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
            Button btn = (Button) n.lookup("#delete");
            btn.setGraphic(fontAwesome.create(FontAwesome.Glyph.CLOSE));
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

    @FXML
    void deleteFrame(Event e) {
        Screen currentScreen = screenProperty.get();
        Frame f = hoverFrameProperty.get();
        currentScreen.removeFrame(f);
        touchUI(currentScreen.getFrames());
    }
}
