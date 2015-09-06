package pl.koziolekweb.ragecomicsmaker.model;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ScreenTest {

	@Test
	public void shouldMoveFrameUpMoveFrame() {
		// given
		Screen screen = new Screen();
		Frame frame1 = new Frame();
		Frame frame2 = new Frame();
		screen.addFrame(frame1);
		screen.addFrame(frame2);
		// when
		assertThat(screen.getFrames()).containsSequence(frame1, frame2);
		screen.moveFrameUp(frame2);
		// then
		assertThat(screen.getFrames()).containsSequence(frame2, frame1);
	}

	@Test
	public void shouldMoveFrameDownMoveFrame() {
		// given
		Screen screen = new Screen();
		Frame frame1 = new Frame();
		Frame frame2 = new Frame();
		screen.addFrame(frame1);
		screen.addFrame(frame2);
		// when
		assertThat(screen.getFrames()).containsSequence(frame1, frame2);
		screen.moveFrameDown(frame1);
		// then
		assertThat(screen.getFrames()).containsSequence(frame2, frame1);
	}
}