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
    requires org.codehaus.stax2;
    requires com.ctc.wstx;
    requires com.google.common;
    requires thumbnailator;
    requires com.github.mustachejava;
    requires org.apache.commons.io;
    requires epub.creator;
    requires jsr305;

    opens pl.koziolekweb.ragecomicsmaker;
    opens pl.koziolekweb.ragecomicsmaker.model to com.fasterxml.jackson.databind;
    opens pl.koziolekweb.ragecomicsmaker.gui;

}