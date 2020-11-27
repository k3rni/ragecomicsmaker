package pl.koziolekweb.ragecomicsmaker.event;

import pl.koziolekweb.ragecomicsmaker.model.Screen;

/**
 * Event message - release mouse new frame on img.
 * User: koziolek
 */
public class AddFrameEvent {

	public final FrameRect frameRect;
	public final Screen screen;

	public AddFrameEvent(FrameRect frameRect, Screen screen) {
		this.frameRect = frameRect;
		this.screen = screen;
	}

	public boolean tooSmall() {
		return frameRect.width < 0.05 || frameRect.height < 0.05;
	}

	public static class FrameRect {
		public final double startX;
		public final double startY;
		public final double width;
		public final double height;

		public FrameRect(double startX, double startY, double width, double height) {
			this.startX = startX;
			this.startY = startY;
			this.width = width;
			this.height = height;
		}
	}
}
