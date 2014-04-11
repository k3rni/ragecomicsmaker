package pl.koziolekweb.ragecomicsmaker.event;

import pl.koziolekweb.ragecomicsmaker.model.Frame;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FrameDroppedEvent {

	public final Frame frame;

	public FrameDroppedEvent(Frame frame) {
		this.frame = frame;
	}
}
