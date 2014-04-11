package pl.koziolekweb.ragecomicsmaker.gui;

import java.awt.*;

/**
 * Because painting in java is fucking magic... when you would like to do it using mouse.
 * User: koziolek
 */
public class RectangleDrawingMagic {

	private Color color;

	/**
	 * Default color is BLACK!!! Because this class ist Krieg!
	 */
	public RectangleDrawingMagic() {
		color = new Color(0, 0, 0);
	}

	/**
	 * We could start with different  color
	 *
	 * @param color
	 */
	public RectangleDrawingMagic(Color color) {
		this.color = color;
	}

	/**
	 * Paint rectangle using current color.
	 *
	 * @param g        canvas
	 * @param startX   mouse cord - mouse pressed
	 * @param startY   mouse cord - mouse pressed
	 * @param currentX mouse cord - mouse current position
	 * @param currentY mouse cord - mouse current position
	 */
	public void paintRectangle(Graphics g, int startX, int startY, int currentX, int currentY) {
		g.setColor(color);
		g.fillRect(findStartXPoint(startX, currentX),
				findStartYPoint(startY, currentY),
				countWidth(startX, currentX),
				countHeight(startY, currentY)
		);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	private int findStartXPoint(int startX, int currentX) {
		return Math.min(startX, currentX);
	}

	private int findStartYPoint(int startY, int currentY) {
		return Math.min(startY, currentY);
	}

	public int countWidth(int startX, int currentX) {
		return Math.abs(startX - currentX);
	}

	public int countHeight(int startY, int currentY) {
		return Math.abs(startY - currentY);
	}
}
