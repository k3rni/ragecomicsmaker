package pl.koziolekweb.ragecomicsmaker.gui;

import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.RemoveFrameEvent;
import pl.koziolekweb.ragecomicsmaker.model.Frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FramePanel extends JPanel {

	private final Frame frame;
	private final JButton removeBtn;
	private final FramePanel _this = this;

	public FramePanel(Frame frame) {
		GridLayout mgr = new GridLayout(1, 2);
		setLayout(mgr);
		this.frame = frame;
		add(new Label(this.frame.getRelativeArea()));
		removeBtn = new JButton("X");
		removeBtn.setSize(25, 25);
		add(removeBtn);
		removeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				App.EVENT_BUS.post(new RemoveFrameEvent(_this));
			}
		});
	}

	public Frame getFrame() {
		return frame;
	}
}
