package pl.koziolekweb.ragecomicsmaker.event;

import com.google.common.eventbus.Subscribe;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
@SuppressWarnings("UnstableApiUsage")
public interface DirSelectedEventListener {
	@Subscribe
	void handleDirSelectedEvent(DirSelectedEvent event);

}
