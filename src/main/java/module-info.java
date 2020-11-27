module pl.koziolekweb.ragecomismaker {
    requires java.desktop;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.swing;
    requires org.controlsfx.controls;

    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires commons.io;
    requires com.google.common;
    requires thumbnailator;
    requires xmlpull;

    exports pl.koziolekweb.ragecomicsmaker;
}