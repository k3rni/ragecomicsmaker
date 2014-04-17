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
     * We could start with different color
     *
     * @param color if you hate BLACK!
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
        g.fillRect(findStartPoint(startX, currentX),
                findStartPoint(startY, currentY),
                countSize(startX, currentX),
                countSize(startY, currentY)
        );

    }

    public void paintFrame(Graphics g, int startX, int startY, int currentX, int currentY) {
        g.setColor(color);
        g.fillRect(startX, startY, startX + currentX, startY + currentY);
    }

    /**
     * Paint rectangle using current color.
     *
     * @param number   frame number
     * @param g        canvas
     * @param startX   mouse cord - mouse pressed
     * @param startY   mouse cord - mouse pressed
     * @param currentX mouse cord - mouse current position
     * @param currentY mouse cord - mouse current position
     */
    public void paintFrameNumber(String number, Graphics g, int startX, int startY, int currentX, int currentY) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString(number, countPosition(startX, currentX), countPosition(startY, currentY));
    }

    private int countPosition(int start, int current) {
        return (start + current) / 2;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private int findStartPoint(int start, int current) {
        return Math.min(start, current);
    }

    private int countSize(int start, int current) {
        return Math.abs(start - current);
    }

}
