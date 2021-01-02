package pl.koziolekweb.ragecomicsmaker;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("UnstableApiUsage")
public class SaveCommand {
	private final Comic comic;
	private final File targetDir;

	public SaveCommand(Comic comic, File targetDir) {
		this.comic = comic;
		this.targetDir = targetDir;
	}

	public void save() throws IOException {
		Path comicFile = targetDir.toPath().resolve("comic.xml");
		if (Files.exists(comicFile)) saveBackup(comicFile);

		XmlMapper mapper = createXMLMapper();
		try (OutputStream io = Files.newOutputStream(comicFile, StandardOpenOption.CREATE)) {
			mapper.writeValue(io, comic);
		}

		// Iterate over all images in comic and their crops. Use ImageIO to produce tiny cropped files
		saveSubImages();
	}

	private void saveBackup(Path comicFile) throws IOException {
		final DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm");
		String name = String.format("backup-%s-comic.xml", sdf.format(LocalDateTime.now()));
		Path backup = comicFile.resolveSibling(name);
		Files.move(comicFile, backup);
	}

	private XmlMapper createXMLMapper() {
		XmlMapper mapper = new XmlMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper;
	}

	private void saveSubImages() throws IOException {
		for (Screen screen : comic.getScreens()) {
			// The screens only keep reference to their file, so we need to load its image first
			if (screen.getFrames().size() == 0) continue;
			if (screen.getImage() == null) continue;

			BufferedImage source = ImageIO.read(screen.getImage());
			for (Frame frame : screen.getFrames()) {
				String frameFilename = frameFilename(screen, frame);
				Path path = clipPath(frameFilename);

				BufferedImage crop = screen.crop(source, frame);
				Files.createDirectories(path.getParent());
				ImageIO.write(crop, "png", path.toFile());
			}
		}
	}

	private Path clipPath(String frameFilename) {
		return targetDir.toPath().resolve("clips").resolve(frameFilename);
	}

	static String frameFilename(Screen screen, Frame frame) {
		return String.format("%03d_%03d.png", screen.getIndex(), frame.getId());
	}
}
