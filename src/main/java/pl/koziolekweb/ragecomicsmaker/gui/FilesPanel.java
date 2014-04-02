package pl.koziolekweb.ragecomicsmaker.gui;

import javax.swing.*;
import java.awt.*;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FilesPanel extends JPanel {

	public FilesPanel() {
		super();
		setLayout(new BorderLayout());
		JButton openDirBtn = new JButton("Select Directory");
		add(openDirBtn, BorderLayout.PAGE_START);

		prepareFileTree();

		JButton saveBtn = new JButton("Save comics.xml");
		add(saveBtn, BorderLayout.PAGE_END);
	}

	private JScrollPane prepareFileTree() {
		JTree fileTree = new JTree(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 8, 9, 10, 11, 12, 13, 8, 9, 10, 11, 12, 13, 8, 9, 10, 11, 12, 13, 8, 9, 10, 11, 12, 13});
		JScrollPane jsp = new JScrollPane(fileTree);
		add(jsp, BorderLayout.CENTER);
		return jsp;
	}
}
