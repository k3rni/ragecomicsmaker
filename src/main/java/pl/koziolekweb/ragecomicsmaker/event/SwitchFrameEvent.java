package pl.koziolekweb.ragecomicsmaker.event;

import pl.koziolekweb.ragecomicsmaker.model.Frame;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class SwitchFrameEvent {

	public final Frame frame;
	public final Direction direction;

	public SwitchFrameEvent(Frame frame, Direction direction) {
		this.frame = frame;
		this.direction = direction;
	}

	public enum Direction {UP, DOWN}
}
