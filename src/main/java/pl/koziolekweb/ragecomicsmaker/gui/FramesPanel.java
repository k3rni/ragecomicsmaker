package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import pl.koziolekweb.ragecomicsmaker.event.AddFrameEvent;
import pl.koziolekweb.ragecomicsmaker.event.AddFrameEventListener;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEventListener;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeSet;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FramesPanel extends JPanel implements ImageSelectedEventListener, AddFrameEventListener {

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
	@Subscribe
	public void handleDirSelectedEvent(ImageSelectedEvent event) {
		TreeSet<Frame> frames = event.selectedScreen.getFrames();
		for (Frame frame : frames) {
			add(new Label(frame.getRelativeArea()));
		}
		updateUI();
	}

	@Override
	@Subscribe
	public void handelAddFrameEvent(AddFrameEvent event) {
		Screen screen = event.screen;
		Frame frame = new Frame(screen.getFrames().size());
		frame.setStartX(event.frameRect.startX);
		frame.setStartY(event.frameRect.startY);
		frame.setSizeX(event.frameRect.width);
		frame.setSizeY(event.frameRect.height);
		frame.setTransitionDuration(1);
		add(new FramePanel(frame));
		screen.addFrame(frame);
		updateUI();
	}
}

