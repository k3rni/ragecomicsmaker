package pl.koziolekweb.ragecomicsmaker;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MathUtil {
    public static double clamp(double min, double v, double max) {
        return min(max, max(min, v));
    }
}
