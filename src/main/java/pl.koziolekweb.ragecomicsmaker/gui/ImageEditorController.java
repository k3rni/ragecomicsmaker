package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageEditorController {
    @FXML public ImageView imageDisplay;
    @FXML public ScrollPane scrollPane;
    @FXML public AnchorPane stack;
    @FXML public Pane framesContainer;
    @FXML public Canvas frameCanvas;

    public Consumer<Frame> newFrameCallback;

    private SimpleDoubleProperty imgWidthProperty = new SimpleDoubleProperty();
    private SimpleDoubleProperty imgHeightProperty = new SimpleDoubleProperty();
    private SimpleObjectProperty<Screen> screenProperty = new SimpleObjectProperty<>();
    private ObservableList<Frame> framesProperty = FXCollections.observableArrayList();
    private SimpleDoubleProperty zoomProperty = new SimpleDoubleProperty(1.0);
    private SimpleObjectProperty<Point3D> dragOriginProperty = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Point3D> dragFinishProperty = new SimpleObjectProperty<>();
    public SimpleObjectProperty<Frame> highlightFrame = new SimpleObjectProperty<>();


    public void initialize() {
        AnchorPane.setTopAnchor(imageDisplay, 0.0);
        AnchorPane.setBottomAnchor(imageDisplay, 0.0);
        AnchorPane.setLeftAnchor(imageDisplay, 0.0);
        AnchorPane.setRightAnchor(imageDisplay, 0.0);
        AnchorPane.setTopAnchor(framesContainer, 0.0);
        AnchorPane.setBottomAnchor(framesContainer, 0.0);
        AnchorPane.setLeftAnchor(framesContainer, 0.0);
        AnchorPane.setRightAnchor(framesContainer, 0.0);

        imageDisplay.setPreserveRatio(true);

        // Handle window resize events, send to resize image appropriately
        // These also invoke resizing behavior for framesContainer
        scrollPane.widthProperty().addListener((observableValue, oldValue, newValue) -> imageDisplay.setFitWidth(newValue.doubleValue()));
        scrollPane.heightProperty().addListener((observable, oldValue, newValue) -> imageDisplay.setFitHeight(newValue.doubleValue()));

        // Wheel event must attach to canvas, which is over the image
        frameCanvas.setOnScroll(this::onScroll);

        frameCanvas.setOnMousePressed(this::onMousePressed);
        frameCanvas.setOnMouseDragged(this::onDragMovement);
        frameCanvas.setOnMouseReleased(this::onMouseReleased);

        // Handle when image bounds change (on user zoom or window resize auto-fit)
        imageDisplay.boundsInParentProperty().addListener(this::onImageResize);

        // framesContainer, being a Pane, does not have a bindable widthProperty
        // We set it to always use preferred size, then set it explicitly in onHeightChange/onWidthChange
        framesContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        framesContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // frameCanvas can bind to actual image size
        frameCanvas.widthProperty().bind(this.imgWidthProperty);
        frameCanvas.heightProperty().bind(this.imgHeightProperty);

        screenProperty.addListener(this::onScreenChanged);
        framesProperty.addListener(this::onFramesChanged);
        highlightFrame.addListener(this::highlightFrameChanged);

        zoomProperty.addListener(this::onZoomChanged);

        dragFinishProperty.addListener(this::onDragProgress);
    }

    private void onImageResize(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds newBounds) {
        // Whenever image size changes (zoom or window resize), update these properties, which are then used
        // to appropriately scale frames.
        this.imgWidthProperty.set(newBounds.getWidth());
        this.imgHeightProperty.set(newBounds.getHeight());
    }

    private void onScroll(ScrollEvent scrollEvent) {
        // Handle mousewheel events
        if (!scrollEvent.isControlDown()) return;

        double step = scrollEvent.getDeltaY() / 100.0;
        // Clamp zoom to a range
        zoomProperty.set(Math.max(0.5, Math.min(4.0, zoomProperty.get()) + step));

        /*
        TODO: calculate scroll offset so that pointer x,y which corresponded to scaled location px, py
        Platform.runLater(() -> {
            scrollPane.setHvalue(...);
            scrollPane.setVvalue(...);
        });
        */

        // Eat the event, hiding it from the ScrollPane
        scrollEvent.consume();
    }

    private void onZoomChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        Image image = imageDisplay.getImage();
        double zoom = zoomProperty.get();

        // These cannot be bound to image.widthProperty.multiply() because we set them explicitly
        // when handling scrollpane's resize events
        imageDisplay.setFitWidth(image.getWidth() * zoom);
        imageDisplay.setFitHeight(image.getHeight() * zoom);
    }


    private void onMousePressed(MouseEvent e) {
        // Maybe start a drag-rectangle gesture
        if (e.getButton() != MouseButton.PRIMARY) return;

        dragOriginProperty.set(e.getPickResult().getIntersectedPoint());
    }

    private void onMouseReleased(MouseEvent e) {
        // End a drag-rectangle gesture
        if (e.getButton() != MouseButton.PRIMARY) return;

        createNewFrame();
        dragOriginProperty.set(null);
        dragFinishProperty.set(null);

        GraphicsContext gc = frameCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, frameCanvas.getWidth(), frameCanvas.getHeight());
    }

    private void onDragMovement(MouseEvent e) {
        // While dragging, update finish point. This is then painted by appropriate listeners.
        System.out.print("Moved ");
        System.out.println(e);

        if (!e.isPrimaryButtonDown()) return;

        dragFinishProperty.set(e.getPickResult().getIntersectedPoint());
    }

    private void onDragProgress(ObservableValue<? extends Point3D> observable, Point3D oldValue, Point3D newValue) {
        // Draw rectangle between current start and finish points.
        if (newValue == null) return;

        Point3D start = dragOriginProperty.get();
        Point3D end = newValue;

        System.out.println(start.distance(end));
        GraphicsContext gc = frameCanvas.getGraphicsContext2D();

        gc.clearRect(0, 0, frameCanvas.getWidth(), frameCanvas.getHeight());

        gc.setStroke(new Color(0.8627451f, 0.078431375f, 0.23529412f, 1.0).brighter()); // Crimson
        gc.setFill(new Color(0.8627451f, 0.078431375f, 0.23529412f, 0.5));

        // TODO: rearrange coords when not dragging topleft to bottomright
        double width = end.getX() - start.getX();
        double height = end.getY() - start.getY();
        gc.strokeRect(start.getX(), start.getY(), width, height);
        gc.fillRect(start.getX() + 1, start.getY() + 1, width - 2, height - 2);
    }

    private void createNewFrame() {
        // TODO: rearrange coords when not dragging topleft to bottomright
        Point3D start = dragOriginProperty.get();
        Point3D end = dragFinishProperty.get();

        double imgWidth = imgWidthProperty.get();
        double imgHeight = imgHeightProperty.get();

        double x = start.getX() / imgWidth;
        double y = start.getY() / imgHeight;
        double w = (end.getX() - start.getX()) / imgWidth;
        double h = (end.getY() - start.getY()) / imgHeight;

        // TODO: Return if rectangle is too small

        Screen screen = screenProperty.get();
        Frame frame = new Frame(screen.getScreenSize());
        frame.setStartX(x);
        frame.setStartY(y);
        frame.setSizeX(w);
        frame.setSizeY(h);
        frame.setTransitionDuration(1);
        screen.addFrame(frame);
        showEditor(screen);

        // Propagate up
        newFrameCallback.accept(frame);
    }

    public void showEditor(Screen screen) {
        // Called by rootController.
        screenProperty.set(screen);
        framesProperty.setAll(screen.getFrames());
    }

    private void onScreenChanged(ObservableValue<? extends Screen> observable, Screen oldScreen, Screen newScreen) {
        // Triggered by showEditor, whenever screen is changed
        try {
            imageDisplay.setImage(new Image(new FileInputStream(newScreen.getImage())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onFramesChanged(ListChangeListener.Change<? extends Frame> change) {
        // Triggered by showEditor, whenever frames are added/removed/reordered
        createVisualFrames((List<Frame>) change.getList());
    }

    private void highlightFrameChanged(ObservableValue<? extends Frame> observable, Frame oldValue, Frame newValue) {
        createVisualFrames(framesProperty);
    }

    private void createVisualFrames(List<Frame> newFrames) {
        ObservableList<Node> frames = framesContainer.getChildren();

        frames.setAll(
                Stream.concat(
                    // TODO: Sort the highlightedFrame last
                    newFrames.stream().map((Frame f) -> buildFrameRect(f)),
                    // Numbers on top
                    newFrames.stream().map((Frame f) -> buildFrameText(f))
                ).collect(Collectors.toUnmodifiableList())
        );
    }

    private Node buildFrameText(Frame f) {
        // Display frame number
        Text t = new Text(String.format("%d", f.getId()));
        t.setTextOrigin(VPos.BOTTOM);
        t.setFont(Font.font("sans-serif", 32.0));
        t.setFill(Color.YELLOW);
        t.setStroke(Color.BLACK);
        t.xProperty().bind(imgWidthProperty.multiply(f.getStartX()));
        t.yProperty().bind(imgHeightProperty.multiply(f.getStartY() + f.getSizeY()));
        return t;
    }

    private Node buildFrameRect(Frame f) {
        // Draw frame around selected area
        Rectangle r = new Rectangle();
        r.setBlendMode(BlendMode.SRC_ATOP);
        if (f == highlightFrame.get()) {
            // Chartreuse
            r.setFill(new Color(0.49803922f, 1.0f, 0.0f, 0.5).brighter());
            r.setStroke(new Color(0.49803922f, 1.0f, 0.0f, 1.0));
        } else {
            r.setFill(Color.gray(0.4, 0.5));
            r.setStroke(Color.BLACK);
        }
        r.getStrokeDashArray().setAll(0.5, 0.5);

        r.widthProperty().bind(imgWidthProperty.multiply(f.getSizeX()));
        r.heightProperty().bind(imgHeightProperty.multiply(f.getSizeY()));
        r.xProperty().bind(imgWidthProperty.multiply(f.getStartX()));
        r.yProperty().bind(imgHeightProperty.multiply(f.getStartY()));
        return r;
    }

    @Deprecated
    public void touchUI() {

    }
}
