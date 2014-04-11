package pl.koziolekweb.ragecomicsmaker.event;

import pl.koziolekweb.ragecomicsmaker.gui.FramePanel;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class RemoveFrameEvent {

	public final FramePanel framePanel;

	public RemoveFrameEvent(FramePanel framePanel) {
		this.framePanel = framePanel;
	}
}
