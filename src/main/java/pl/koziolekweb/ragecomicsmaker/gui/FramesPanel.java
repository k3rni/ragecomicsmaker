package pl.koziolekweb.ragecomicsmaker.gui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FramesPanel extends JPanel {

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
}
