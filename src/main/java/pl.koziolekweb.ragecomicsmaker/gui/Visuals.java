package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.geometry.VPos;
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

    public static Text buildFrameText(Frame f) {
        Text t = new Text(String.format("%d", f.getId()));
        t.setTextOrigin(VPos.BOTTOM);
        t.setFont(TEXT_FONT);
        t.setFill(TEXT_COLOR);
        t.setStroke(TEXT_STROKE);

        return t;
    }

    public static Rectangle buildFrameRect(Frame f, boolean highlight) {
        Rectangle r = new Rectangle();
        r.setBlendMode(BlendMode.SRC_ATOP);
        if (highlight) {
            r.setFill(HIGHLIGHTED_FRAME_FILL);
            r.setStroke(HIGHLIGHTED_FRAME_BORDER);
        } else {
            r.setFill(FRAME_FILL);
            r.setStroke(FRAME_BORDER);
        }
        r.getStrokeDashArray().setAll(0.5, 0.5);

        return r;
    }
}
