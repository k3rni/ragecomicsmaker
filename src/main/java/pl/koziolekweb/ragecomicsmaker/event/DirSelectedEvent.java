package pl.koziolekweb.ragecomicsmaker.event;

import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class DirSelectedEvent {

	private final File selectedDir;

	private final Comic comic;

	public DirSelectedEvent(File selectedDir) {
		this.selectedDir = selectedDir;
		this.comic = prepareModel();
	}

	public List<File> getImages() {
		if (selectedDir == null)
			return Collections.emptyList();
		File[] files = selectedDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg")
						|| name.toLowerCase().endsWith(".png");
			}
		});

		return Arrays.asList(files);
	}

	public Comic getModel() {
		return comic;
	}

	public File getSelectedDir() {
		return selectedDir;
	}

	private Comic prepareModel() {
		Comic comic = new Comic();
		comic.initDefaults();
		List<File> images = getImages();
		Collections.sort(images);
		int i = 0;
		for (File image : images) {
			Screen screen = new Screen();
			screen.setBgcolor("#ffffff");
			screen.setImage(image);
			screen.setIndex(i);
			comic.addScreen(screen);
			i++;
		}
		return comic;
	}
}
