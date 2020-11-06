package pl.koziolekweb.ragecomicsmaker;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubWriter;
import org.apache.commons.io.FilenameUtils;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class ComicCompiler {
    private final File targetDir;
    private final Comic comic;

    public ComicCompiler(File targetDir, Comic comic) {
        this.targetDir = targetDir;
        this.comic = comic;
    }
    public void save() throws IOException {
        Book book = new Book();
        buildBookMetadata(book.getMetadata());
        addBookImages(book);

        Mustache template = findTemplate();

        for (Screen screen: comic.getScreens()) {
            File image = screen.getImage();
            if (image == null) continue;

            String href = String.format("page-%d.xhtml", screen.getIndex());
            book.addSection(
                    String.format("Page %d", screen.getIndex()),
                    new Resource(buildPage(screen, template), href)
            );

            for (Frame frame : screen.getFrames()) {
                String frame_ref = String.format("page-%d-%d.xhtml", screen.getIndex(), frame.getId());
                book.addSection(
                        String.format("Page %d.%d", screen.getIndex(), frame.getId()),
                        new Resource(buildPage(screen, frame, template), frame_ref)
                );
            }
        }

        EpubWriter epubWriter = new EpubWriter();
        Path out = targetDir.toPath().resolve("book.epub");
        epubWriter.write(book, new FileOutputStream(out.toFile()));
    }

    private Reader buildPage(Screen screen, Frame frame, Mustache template) {
        File image = screen.getImage();
        String stem = FilenameUtils.getBaseName(image.getName());
        String ext = FilenameUtils.getExtension(image.getName());

        StringWriter writer = new StringWriter();
        HashMap<String, Object> scope = new HashMap<>();

        scope.put("title", comic.getTitle());
        scope.put("image", String.format("screens/%s.%d.%s", stem, frame.getId(), ext));

        template.execute(writer, scope);

        return new StringReader(writer.toString());
    }

    private Reader buildPage(Screen screen, Mustache template) {
        StringWriter writer = new StringWriter();
        HashMap<String, Object> scope = pageScope(screen, this.comic);

        template.execute(writer, scope);

        return new StringReader(writer.toString());
    }

    private HashMap<String, Object> pageScope(Screen screen, Comic comic) {
        File image = screen.getImage();
        String stem = FilenameUtils.getBaseName(image.getName());
        String ext = FilenameUtils.getExtension(image.getName());

        HashMap<String, Object> scope = new HashMap();

        scope.put("title", comic.getTitle());
        scope.put("image", String.format("screens/%s.%s", stem, ext));
        return scope;
    }

    private Mustache findTemplate() {
        MustacheFactory mf = new DefaultMustacheFactory();

        try {
            Path path = targetDir.toPath().resolve("page.xhtml");
            return mf.compile(new FileReader(path.toFile()), "page.xhtml");
        } catch (FileNotFoundException fnf) {
            // No page template in comic directory. Use default one
            InputStream s = getClass().getResourceAsStream("page.xhtml");
            return mf.compile("page.xhtml");
        }
    }

    private void addBookImages(Book book) throws IOException {
        Resources res = book.getResources();

        for (Screen screen: comic.getScreens()) {
            File image = screen.getImage();
            if (image == null) continue;

            String stem = FilenameUtils.getBaseName(image.getName());
            String ext = FilenameUtils.getExtension(image.getName());

            res.add(
                    new Resource(
                            new FileInputStream(screen.getImage()),
                            String.format("screens/%s.%s", stem, ext)
                    )
            );

            for (Frame frame : screen.getFrames()) {
                String href = String.format("screens/%s.%d.%s", stem, frame.getId(), ext);
                String frameFilename = frameFilename(screen, frame);
                Path path = clipPath(frameFilename);
                res.add(new Resource(new FileInputStream(path.toFile()), href));
            }
        }
    }

    private void buildBookMetadata(Metadata metadata) {
        metadata.setTitles(List.of(comic.getTitle()));
        metadata.setDescriptions(List.of(comic.description));
        // This works for a single author. For a list, consider splitting and using setAuthors
        metadata.addAuthor(new Author(comic.author));
        // Same here: single illustrator
        Author illustrator = new Author(comic.illustrator);
        illustrator.setRole("art");
        metadata.addAuthor(illustrator);
        metadata.addPublisher(comic.publisher);
        metadata.addIdentifier(new Identifier(Identifier.Scheme.ISBN, comic.isbn));
        metadata.setRights(List.of(comic.rights));
        metadata.addDate(new Date(comic.publicationDate, Date.Event.PUBLICATION));
    }

    private Path clipPath(String frameFilename) {
        return FileSystems.getDefault().getPath(targetDir.getAbsolutePath(), "clips", frameFilename);
    }

    private String frameFilename(Screen screen, Frame frame) {
        return String.format("%1$03d_%2$03d.png", screen.getIndex(), frame.getId());
    }
}
