package pl.koziolekweb.ragecomicsmaker.event;

import com.google.common.eventbus.Subscribe;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public interface DirSelectedEventListener {

	@Subscribe
	public void handleDirSelectedEvent(DirSelectedEvent event);

}
