package pl.koziolekweb.ragecomicsmaker.gui;

import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.FrameStateChangeEvent;
import pl.koziolekweb.ragecomicsmaker.event.RemoveFrameEvent;
import pl.koziolekweb.ragecomicsmaker.model.Frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FramePanel extends JPanel {

	private final Frame frame;
	private final JButton removeBtn;
	private final FramePanel _this = this;

	public FramePanel(final Frame frame) {
		GridBagLayout mgr = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(mgr);
		this.frame = frame;
		int i = 0;

		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = i;
		gbc.gridy = 0;
		add(new JLabel(this.frame.getId() + "."), gbc);

		final JCheckBox visibility = new JCheckBox((String) null, frame.isVisible());
		visibility.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (visibility.isSelected())
					_this.frame.visible();
				else
					_this.frame.unvisible();
				App.EVENT_BUS.post(new FrameStateChangeEvent(_this.frame));
			}
		});

		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = ++i;
		gbc.gridy = 0;
		add(visibility, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = ++i;
		gbc.gridy = 0;
		Label comp = new Label(this.frame.getRelativeArea());
		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					new FrameEditorFrame(FramePanel.this.frame);
			}
		});
		add(comp, gbc);

		removeBtn = new JButton("X");
		removeBtn.setSize(25, 25);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = ++i;
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
