package pl.koziolekweb.ragecomicsmaker.gui.action;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.io.Files;
import pl.koziolekweb.ragecomicsmaker.ComicCompiler;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEventListener;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
@SuppressWarnings("UnstableApiUsage")
public class SaveAction extends MouseAdapter implements DirSelectedEventListener {
	private Comic comic;
	private File targetDir;
	private final DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm");

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		try {

			String name = targetDir.getAbsolutePath() + File.separator + "comic.xml";
			File comicFile = new File(name);
			if (comicFile.exists()) {
				File backup = new File(targetDir.getAbsolutePath() + File.separator + "backup-" +
						sdf.format(LocalDateTime.now())
						+ "-comic.xml");
				Files.move(comicFile, backup);
			}
			comicFile.createNewFile();
			XmlMapper mapper = new XmlMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(new FileOutputStream(name), comic);
			// Iterate over all images in comic and their crops. Use ImageIO to produce tiny cropped files
			saveSubImages();
			new ComicCompiler(targetDir, comic).save();
			JOptionPane.showMessageDialog(null,
					String.format("Saved comic into %s as book.epub", targetDir.getCanonicalPath()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}



	private void saveSubImages() throws IOException {
		for (Screen screen : comic.getScreens()) {
			// The screens only keep reference to their file, so we need to load its image first
			if (screen.getFrames().size() == 0) continue;
			if (screen.getImage() == null) continue;

			BufferedImage image = ImageIO.read(screen.getImage());
			for (Frame frame : screen.getFrames()) {
				String frameFilename = frameFilename(screen, frame);
				Path path = clipPath(frameFilename);

				BufferedImage clip = getSubImage(image, frame);
				Files.createParentDirs(path.toFile());
				ImageIO.write(clip, "png", path.toFile());
			}
		}
	}

	private Path clipPath(String frameFilename) {
		return FileSystems.getDefault().getPath(targetDir.getAbsolutePath(), "clips", frameFilename);
	}

	private String frameFilename(Screen screen, Frame frame) {
		return String.format("%1$03d_%2$03d.png", screen.getIndex(), frame.getId());
	}

	private BufferedImage getSubImage(BufferedImage image, Frame frame) {
		double x = frame.getStartX() * image.getWidth();
		double w = frame.getSizeX() * image.getWidth();
		double y = frame.getStartY() * image.getHeight();
		double h = frame.getSizeY() * image.getHeight();

		return image.getSubimage(
				(int) Math.round(x),
				(int) Math.round(y),
				(int) Math.round(w),
				(int) Math.round(h));
	}

	@Override
	public void handleDirSelectedEvent(DirSelectedEvent event) {
		this.comic = event.getModel();
		this.targetDir = event.getSelectedDir();
	}
}
