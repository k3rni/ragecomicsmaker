package pl.koziolekweb.ragecomicsmaker.event;

import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Screen;
import pl.koziolekweb.ragecomicsmaker.xml.XmlUnmarshaller;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileFilter;
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
				return name.matches("screen[0-9]+\\.(jpg|png)");
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
		File[] comicFileName = selectedDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().equals("comic.xml");
			}
		});
		if (comicFileName.length == 1) {
			try {
				Comic from = (Comic) (XmlUnmarshaller.startUnmarshallOf(Comic.class).from(comicFileName[0]));
				List<File> images = getImages();
				Collections.sort(images);
				for (Screen screen : from.getScreens()) {
					screen.setImage(images.get(screen.getIndex()));
				}
				return from;
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
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
