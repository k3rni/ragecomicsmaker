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
		GridBagLayout mgr = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(mgr);
		this.frame = frame;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;

		add(new Label(this.frame.getRelativeArea()), gbc);

		removeBtn = new JButton("X");
		removeBtn.setSize(25, 25);
		gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(removeBtn, gbc);
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
