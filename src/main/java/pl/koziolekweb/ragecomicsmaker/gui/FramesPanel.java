package pl.koziolekweb.ragecomicsmaker.gui;

import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEventListener;
import pl.koziolekweb.ragecomicsmaker.model.Frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeSet;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FramesPanel extends JPanel implements ImageSelectedEventListener {

	public FramesPanel() {
		super();
		JButton addFrame = new JButton("Add Frame");
		add(addFrame);

		addFrame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				add(new JLabel("wqeqqeweqqeqeqq"));
				updateUI();
			}
		});
	}

	@Override
	public void handleDirSelectedEvent(ImageSelectedEvent event) {
		TreeSet<Frame> frames = event.selectedScreen.getFrames();
		for (Frame frame : frames) {
			add(new Label(frame.getRelativeArea()));
		}
		updateUI();
	}
}
