package pl.koziolekweb.ragecomicsmaker;


import com.formdev.flatlaf.FlatDarkLaf;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEvent;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEventListener;
import pl.koziolekweb.ragecomicsmaker.gui.FilesPanel;
import pl.koziolekweb.ragecomicsmaker.gui.FramesPanel;
import pl.koziolekweb.ragecomicsmaker.gui.ImagePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 */
public class App implements Runnable, ErrorEventListener {

	public static final EventBus EVENT_BUS = new EventBus();
	private JFrame main;

	public static void main(String[] args) {
		FlatDarkLaf.install();
		SwingUtilities.invokeLater(new App());
	}

	@Override
	public void run() {
		main = new JFrame("Rage Comics Maker");
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

		EVENT_BUS.register(imagePanel);
		EVENT_BUS.register(filesPanel);
		EVENT_BUS.register(framesPanel);
		EVENT_BUS.register(this);


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
		springLayout.putConstraint(SpringLayout.WEST, framesPanel, -350, SpringLayout.EAST, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, framesPanel, 5, SpringLayout.EAST, mainPanel);

	}

	@Override
	@Subscribe
	public void handleErrorEvent(ErrorEvent event) {
		JOptionPane.showConfirmDialog(main, event.message, "UWAGA!", JOptionPane.CLOSED_OPTION);
	}
}
