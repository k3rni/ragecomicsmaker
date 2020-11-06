package pl.koziolekweb.ragecomicsmaker.event;

import com.google.common.eventbus.Subscribe;

/**
 * Created by bartlomiej.kuczynski on 2014-04-20.
 */
@SuppressWarnings("UnstableApiUsage")
public interface FrameStateChangeEventListener {
	@Subscribe
    void handelFrameStateChangeEvent(FrameStateChangeEvent event);
}
