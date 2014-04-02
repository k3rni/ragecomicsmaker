package pl.koziolekweb.ragecomicsmaker;

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
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setMinimumSize(new Dimension(640, 480));
		mainPanel(main);

		main.pack();
		main.setVisible(true);
	}

	private void mainPanel(JFrame main) {
		Container mainPanel = main.getContentPane();
		SpringLayout springLayout = new SpringLayout();
		mainPanel.setLayout(springLayout);

		JButton openDirBtn = new JButton("Select Directory");
		mainPanel.add(openDirBtn);

		JScrollPane fileTree = prepareFileTree(mainPanel);

		JButton saveBtn = new JButton("Save comics.xml");
		mainPanel.add(saveBtn);

		ImagePanel imagePanel = new ImagePanel();
		FramesPanel framesPanel = new FramesPanel();

		mainPanel.add(imagePanel);
		mainPanel.add(framesPanel);


		springLayout.putConstraint(SpringLayout.WEST, openDirBtn, 5, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, openDirBtn, 5, SpringLayout.NORTH, mainPanel);

		springLayout.putConstraint(SpringLayout.WEST, fileTree, 5, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, fileTree, 5, SpringLayout.SOUTH, openDirBtn);
		springLayout.putConstraint(SpringLayout.SOUTH, fileTree, -5, SpringLayout.NORTH, saveBtn);
		springLayout.putConstraint(SpringLayout.EAST, fileTree, -5, SpringLayout.EAST, openDirBtn);

		springLayout.putConstraint(SpringLayout.WEST, saveBtn, 5, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, saveBtn, -5, SpringLayout.SOUTH, mainPanel);

		springLayout.putConstraint(SpringLayout.NORTH, imagePanel, 5, SpringLayout.NORTH, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, imagePanel, -5, SpringLayout.SOUTH, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, imagePanel, 5, SpringLayout.EAST, openDirBtn);
		springLayout.putConstraint(SpringLayout.EAST, imagePanel, 5, SpringLayout.WEST, framesPanel);

		springLayout.putConstraint(SpringLayout.NORTH, framesPanel, 5, SpringLayout.NORTH, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, framesPanel, -5, SpringLayout.SOUTH, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, framesPanel, 5, SpringLayout.EAST, imagePanel);
		springLayout.putConstraint(SpringLayout.WEST, framesPanel, -250, SpringLayout.EAST, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, framesPanel, 5, SpringLayout.EAST, mainPanel);

	}


	private JScrollPane prepareFileTree(Container mainPanel) {
		JTree fileTree = new JTree(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 8, 9, 10, 11, 12, 13, 8, 9, 10, 11, 12, 13, 8, 9, 10, 11, 12, 13, 8, 9, 10, 11, 12, 13});
		JScrollPane jsp = new JScrollPane(fileTree);
		mainPanel.add(jsp);
		return jsp;
	}
}
