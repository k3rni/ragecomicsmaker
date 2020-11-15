package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import pl.koziolekweb.ragecomicsmaker.event.AddFrameEvent;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

public class FrameEditorController {
    @FXML AnchorPane imageEditorAnchor;
    ImagePanel imagePanel;

    public Consumer<Frame> newFrameCallback;

    @FXML
    public void initialize() {
        loadImagePanel();
    }

    public void touchUI() {
        imagePanel.repaint();
    }

    public void showEditor(Screen screen) {
        try {
            imagePanel.openScreen(screen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadImagePanel() {
        if (imagePanel != null)
            return;

        // Boilerplate to embed a Swing component. Calls setContent() on UI thread.
        final SwingNode node = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            imagePanel = new ImagePanel();
            node.setContent(imagePanel);
        });

        // Needed for the embedded node to resize properly
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);

        imageEditorAnchor.getChildren().add(node);
    }

    @Subscribe
    public void handleAddFrameEvent(AddFrameEvent event) {
        Screen screen = event.screen;
        Frame frame = new Frame(screen.getScreenSize());
        frame.setStartX(event.frameRect.startX);
        frame.setStartY(event.frameRect.startY);
        frame.setSizeX(event.frameRect.width);
        frame.setSizeY(event.frameRect.height);
        frame.setTransitionDuration(1);
        screen.addFrame(frame);
        //touchUI();
        newFrameCallback.accept(frame);
    }
}
