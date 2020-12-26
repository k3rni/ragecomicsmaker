package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

public class ZoomToCursor {
    /*
     *  Zoom behavior: given screen-relative sx,sy and image-relative ix,iy; both represent the same point
     *  in different coordinate sets. Which means that there exists a transformation from one to the other.
     *  After zooming, they should still map to the same point, scrollpane bounds permitting.
     *  sx,sy do not change during the zoom gesture; but ix,iy do change.
     *  So we need to calculate new ix,iy and then adjust hvalue and vvalue so they map the same.
     */

    /*
       Unit-scaled: a value bounded to (0..1), linearly scaled from some other dimension. For example, cursor
       is a x,y pair, where the original mouse pointer location is scaled by the content's width and height.
     */

    /**
     * Always called before scaling. Calculates a parameter needed to adjust scrollbars later.
     *
     * @param cursor   Unit-scaled pointer coordinates, relative to scrolled/zoomed content
     * @param img      Bounds of the zoomable content
     * @param viewport Bounds of the ScrollPane viewport
     * @param hValue   ScrollPane's hValue
     * @param vValue   ScrollPane's vValue
     * @return Unit-scaled pointer coordinates, but relative to viewport and not content.
     */
    public static Point2D zoomPivot(Point2D cursor, Bounds img, Bounds viewport, double hValue, double vValue) {
        double x = cursor.getX();
        double y = cursor.getY();

        // Scrollpane offsets, pre-zoom, pixels
        double ox = (img.getWidth() - viewport.getWidth()) * hValue;
        double oy = (img.getHeight() - viewport.getHeight()) * vValue;
        // Scrollpane-relative, pre-zoom, unit-scaled
        double sx = (x * img.getWidth() - ox) / viewport.getWidth();
        double sy = (y * img.getHeight() - oy) / viewport.getHeight();

        return new Point2D(sx, sy);
    }

    /**
     * Always called after scaling. Calculates the new scrollbar positions.
     *
     * @param cursor   Unit-scaled pointer coordinates, relative to scrolled/zoomed content
     * @param pivot    Unit-scaled pointer coordinates BEFORE scaling was applied; relative to viewport.
     * @param img      Bounds of the zoomable content, AFTER scaling
     * @param viewport Bounds of the ScrollPane viewport
     * @return a Point2D containing hValue in X and vValue in Y. Set these on the ScrollPane.
     */
    public static Point2D scrollOffsets(Point2D cursor, Point2D pivot, Bounds img, Bounds viewport) {
        double x = cursor.getX();
        double y = cursor.getY();
        double sx = pivot.getX();
        double sy = pivot.getY();
        // POST-ZOOM
        // Viewport did not change, but img did.
        // We need to calculate required ox,oy from sx, sy given the new conditions
        // sx = (x * w - ox) / vw ⇒
        // sx * vw = x * w - ox ⇒
        // sx * vw - x * w = -ox ⇒
        // ox = x * w - sx * vw
        double ox = x * img.getWidth() - sx * viewport.getWidth();
        double oy = y * img.getHeight() - sy * viewport.getHeight();
        // And from these, we calculate hvalue and vvalue
        // ox = (w - vw) * hv
        // hv = ox / (w - vw)
        final double hv = ox / (img.getWidth() - viewport.getWidth());
        final double vv = oy / (img.getHeight() - viewport.getHeight());

        return new Point2D(hv, vv);
    }
}
