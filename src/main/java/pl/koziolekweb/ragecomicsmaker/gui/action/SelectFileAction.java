package pl.koziolekweb.ragecomicsmaker.gui.action;

import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.FileSelectedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class SelectFileAction extends MouseAdapter {


	private Component parent;

	public SelectFileAction(Component parent) {
		this.parent = parent;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.showOpenDialog(parent);
		File selectedFile = jfc.getSelectedFile();
		App.EVENT_BUS.post(new FileSelectedEvent(selectedFile));
	}
}
