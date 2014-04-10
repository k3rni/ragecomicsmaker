package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEventListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			Image scaledInstance = image.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(scaledInstance, 0, 0, null);
			if (paintNewFrame) {
				g.setColor(new Color(200, 200, 200, 200));
				g.fillRect(startX, startY, currentX - startX, currentY - startY);
			}
		}
	}

	@Override
	@Subscribe
	public void handleDirSelectedEvent(ImageSelectedEvent event) {
		try {
			image = ImageIO.read(event.selectedScreen.getImage());
			repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
