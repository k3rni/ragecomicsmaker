package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEventListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class ImagePanel extends JPanel implements ImageSelectedEventListener {

	private BufferedImage image;

	public ImagePanel() {
		super();
		App.EVENT_BUS.register(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			Image scaledInstance = image.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(scaledInstance, 0, 0, null);
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
