package pl.koziolekweb.ragecomicsmaker.gui;

import com.google.common.eventbus.Subscribe;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.FileSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.gui.action.SelectFileAction;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.Collection;

import static javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FilesPanel extends JPanel implements FileSelectedEventListener {

	private JTree fileTree;
	private File curentProjectDir;

	public FilesPanel() {
		super();
		App.EVENT_BUS.register(this);
		setLayout(new BorderLayout());
		JButton openDirBtn = new JButton("Select Directory");
		openDirBtn.addMouseListener(new SelectFileAction(this));

		add(openDirBtn, BorderLayout.PAGE_START);

		prepareFileTree();

		JButton saveBtn = new JButton("Save comics.xml");
		add(saveBtn, BorderLayout.PAGE_END);
	}

	@Subscribe
	public void handleFileSelectedEvent(FileSelectedEvent event) {
		Collection<Screen> images = event.getModel().getScreens();
		DefaultTreeModel model = (DefaultTreeModel) fileTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		for (Screen image : images) {
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(image.getImage().getName());
			root.add(child);
		}
		model.reload(root);
		this.curentProjectDir = event.getSelectedDir();
	}

	private JScrollPane prepareFileTree() {
		fileTree = new JTree(new String[]{});
		fileTree.getSelectionModel().setSelectionMode(SINGLE_TREE_SELECTION);
		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				System.out.println(fileTree.getLastSelectedPathComponent());
				System.out.println(e.getNewLeadSelectionPath().getLastPathComponent());
//				File f =   curentProjectDir.get
			}
		});
		JScrollPane jsp = new JScrollPane(fileTree);
		add(jsp, BorderLayout.CENTER);
		return jsp;
	}
}
