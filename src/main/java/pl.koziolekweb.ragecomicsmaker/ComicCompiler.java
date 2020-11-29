package pl.koziolekweb.ragecomicsmaker;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.Lists;
import coza.opencollab.epub.creator.api.MetadataItem;
import coza.opencollab.epub.creator.api.OpfCreator;
import coza.opencollab.epub.creator.impl.OpfCreatorDefault;
import coza.opencollab.epub.creator.impl.TocCreatorDefault;
import coza.opencollab.epub.creator.model.Content;
import coza.opencollab.epub.creator.model.EpubBook;
import coza.opencollab.epub.creator.util.EpubWriter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static coza.opencollab.epub.creator.api.MetadataItem.builder;

public class ComicCompiler {
    private final File targetDir;
    private final Comic comic;

    public ComicCompiler(File targetDir, Comic comic) {
        this.targetDir = targetDir;
        this.comic = comic;
    }
    public void save() throws IOException {
        EpubBook book = new EpubBook();

        setBasicMetadata(book);
        Collection<MetadataItem> metadata = extraMetadata();
        addBookImages(book);
        addCover(book);

        Mustache template = findTemplate();

        for (Screen screen: comic.getScreens()) {
            File image = screen.getImage();
            if (image == null) continue;

            String href = String.format("page-%d.xhtml", screen.getIndex());

            Content content = book.addContent(pageBytes(screen, template),
                    "text/html",
                    href,
                    true, true);
            content.setId(String.format("Page %d", screen.getIndex() + 1));
            content.setLinear(true);

            for (Frame frame : screen.getFrames()) {
                String frame_ref = String.format("page-%d-%d.xhtml", screen.getIndex(), frame.getId());
                book.addContent(
                        pageBytes(screen, frame, template),
                        "text/html",
                        frame_ref,
                        false, true)
                .setId(String.format("Page %d Frame %d", screen.getIndex() + 1, frame.getId()));
            }
        }

        saveToFile(book, metadata);
    }

    private void saveToFile(EpubBook book, Collection<MetadataItem> metadataItems) {
        EpubWriter writer = new EpubWriter();
        OpfCreator opf = new OpfCreatorDefault();
        for (MetadataItem m : metadataItems) opf.addMetadata(m);

        writer.setOpfCreator(opf);
        writer.setTocCreator(this::createHiddenToc);

        Path out = targetDir.toPath().resolve("book.epub");
        try {
            writer.writeEpubToStream(book, new FileOutputStream(out.toFile()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Content createHiddenToc(EpubBook book) {
        Content toc = new TocCreatorDefault().createTocFromBook(book);
        String old = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(toc.getContent())).toString();
        String hidden = old.replace("<ol>", "<ol hidden=''>");
        toc.setContent(hidden.getBytes(StandardCharsets.UTF_8));
        return toc;
    }

    private byte[] pageBytes(Screen screen, Mustache template) {
        StringWriter writer = new StringWriter();
        HashMap<String, Object> scope = pageScope(screen, this.comic);

        template.execute(writer, scope);

        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] pageBytes(Screen screen, Frame frame, Mustache template) {
        File image = screen.getImage();
        String stem = FilenameUtils.getBaseName(image.getName());
        String ext = FilenameUtils.getExtension(image.getName());

        StringWriter writer = new StringWriter();
        HashMap<String, Object> scope = new HashMap<>();

        scope.put("title", comic.getTitle());
        scope.put("image", String.format("screens/%s.%d.%s", stem, frame.getId(), ext));

        template.execute(writer, scope);
        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    private HashMap<String, Object> pageScope(Screen screen, Comic comic) {
        File image = screen.getImage();
        String stem = FilenameUtils.getBaseName(image.getName());
        String ext = FilenameUtils.getExtension(image.getName());

        HashMap<String, Object> scope = new HashMap<>();

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

    private void addBookImages(EpubBook book) throws IOException {
        for (Screen screen: comic.getScreens()) {
            File image = screen.getImage();
            if (image == null) continue;

            String stem = FilenameUtils.getBaseName(image.getName());
            String ext = FilenameUtils.getExtension(image.getName());

            Content content = book.addContent(new FileInputStream(screen.getImage()),
                    mime(ext), String.format("screens/%s.%s", stem, ext),
                    false, false);
            content.setId(stem);

            for (Frame frame : screen.getFrames()) {
                String href = String.format("screens/%s.%d.%s", stem, frame.getId(), ext);
                String frameFilename = frameFilename(screen, frame);
                Path path = clipPath(frameFilename);
                book.addContent(new FileInputStream(path.toFile()),
                        mime(ext), href, false, false)
                .setId(String.format("%s.%d", stem, frame.getId()));
            }
        }
    }

    private void addCover(EpubBook book) throws IOException {
        Path cover = targetDir.toPath().resolve("cover.jpg");
        File coverImage = cover.toFile();


        if (coverImage.canRead()) {
            addCoverFrom(book, coverImage, "image/jpeg");
            return;
        }

        cover = targetDir.toPath().resolve("cover.png");
        coverImage = cover.toFile();
        if (coverImage.canRead()) {
            addCoverFrom(book, coverImage, "image/png");
            return;
        }
    }

    private void addCoverFrom(EpubBook book, File image, String mime) throws IOException {
        book.addCoverImage(IOUtils.toByteArray(new FileInputStream(image)), mime, image.getName());
    }

    private String mime(String ext) {
        switch (ext) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }

    private void setBasicMetadata(EpubBook book) {
        // Required by epubcreator, exact content not relevant
        book.setId(comic.getTitle());

        book.setLanguage(comic.getLanguage());
        book.setTitle(comic.getTitle());
        // There is a limited set of built-in metadata available. Everything else
        // must be constructed manually
    }

    private Collection<MetadataItem> extraMetadata() {
        List<MetadataItem> meta = Lists.newArrayList();
        // Author is straightforward; could also use setAuthor.
        meta.add(builder()
            .name("dc:creator")
            .id("author")
            .value(comic.getAuthor()));

        // Illustrator is a dc:creator tag followed by a <meta> tag updating role
        meta.add(builder()
            .name("dc:creator")
            .id("illustrator")
            .value(comic.getIllustrator()));
        meta.add(builder()
            .name("meta")
            .property("role")
            .refines("#illustrator")
            .value("art"));

        meta.add(builder().name("dc:description").value(comic.getDescription()));
        meta.add(builder().name("dc:publisher").value(comic.getPublisher()));
        meta.add(builder().name("dc:date").value(comic.publicationDate.get()));
        meta.add(builder().name("dc:rights").value(comic.getRights()));

        meta.add(builder()
            .name("dc:identifier")
            .id("isbn")
            .value(comic.getISBN()));
        meta.add(builder()
                .name("meta")
                .property("scheme")
                .refines("#isbn")
                .value("ISBN"));

        return meta;
    }

    private Path clipPath(String frameFilename) {
        return FileSystems.getDefault().getPath(targetDir.getAbsolutePath(), "clips", frameFilename);
    }

    private String frameFilename(Screen screen, Frame frame) {
        return String.format("%1$03d_%2$03d.png", screen.getIndex(), frame.getId());
    }
}
