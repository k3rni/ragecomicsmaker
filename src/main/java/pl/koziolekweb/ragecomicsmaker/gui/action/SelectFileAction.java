package pl.koziolekweb.ragecomicsmaker.gui.action;

import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
@SuppressWarnings("UnstableApiUsage")
public class SelectFileAction extends MouseAdapter {
	private final Component parent;

	public SelectFileAction(Component parent) {
		this.parent = parent;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.showOpenDialog(parent);
		jfc.setCurrentDirectory(new File("/home/koziolek/workspace/ragecomicsmaker/src/test/resources/sample"));
		File selectedFile = jfc.getSelectedFile();
		App.EVENT_BUS.post(new DirSelectedEvent(selectedFile));
	}
}
