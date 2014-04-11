package pl.koziolekweb.ragecomicsmaker.event;

import pl.koziolekweb.ragecomicsmaker.model.Screen;

import static com.google.common.base.Preconditions.checkArgument;

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

	public static class FrameRect {

		public final double startX;
		public final double startY;
		public final double width;
		public final double height;

		public FrameRect(double startX, double startY, double width, double height) {
			checkArgument(startX <= 1.);
			checkArgument(startX >= 0.);
			checkArgument(startY <= 1.);
			checkArgument(startY >= 0.);
			checkArgument(width <= 1.);
			checkArgument(width >= 0.);
			checkArgument(height <= 1.);
			checkArgument(height >= 0.);
			this.startX = startX;
			this.startY = startY;
			this.width = width;
			this.height = height;
		}
	}
}
