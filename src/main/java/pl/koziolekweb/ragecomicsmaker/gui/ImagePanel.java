package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.FrameSizeCalculator;
import pl.koziolekweb.ragecomicsmaker.event.*;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class ImagePanel extends JPanel implements ImageSelectedEventListener, FrameDroppedEventListener {

    private BufferedImage image;
    private boolean paintNewFrame = false;
    private int startY;
    private int startX;
    private int endX;
    private int endY;
    private int currentX;
    private int currentY;

    private FrameSizeCalculator fsc = new FrameSizeCalculator();
    private RectangleDrawingMagic rdm = new RectangleDrawingMagic();
    private Screen selectedScreen;
    private Image scaledInstance;

    public ImagePanel() {
        super();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (image != null) {
                    paintNewFrame = true;
                    startX = e.getX();
                    startY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (image != null) {
                    paintNewFrame = false;
                    endX = e.getX();
                    endY = e.getY();
                    try {
                        AddFrameEvent addFrameEvent = new AddFrameEvent(fsc.buildFrameRec(startX, startY,
                                Math.abs(startX - endX), Math.abs(startY - endY),
                                scaledInstance.getWidth(null), scaledInstance.getHeight(null)), selectedScreen);
                        App.EVENT_BUS.post(addFrameEvent);
                    } finally {
                        repaint();
                    }
                }
            }

        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (image != null && paintNewFrame) {
                    currentX = e.getX();
                    currentY = e.getY();
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        BufferedImage off = new BufferedImage(getWidth(), getHeight(), TYPE_4BYTE_ABGR);
        Graphics buffer = off.getGraphics();
        if (image != null) {
            scaledInstance = image.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            buffer.drawImage(scaledInstance, 0, 0, null);
            if (paintNewFrame) {
                rdm.setColor(new Color(200, 200, 200, 200));
                rdm.paintRectangle(buffer, startX, startY, currentX, currentY);
            }
        }
        if (selectedScreen != null) {
            Collection<Frame> frames = selectedScreen.getFrames();
            if (!frames.isEmpty()) {
                for (Frame frame : frames) {
                    int scaledInstanceWidth = scaledInstance.getWidth(null);
                    int scaledInstanceHeight = scaledInstance.getHeight(null);
                    int sx = fsc.calculateSize(frame.getStartX(), scaledInstanceWidth);
                    int w = fsc.calculateSize(frame.getSizeX(), scaledInstanceWidth);
                    int sy = fsc.calculateSize(frame.getStartY(), scaledInstanceHeight);
                    int h = fsc.calculateSize(frame.getSizeY(), scaledInstanceHeight);

                    rdm.setColor(new Color(130, 130, 130, 100));
                    rdm.paintFrame(buffer, sx, sy, w, h);
                    rdm.paintFrameNumber(frame.getId() + "", buffer, sx, sy, w, h);
                }
            }
        }
        graphics.drawImage(off, 0, 0, null);
    }

    @Override
    @Subscribe
    public void handleDirSelectedEvent(ImageSelectedEvent event) {
        try {
            selectedScreen = event.selectedScreen;
            image = ImageIO.read(selectedScreen.getImage());
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Subscribe
    public void handleFrameDroppedEvent(FrameDroppedEvent event) {
        repaint();
    }
}
