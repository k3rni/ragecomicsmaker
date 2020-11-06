package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.AddFrameEvent;
import pl.koziolekweb.ragecomicsmaker.event.AddFrameEventListener;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEventListener;
import pl.koziolekweb.ragecomicsmaker.event.FrameDroppedEvent;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEventListener;
import pl.koziolekweb.ragecomicsmaker.event.RemoveFrameEvent;
import pl.koziolekweb.ragecomicsmaker.event.RemoveFrameEventListener;
import pl.koziolekweb.ragecomicsmaker.event.SwitchFrameEvent;
import pl.koziolekweb.ragecomicsmaker.event.SwitchFrameEventListener;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.swing.*;
import java.awt.*;

import static pl.koziolekweb.ragecomicsmaker.event.SwitchFrameEvent.Direction.UP;

/**
 * User: koziolek
 */
@SuppressWarnings("UnstableApiUsage")
public class FramesPanel extends JPanel implements ImageSelectedEventListener, AddFrameEventListener,
		RemoveFrameEventListener, DirSelectedEventListener, SwitchFrameEventListener {

	private final GridBagConstraints gbc;

	private Screen selectedScreen;

	public FramesPanel() {
		super(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridy = 0;
	}

	@Override
	@Subscribe
	public void handleDirSelectedEvent(ImageSelectedEvent event) {
		removeAll();
		gbc.gridy = 0;
		selectedScreen = event.selectedScreen;
		selectedScreen.getFrames().forEach(this::addFrame);
		updateUI();
	}

	private void addFrame(Frame frame) {
		gbc.gridx = 0;
		gbc.gridy = gbc.gridy + 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		FramePanel framePanel = new FramePanel(frame, frame.getId() == selectedScreen.getScreenSize() - 1);
		add(framePanel, gbc);
	}

	@Override
	@Subscribe
	public void handleAddFrameEvent(AddFrameEvent event) {
		selectedScreen = event.screen;
		Frame frame = new Frame(selectedScreen.getScreenSize());
		frame.setStartX(event.frameRect.startX);
		frame.setStartY(event.frameRect.startY);
		frame.setSizeX(event.frameRect.width);
		frame.setSizeY(event.frameRect.height);
		frame.setTransitionDuration(1);
		addFrame(frame);
		selectedScreen.addFrame(frame);
		updateUI();
	}

	@Override
	@Subscribe
	public void handleRemoveFrameEvent(RemoveFrameEvent event) {
		remove(event.framePanel);
		selectedScreen.removeFrame(event.framePanel.getFrame());
		App.EVENT_BUS.post(new FrameDroppedEvent(event.framePanel.getFrame()));
		updateUI();
	}

	@Override
	public void handleDirSelectedEvent(DirSelectedEvent event) {
		removeAll();
	}

	@Override
	@Subscribe
	public void handelSwitchFrameEvent(SwitchFrameEvent event) {
		int currentId = event.frame.getId();
		if (event.direction == UP) {
			if (currentId == 0) return;
			selectedScreen.moveFrameUp(event.frame);
		} else {
			if (currentId == selectedScreen.getScreenSize()) return;
			selectedScreen.moveFrameDown(event.frame);
		}
		App.EVENT_BUS.post(new ImageSelectedEvent(null, selectedScreen));

	}
}

