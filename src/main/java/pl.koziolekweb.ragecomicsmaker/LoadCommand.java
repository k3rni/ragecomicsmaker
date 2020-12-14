package pl.koziolekweb.ragecomicsmaker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Streams;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEvent;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LoadCommand {
    private Path dir;
    private Comic comic;

    public LoadCommand(Path dir) {
        this.dir = dir;
    }

    public Comic load() {
        Path xml = dir.resolve("comic.xml");

        if (!xml.toFile().canRead()) {
            this.comic = new Comic();
            loadScreens();
        } else {
            this.comic = loadComicXml(xml);
            verifyScreens();
        }

        return this.comic;
    }

    private Comic loadComicXml(Path xml) {
        ObjectMapper mapper = new XmlMapper();
        try {
            return mapper.readValue(xml.toFile(), Comic.class);
        } catch (IOException e) {
            App.EVENT_BUS.post(new ErrorEvent("Looks like comic.xml is corrupted. Consider replacing it with one of the backups", null));
            App.EVENT_BUS.post(new ErrorEvent(e.getMessage(), null));
            e.printStackTrace();
        }
        return null;
    }

    void loadScreens() {
        List<File> images = getImages(Optional.empty());
        Collections.sort(images);
        final String white = "#FFFFFF";
        Streams.mapWithIndex(images.stream(),
                (file, index) -> new Screen(file, white, index))
                .forEach(comic::addScreen);
    }

    void verifyScreens() {
        String pattern = comic.getImages().getNamePattern();
        List<File> images;

        if (pattern == null || pattern.isBlank())
            images = getImages(Optional.empty());
        else
            images = getImages(Optional.of(pattern));

        Collections.sort(images); // ?

        if (images.size() != comic.getScreens().size()) {
            App.EVENT_BUS.post(new ErrorEvent("Screen count differs from saved version", null));
        }
    }

    List<File> getImages(Optional<String> namePattern) {
        final String digits = "[0-9]+";
        final String extPattern = "(jpg|png)";

//        if (targetDir == null)
//            return Collections.emptyList();

        String pattern = namePattern.orElse("screen")
                .split("@")[0] +
                digits + "\\." + extPattern;

        File dir = this.dir.toFile();
        return Arrays.stream(
                dir.listFiles((pathname ->
                        pathname.getName().matches(pattern))))
                .collect(Collectors.toList());
    }

}
