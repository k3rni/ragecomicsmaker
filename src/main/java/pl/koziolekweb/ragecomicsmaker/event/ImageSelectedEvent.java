package pl.koziolekweb.ragecomicsmaker.event;

import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class ImageSelectedEvent {
	public final Comic comic;
	public final Screen selectedScreen;

	public ImageSelectedEvent(Comic comic, Screen selectedScreen) {
		this.comic = comic;
		this.selectedScreen = selectedScreen;
	}
}
