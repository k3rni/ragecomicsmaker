package pl.koziolekweb.ragecomicsmaker.gui;

import javax.swing.*;
import java.awt.*;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class ImagePanel extends JPanel {

	public ImagePanel() {
		super();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRect(0, 0, 100, 100);
	}
}
