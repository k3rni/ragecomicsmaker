package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.FrameSizeCalculator;
import pl.koziolekweb.ragecomicsmaker.event.AddFrameEvent;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.FrameDroppedEvent;
import pl.koziolekweb.ragecomicsmaker.event.FrameStateChangeEvent;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
@SuppressWarnings("UnstableApiUsage")
public class ImagePanel extends JPanel {

	private BufferedImage image;
	private boolean paintNewFrame = false;
	private int startY;
	private int startX;
	private int endX;
	private int endY;
	private int currentX;
	private int currentY;

	private final FrameSizeCalculator fsc = new FrameSizeCalculator();
	private final RectangleDrawingMagic rdm = new RectangleDrawingMagic();
	private Screen selectedScreen;
	private Image scaledInstance;

	private Color frameColor = new Color(0x70_c8c8c8, true);

	public ImagePanel() {
		super();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				fitImage();
			}
		});

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
					if (!inImage(startX, startY) || !inImage(endX, endY)) {
						repaint();
						return;
					}
					try {
						AddFrameEvent addFrameEvent = new AddFrameEvent(fsc.buildFrameRec(
								Math.min(startX, endX), Math.min(startY, endY),
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
		if (image != null) paintImage(buffer);
		if (selectedScreen != null) paintFrames(buffer);
		graphics.drawImage(off, 0, 0, null);
		buffer.dispose();
	}

	private void paintFrames(Graphics buffer) {
		for (Frame frame : selectedScreen.getFrames()) {
			if (!frame.isVisible()) continue;

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

	private void paintImage(Graphics buf) {
		if (scaledInstance == null)
			fitImage();

		buf.drawImage(scaledInstance, 0, 0, null);

		if (paintNewFrame) {
			rdm.setColor(frameColor);
			rdm.paintRectangle(buf, startX, startY, currentX, currentY);
		}
	}

	private void fitImage() {
		if (image == null) return;

		int imgHeight = Integer.MAX_VALUE;
		int imgWidth = Integer.MAX_VALUE;
		double aspectRatio = calculateAspectRatio(image);

		if (aspectRatio > 1.0) {
			// Image is wider than it is tall. Reduce height.
			imgWidth = getWidth();
			imgHeight = (int) (imgWidth / aspectRatio);

			while (imgHeight > getHeight()) {
				double ratio = (double) getHeight() / imgHeight;
				imgWidth *= ratio;
				imgHeight = (int) (imgWidth / aspectRatio);
			}
		} else {
			// Image is taller than it is wide. Reduce width.
			imgHeight = getHeight();
			imgWidth = (int) (imgHeight * aspectRatio);

			while (imgWidth > getWidth()) {
				double ratio = (double) getWidth() / imgWidth;
				imgHeight *= ratio;
				imgWidth = (int) (imgHeight * aspectRatio);
			}
		}

		scaledInstance = image.getScaledInstance(imgWidth, imgHeight,
				Image.SCALE_SMOOTH);
	}

	private double calculateAspectRatio(BufferedImage image) {
		return image.getWidth() / (double) image.getHeight();
	}

	@Subscribe
	public void handleDirSelectedEvent(ImageSelectedEvent event) {
		try {
			openScreen(event.selectedScreen);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void openScreen(Screen selectedScreen) throws IOException {
		this.selectedScreen = selectedScreen;
		this.image = ImageIO.read(selectedScreen.getImage());
		repaint();
	}

	@Subscribe
	public void handleFrameDroppedEvent(FrameDroppedEvent event) {
		repaint();
	}

	@Subscribe
	public void handleDirSelectedEvent(DirSelectedEvent event) {
		this.image = null;
		this.scaledInstance = null;
		this.selectedScreen = null;
		this.paintNewFrame = false;
	}

	@Subscribe
	public void handleFrameStateChangeEvent(FrameStateChangeEvent event) {
		repaint();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean inImage(int posX, int posY) {
		return posX > 0 && posX <= scaledInstance.getWidth(null)
				&& posY > 0 && posY <= scaledInstance.getHeight(null);
	}
}
