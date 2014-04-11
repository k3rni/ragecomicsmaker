package pl.koziolekweb.ragecomicsmaker.gui;

import pl.koziolekweb.ragecomicsmaker.model.Frame;

import javax.swing.*;
import java.awt.*;

public class FramePanel extends JPanel {

	private final GridLayout mgr;

	public FramePanel(Frame frame) {
		mgr = new GridLayout(1, 9);
		setLayout(mgr);
		add(new Label(frame.getRelativeArea()));
	}
}
