package pl.koziolekweb.ragecomicsmaker.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Screen;
import pl.koziolekweb.ragecomicsmaker.xml.XmlUnmarshaller;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
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

	public List<File> getImages(final String namePattern) {
		if (selectedDir == null)
			return Collections.emptyList();
		File[] files = selectedDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String pattern = "screen";
				if (namePattern != null && !namePattern.isEmpty())
					pattern = namePattern.split("@")[0];

				return name.matches(pattern + "[0-9]+\\.(jpg|png)");
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
			ObjectMapper mapper = new XmlMapper();
			try {
				final Comic from = mapper.readValue(comicFileName[0], Comic.class);
				List<File> images = getImages(from.getImages().getNamePattern());
				Collections.sort(images);;
				if (images.size() != from.getScreens().size()) {
					App.EVENT_BUS.post(new ErrorEvent("Liczba obarazów inna niż zadeklarowana w pliku!", null));
//					return from;
				}
//				renumberScreens(from, images);
				return from;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Comic comic = new Comic();
		comic.initDefaults();
		List<File> images = getImages(null);
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

	private void renumberScreens(Comic from, List<File> images) {
		Collections2.filter(images, input -> {
			String number = input.getName().replaceAll("\\D", "");
			Screen screenByIndex = from.findScreenByIndex(number);
			screenByIndex.setIndex(Integer.parseInt(number));
			screenByIndex.setImage(input);
			return true;
		}).size();
	}
}
