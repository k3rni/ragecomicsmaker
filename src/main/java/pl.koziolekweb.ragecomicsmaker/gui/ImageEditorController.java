package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEvent;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static pl.koziolekweb.ragecomicsmaker.gui.ZoomToCursor.scrollOffsets;
import static pl.koziolekweb.ragecomicsmaker.gui.ZoomToCursor.zoomPivot;

public class ImageEditorController implements FrameManager {
    @FXML
    public ImageView imageDisplay;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public AnchorPane stack;
    @FXML
    public Pane framesContainer;
    @FXML
    public Canvas frameCanvas;

    public Consumer<Frame> newFrameCallback;

    private SimpleDoubleProperty imgWidthProperty = new SimpleDoubleProperty();
    private SimpleDoubleProperty imgHeightProperty = new SimpleDoubleProperty();
    private SimpleObjectProperty<Screen> screenProperty = new SimpleObjectProperty<>();
    private ObservableList<Frame> framesProperty = FXCollections.observableArrayList();
    private SimpleDoubleProperty zoomProperty = new SimpleDoubleProperty(0d);
    public SimpleObjectProperty<Frame> highlightFrame = new SimpleObjectProperty<>();

    private DrawFrames drawFrames;

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
        scrollPane.widthProperty().addListener(this::scrollWidthChanged);
        scrollPane.heightProperty().addListener(this::scrollHeightChanged);

        // Wheel event must attach to canvas, which is over the image
        frameCanvas.setOnScroll(this::onScroll);

        frameCanvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY)
                drawFrames.onMousePressed(e);
            else if (e.getButton() == MouseButton.SECONDARY)
                scrollPane.setPannable(true);
        });
        frameCanvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY)
                drawFrames.onDragMovement(e);
        });
        frameCanvas.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY)
                drawFrames.onMouseReleased(e);
            else if (e.getButton() == MouseButton.SECONDARY)
                scrollPane.setPannable(false);
        });

        drawFrames = new DrawFrames(frameCanvas, this);

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
    }

    private void onImageResize(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds newBounds) {
        // Whenever image size changes (zoom or window resize), update these properties, which are then used
        // to appropriately scale frames.
        this.imgWidthProperty.set(newBounds.getWidth());
        this.imgHeightProperty.set(newBounds.getHeight());
    }

    public void fit() {
        // Reset zoom and fit
        zoomProperty.set(0d);

        double width = scrollPane.getWidth();
        double height = scrollPane.getHeight();
        imageDisplay.setFitWidth(width);
        imageDisplay.setFitHeight(height);
        // XXX: Is this necessary?
        framesContainer.setPrefWidth(width);
        framesContainer.setPrefHeight(height);
    }

    private void onScroll(ScrollEvent scrollEvent) {
        // Handle mousewheel events
        if (!scrollEvent.isControlDown()) return;
        if (scrollEvent.getDeltaY() == 0) return;

        Bounds viewport = scrollPane.getViewportBounds();
        Bounds img = imageDisplay.getBoundsInLocal();
        double x = scrollEvent.getX() / img.getWidth();
        double y = scrollEvent.getY() / img.getHeight();
        Point2D cursor = new Point2D(x, y);

        double z = scrollToZoom(scrollEvent);
        zoomProperty.set(z);

        // Eat the event, hiding it from the ScrollPane
        scrollEvent.consume();

        Point2D pivot = zoomPivot(cursor, img, viewport, scrollPane.getHvalue(), scrollPane.getVvalue());

        // Resize image content and frame container to new dimensions.
        Image image = imageDisplay.getImage();
        double w = image.getWidth() * z;
        double h = image.getHeight() * z;
        imageDisplay.setFitWidth(w);
        imageDisplay.setFitHeight(h);
        // Not sure if necessary
        framesContainer.setPrefWidth(w);
        framesContainer.setPrefHeight(h);

        // Rescaling done, calculate scroll offset so that image does not move under cursor (if possible)
        // Note that imageDisplay's bounds have changed now.
        Point2D offsets = scrollOffsets(cursor, pivot, imageDisplay.getBoundsInLocal(), viewport);

        moveScrollbars(offsets.getX(), offsets.getY());
    }

    /**
     * Setting new values in the ScrollPane needs to be done after it has
     * recalculated itself to the new content. Using a Timeline animation for that.
     *
     * @param hv new hValue to set
     * @param vv new vValue to set
     */
    private void moveScrollbars(double hv, double vv) {
        Timeline tl = new Timeline();
        tl.setCycleCount(1);
        tl.getKeyFrames().add(
                new KeyFrame(Duration.millis(17), // Assuming 60fps, wait one frame
                        new KeyValue(scrollPane.hvalueProperty(), clamp(0.0, hv, 1.0)),
                        new KeyValue(scrollPane.vvalueProperty(), clamp(0.0, vv, 1.0))
                )
        );
        tl.play();
    }

    /**
     * Zoom gesture: take current zoom value and scroll wheel travel, calculate new zoom.
     *
     * @param scrollEvent wheel event
     * @return new zoom value
     */
    private double scrollToZoom(ScrollEvent scrollEvent) {
        final double step = Math.sqrt(2.0);
        double z = zoomProperty.doubleValue();
        boolean zoomIn = scrollEvent.getDeltaY() > 0;

        // Zero means fit-to-screen. But it needs to be converted to an actual zoom value.
        if (Math.abs(z) < 0.01) z = getActualZoom();

        if (zoomIn) z *= step;
        else z /= step;

        return clamp(0.5, z, 4.0);
    }

    private double getActualZoom() {
        Image image = imageDisplay.getImage();
        return min(stack.getWidth() / image.getWidth(),
                stack.getHeight() / image.getHeight());
    }

    private double clamp(double min, double v, double max) {
        return min(max, max(min, v));
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
            App.EVENT_BUS.post(new ErrorEvent("Screen image file not found", e));
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
        framesContainer.getChildren().setAll(
                Stream.of(
                        // All frames but the highlighted one
                        newFrames.stream().filter(f -> f != highlightFrame.get()).map(this::buildFrameRect),
                        // Highlighted frame goes on top of every other one
                        newFrames.stream().filter(f -> f == highlightFrame.get()).map(this::buildFrameRect),
                        // Numbers last
                        newFrames.stream().map(this::buildFrameText)
                ).flatMap(frame -> frame) // flattens a stream of streams
                        .collect(Collectors.toUnmodifiableList())
        );
    }

    private Node buildFrameText(Frame f) {
        // Display frame number
        Text t = Visuals.buildFrameText(f);
        t.xProperty().bind(imgWidthProperty.multiply(f.getStartX()));
        t.yProperty().bind(imgHeightProperty.multiply(f.getStartY() + f.getSizeY()));
        return t;
    }

    private Node buildFrameRect(Frame f) {
        // Draw frame around selected area
        Rectangle rect = (f == highlightFrame.get()) ? Visuals.buildHighlightFrameRect()
                : Visuals.buildFrameRect();
        rect.widthProperty().bind(imgWidthProperty.multiply(f.getSizeX()));
        rect.heightProperty().bind(imgHeightProperty.multiply(f.getSizeY()));
        rect.xProperty().bind(imgWidthProperty.multiply(f.getStartX()));
        rect.yProperty().bind(imgHeightProperty.multiply(f.getStartY()));
        return rect;
    }

    private void scrollWidthChanged(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        zoomProperty.set(0);
        imageDisplay.setFitWidth(newValue.doubleValue());
    }

    private void scrollHeightChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        zoomProperty.set(0);
        imageDisplay.setFitHeight(newValue.doubleValue());
    }

    // FrameManager
    @Override
    public boolean ignoreFrameEvents() {
        return screenProperty.get() == null;
    }

    @Override
    public void createFrame(Point3D start, Point3D end) {
        double imgWidth = imgWidthProperty.get();
        double imgHeight = imgHeightProperty.get();
        Screen screen = screenProperty.get();
        int frameId = screen.getFrameCount();

        Optional.ofNullable(createNewFrame(frameId, start, end, imgWidth, imgHeight))
                .ifPresent(newFrame -> {
                    screen.addFrame(newFrame);
                    showEditor(screen);
                    // Propagate to parent controller
                    newFrameCallback.accept(newFrame);
                });
    }

    private Frame createNewFrame(int frameId, Point3D start, Point3D end, double imgWidth, double imgHeight) {
        if (end == null) return null;

        double sy = clamp(0, start.getY(), imgHeight);
        double ey = clamp(0, end.getY(), imgHeight);
        double sx = clamp(0, start.getX(), imgWidth);
        double ex = clamp(0, end.getX(), imgWidth);

        if (sx / imgWidth < 0.05 || sy / imgHeight < 0.05)
            return null;

        double top = min(sy, ey);
        double left = min(sx, ex);

        double width = Math.abs(ex - sx);
        double height = Math.abs(ey - sy);

        Frame frame = new Frame(frameId);
        frame.setStartX(left / imgWidth);
        frame.setStartY(top / imgHeight);
        frame.setSizeX(width / imgWidth);
        frame.setSizeY(height / imgHeight);
        frame.setTransitionDuration(1);
        return frame;
    }
}
