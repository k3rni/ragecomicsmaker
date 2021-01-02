package pl.koziolekweb.ragecomicsmaker.output;

import pl.koziolekweb.ragecomicsmaker.SaveCommand;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class CBZCompiler {
    private Path targetDir;
    private Comic comic;

    public CBZCompiler(Path targetDir, Comic comic) {
        super();
        this.targetDir = targetDir;
        this.comic = comic;
    }

    public Path save() throws IOException {
        Path out = targetDir.resolve(getTargetFilename());

        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(out, CREATE, TRUNCATE_EXISTING))) {
            saveScreens(zip);
        }

        return out;
    }

    private void saveScreens(ZipOutputStream zip) throws IOException {
        zip.setLevel(ZipEntry.STORED);
        for (Screen screen : comic.getScreens()) {
            File image = screen.getImage();
            if (image == null) continue;

            if (comic.getInsertFullPages()) {
                ZipEntry entry = new ZipEntry(String.format("%04d_00.png", screen.getIndex()));
                zip.putNextEntry(entry);
                byte[] bytes = Files.readAllBytes(image.toPath());
                zip.write(bytes);
            }

            for (Frame f : screen.getFrames()) {
                ZipEntry entry = new ZipEntry(String.format("%04d_%02d.png", screen.getIndex(), f.getId()));
                zip.putNextEntry(entry);
                byte[] bytes = Files.readAllBytes(clipPath(SaveCommand.frameFilename(screen, f)));
                zip.write(bytes);
            }
        }
    }

    private String getTargetFilename() {
        return String.format("%s - %s.cbz", comic.getTitle(), comic.getAuthor());
    }

    private Path clipPath(String frameFilename) {
        return targetDir.resolve("clips").resolve(frameFilename);
    }
}
