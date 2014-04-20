package pl.koziolekweb.ragecomicsmaker.event;

import pl.koziolekweb.ragecomicsmaker.model.Frame;

/**
 * Created by bartlomiej.kuczynski on 2014-04-20.
 */
public class FrameStateChangeEvent {

    public final Frame frame;

    public FrameStateChangeEvent(Frame frame) {
        this.frame = frame;
    }
}
