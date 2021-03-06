package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import pl.koziolekweb.ragecomicsmaker.model.Frame;

import java.util.List;
import java.util.function.Consumer;

class FrameListCell extends ListCell<Frame> {
    private final SnapshotParameters snapshotParameters = new SnapshotParameters();
    private final Callback<Frame, Node> nodeCreator;
    private final Consumer<List<Frame>> reorderCallback;

    public FrameListCell(Consumer<FrameListCell> mouseTracker,
                         Callback<Frame, Node> nodeCreator,
                         Consumer<List<Frame>> reorderCallback
    ) {
        super();
        this.nodeCreator = nodeCreator;
        this.reorderCallback = reorderCallback;
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        setOnMouseEntered(e -> mouseTracker.accept(this));
        setOnMouseExited(e -> mouseTracker.accept(null));

        configureDnd();
    }

    @Override
    protected void updateItem(Frame item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (item == null || empty) setGraphic(null);
        else setGraphic(nodeCreator.call(item));
    }

    private void configureDnd() {
        ListCell<Frame> thisCell = this;

        setOnDragDetected(event -> {
            if (getItem() == null) return;

            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(getItem().getId()));

            dragboard.setDragView(thisCell.snapshot(snapshotParameters, null));
            dragboard.setContent(content);
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
                setOpacity(0.5);
            }
        });

        setOnDragExited(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
                setOpacity(1.0);
            }
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        setOnDragDropped(event -> {
            if (getItem() == null) {
                // TODO: Add onDragDetected to frame editor component, so that it accepts dnd?
                event.setDropCompleted(dragOutside(event));
            } else {
                event.setDropCompleted(dragToReorder(event));
            }

            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }

    private boolean dragToReorder(DragEvent event) {
        Dragboard drag = event.getDragboard();
        boolean success = false;

        if (drag.hasString()) {
            ObservableList<Frame> items = getListView().getItems();

            int draggedId = Integer.parseInt(drag.getString());
            Frame draggedItem = items.stream().filter(obj -> obj.getId() == draggedId).findFirst().get();

            Frame targetItem = getItem();
            int targetId = targetItem.getId();

            targetItem.setId(draggedId);
            draggedItem.setId(targetId);

            success = true;
            reorderCallback.accept(items);
        }

        return success;
    }

    private boolean dragOutside(DragEvent event) {
        Dragboard drag = event.getDragboard();
        boolean success = false;

        if (drag.hasString()) {
            ObservableList<Frame> items = getListView().getItems();

            int draggedId = Integer.parseInt(drag.getString());
            Frame draggedItem = items.stream().filter(obj -> obj.getId() == draggedId).findFirst().get();

            items.remove(draggedItem);
            success = true;
            reorderCallback.accept(items);
        }

        return success;
    }
}
