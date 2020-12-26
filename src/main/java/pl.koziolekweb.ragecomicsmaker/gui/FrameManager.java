package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.geometry.Point3D;

public interface FrameManager {
    boolean ignoreFrameEvents();

    void createFrame(Point3D start, Point3D end);
}
