package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.geometry.Point3D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import pl.koziolekweb.ragecomicsmaker.model.Frame;

public class Visuals {
    static Font TEXT_FONT = Font.font("sans-serif", FontWeight.BLACK, 32.0);
    static Paint TEXT_COLOR = Color.YELLOW;
    static Paint TEXT_STROKE = Color.BLACK;

    static Paint FRAME_FILL = Color.gray(0.4, 0.5);
    static Paint FRAME_BORDER = Color.BLACK;

    // Chartreuse
    static Paint HIGHLIGHTED_FRAME_FILL = new Color(0.49803922f, 1.0f, 0.0f, 0.5).brighter();
    static Paint HIGHLIGHTED_FRAME_BORDER = new Color(0.49803922f, 1.0f, 0.0f, 1.0);

    // Crimson
    static Paint NEW_FRAME_BORDER = new Color(0.8627451f, 0.078431375f, 0.23529412f, 1.0).brighter();
    static Paint NEW_FRAME_FILL = new Color(0.8627451f, 0.078431375f, 0.23529412f, 0.5);


    public static Text buildFrameText(Frame f) {
        Text t = new Text(String.format("%d", f.getId()));
        t.setTextOrigin(VPos.BOTTOM);
        t.setFont(TEXT_FONT);
        t.setFill(TEXT_COLOR);
        t.setStroke(TEXT_STROKE);

        return t;
    }

    public static Rectangle buildFrameRect() {
        Rectangle r = new Rectangle();
        r.setBlendMode(BlendMode.SRC_ATOP);
        r.setFill(FRAME_FILL);
        r.setStroke(FRAME_BORDER);
        r.getStrokeDashArray().setAll(0.5, 0.5);

        return r;
    }

    public static Rectangle buildHighlightFrameRect() {
        Rectangle r = new Rectangle();
        r.setBlendMode(BlendMode.SRC_ATOP);
        r.setFill(HIGHLIGHTED_FRAME_FILL);
        r.setStroke(HIGHLIGHTED_FRAME_BORDER);
        r.getStrokeDashArray().setAll(0.5, 0.5);
        return r;
    }

    public static void drawNewSelection(Canvas canvas, Point3D start, Point3D end) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(NEW_FRAME_BORDER);
        gc.setFill(NEW_FRAME_FILL);

        double width = Math.abs(end.getX() - start.getX());
        double height = Math.abs(end.getY() - start.getY());
        double left = Math.min(start.getX(), end.getX());
        double top = Math.min(start.getY(), end.getY());

        gc.fillRect(left, top, width, height);
        gc.strokeRect(left, top, width, height);
    }
}
