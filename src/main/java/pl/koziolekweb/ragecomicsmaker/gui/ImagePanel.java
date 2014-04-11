package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.FrameSizeCalculator;
import pl.koziolekweb.ragecomicsmaker.event.AddFrameEvent;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEventListener;
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
import java.util.TreeSet;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class ImagePanel extends JPanel implements ImageSelectedEventListener {

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
					AddFrameEvent addFrameEvent = new AddFrameEvent(fsc.buildFrameRec(startX, startY,
							endX, endY,
							scaledInstance.getWidth(null), scaledInstance.getHeight(null)), selectedScreen);
					App.EVENT_BUS.post(addFrameEvent);
					repaint();
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
		if (image != null) {
			scaledInstance = image.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
			graphics.drawImage(scaledInstance, 0, 0, null);
			if (paintNewFrame) {
				rdm.setColor(new Color(200, 200, 200, 200));
				rdm.paintRectangle(graphics, startX, startY, currentX, currentY);
			}
		}
		if (selectedScreen != null) {
			TreeSet<pl.koziolekweb.ragecomicsmaker.model.Frame> frames = selectedScreen.getFrames();
			if (!frames.isEmpty()) {
				int r = 30;
				int g = 30;
				int b = 30;
				int a = 100;
				for (Frame frame : frames) {
					int scaledInstanceWidth = scaledInstance.getWidth(null);
					int scaledInstanceHeight = scaledInstance.getHeight(null);
					rdm.setColor(new Color(r, g, b, a));
					int sx = fsc.calculateSize(frame.getStartX(), scaledInstanceWidth);
					int w = fsc.calculateSize(frame.getSizeX(), scaledInstanceWidth);

					int sy = fsc.calculateSize(frame.getStartY(), scaledInstanceHeight);
					int h = fsc.calculateSize(frame.getSizeY(), scaledInstanceHeight);
					rdm.paintRectangle(graphics, sx, sy, w, h);
					r += 30;
					g += 30;
					b += 30;
				}
			}
		}
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
}
