package pl.koziolekweb.ragecomicsmaker.event;

import com.google.common.eventbus.Subscribe;

/**
 * Created by bartlomiej.kuczynski on 2014-04-20.
 */
public interface FrameStateChangeEventListener {

	@Subscribe
	public void handelFrameStateChangeEvent(FrameStateChangeEvent event);
}
