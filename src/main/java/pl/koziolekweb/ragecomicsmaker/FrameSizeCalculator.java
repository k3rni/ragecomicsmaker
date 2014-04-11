package pl.koziolekweb.ragecomicsmaker;

import pl.koziolekweb.ragecomicsmaker.event.AddFrameEvent;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Frame size calculations goes here.
 * User: koziolek
 */
public class FrameSizeCalculator {

	public double calculateProportion(int position, int maximum) {
		checkArgument(position >= 0);
		checkArgument(maximum >= 0);
		return (double) position / (double) maximum;
	}

	public AddFrameEvent.FrameRect buildFrameRec(int startX, int startY, int width, int height, int imgWidth, int imgHeight) {

		return new AddFrameEvent.FrameRect(
				calculateProportion(startX, imgWidth),
				calculateProportion(startY, imgHeight),
				calculateProportion(width, imgWidth),
				calculateProportion(height, imgHeight)
		);
	}

	public int calculateSize(double proportion, int maximum) {
		return (int) (proportion * maximum);
	}
}
