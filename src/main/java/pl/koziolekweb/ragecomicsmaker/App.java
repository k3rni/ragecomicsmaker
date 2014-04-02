package pl.koziolekweb.ragecomicsmaker;

import pl.koziolekweb.ragecomicsmaker.gui.FilesPanel;
import pl.koziolekweb.ragecomicsmaker.gui.FramesPanel;
import pl.koziolekweb.ragecomicsmaker.gui.ImagePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 */
public class App implements Runnable {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new App());
	}

	@Override
	public void run() {
		JFrame main = new JFrame("Rage Comics Maker");
		main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		main.setMinimumSize(new Dimension(640, 480));
		mainPanel(main);

		main.pack();
		main.setVisible(true);
	}

	private void mainPanel(JFrame main) {
		Container mainPanel = main.getContentPane();
		SpringLayout springLayout = new SpringLayout();
		mainPanel.setLayout(springLayout);

		FilesPanel filesPanel = new FilesPanel();
		ImagePanel imagePanel = new ImagePanel();
		FramesPanel framesPanel = new FramesPanel();

		mainPanel.add(filesPanel);
		mainPanel.add(imagePanel);
		mainPanel.add(framesPanel);

		springLayout.putConstraint(SpringLayout.NORTH, filesPanel, 5, SpringLayout.NORTH, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, filesPanel, -5, SpringLayout.SOUTH, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, filesPanel, 5, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, filesPanel, 250, SpringLayout.WEST, mainPanel);


		springLayout.putConstraint(SpringLayout.NORTH, imagePanel, 5, SpringLayout.NORTH, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, imagePanel, -5, SpringLayout.SOUTH, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, imagePanel, 5, SpringLayout.EAST, filesPanel);
		springLayout.putConstraint(SpringLayout.EAST, imagePanel, 5, SpringLayout.WEST, framesPanel);

		springLayout.putConstraint(SpringLayout.NORTH, framesPanel, 5, SpringLayout.NORTH, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, framesPanel, -5, SpringLayout.SOUTH, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, framesPanel, 5, SpringLayout.EAST, imagePanel);
		springLayout.putConstraint(SpringLayout.WEST, framesPanel, -250, SpringLayout.EAST, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, framesPanel, 5, SpringLayout.EAST, mainPanel);

	}
}
