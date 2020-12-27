package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class DrawFrames {
    private final SimpleObjectProperty<Point3D> dragOriginProperty = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Point3D> dragFinishProperty = new SimpleObjectProperty<>();
    private final Canvas target;
    private final FrameManager manager;

    public DrawFrames(Canvas target, FrameManager manager) {
        this.target = target;
        this.manager = manager;

        dragFinishProperty.addListener(this::onDragProgress);
    }

    void onMousePressed(MouseEvent e) {
        // Maybe start a drag-rectangle gesture
        if (e.getButton() != MouseButton.PRIMARY) return;
        if (manager.ignoreFrameEvents()) return;

        dragOriginProperty.set(e.getPickResult().getIntersectedPoint());
    }

    void onMouseReleased(MouseEvent e) {
        // End a drag-rectangle gesture
        if (e.getButton() != MouseButton.PRIMARY) return;
        if (manager.ignoreFrameEvents()) return;

        Point3D start = dragOriginProperty.get();
        Point3D end = dragFinishProperty.get();
        manager.createFrame(start, end);

        dragOriginProperty.set(null);
        dragFinishProperty.set(null);

        GraphicsContext gc = target.getGraphicsContext2D();
        gc.clearRect(0, 0, target.getWidth(), target.getHeight());
    }

    void onDragMovement(MouseEvent e) {
        // While dragging, update finish point. This is then painted by appropriate listeners.
        if (manager.ignoreFrameEvents()) return;
        if (!e.isPrimaryButtonDown()) return;

        dragFinishProperty.set(e.getPickResult().getIntersectedPoint());
    }

    private void onDragProgress(ObservableValue<? extends Point3D> observable, Point3D oldValue, Point3D newValue) {
        // Draw rectangle between current start and finish points.
        if (newValue == null) return;
        if (manager.ignoreFrameEvents()) return;

        Visuals.drawNewSelection(target, dragOriginProperty.get(), newValue);
    }
}
