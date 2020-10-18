package pl.koziolekweb.ragecomicsmaker.gui;

import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEventListener;
import pl.koziolekweb.ragecomicsmaker.event.ImageSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.gui.action.EditMetaAction;
import pl.koziolekweb.ragecomicsmaker.gui.action.SaveAction;
import pl.koziolekweb.ragecomicsmaker.gui.action.SelectFileAction;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.Collection;

import static javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FilesPanel extends JPanel implements DirSelectedEventListener {

	private JTree fileTree;
	private File curentProjectDir;
	private Comic comic;

	public FilesPanel() {
		super();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(layout);

		JButton openDirBtn = new JButton("Select Directory");
		c.gridx = c.gridy = 0;
		c.gridwidth = 20; c.gridheight = 3;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 0;
		c.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(openDirBtn, c);
		add(openDirBtn);

		Component files = prepareFileTree();
		c.gridy = 3;
		c.gridheight = 14;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		layout.setConstraints(files, c);
		add(files);

		JButton saveBtn = new JButton("Build");
		c.gridy = 17;
		c.gridwidth = 10; c.gridheight = 3;
		c.weighty = 0;
		c.anchor = GridBagConstraints.SOUTHWEST;
		layout.setConstraints(saveBtn, c);
		add(saveBtn);

		JButton metaBtn = new JButton("Metadata");
		metaBtn.setEnabled(false);
		c.gridx = 10;
		c.anchor = GridBagConstraints.SOUTHEAST;
		layout.setConstraints(metaBtn, c);
		add(metaBtn);


		openDirBtn.addMouseListener(new SelectFileAction(this));
		SaveAction saveAction = new SaveAction();
		App.EVENT_BUS.register(saveAction);
		saveBtn.addMouseListener(saveAction);

		metaBtn.addMouseListener(new EditMetaAction());

	}

	public void handleDirSelectedEvent(DirSelectedEvent event) {
		this.comic = event.getModel();
		Collection<Screen> images = comic.getScreens();
		DefaultTreeModel model = (DefaultTreeModel) fileTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();
		for (Screen image : images) {
			File image1 = image.getImage();
			if (image1 != null) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(image1.getName());
				root.add(child);
			}
		}
		model.reload(root);
		this.curentProjectDir = event.getSelectedDir();
	}

	private JScrollPane prepareFileTree() {
		fileTree = new JTree(new String[]{});
		fileTree.getSelectionModel().setSelectionMode(SINGLE_TREE_SELECTION);
		fileTree.addTreeSelectionListener(new ImageFIleTreeSelectionListener());
		JScrollPane jsp = new JScrollPane(fileTree);
		return jsp;
	}

	private class ImageFIleTreeSelectionListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TreePath newLeadSelectionPath = e.getNewLeadSelectionPath();
			if (newLeadSelectionPath == null) return;
			Screen selectedScreen = comic.findScreenByFileName(newLeadSelectionPath.getLastPathComponent().toString());
			App.EVENT_BUS.post(new ImageSelectedEvent(comic, selectedScreen));
		}
	}
}
