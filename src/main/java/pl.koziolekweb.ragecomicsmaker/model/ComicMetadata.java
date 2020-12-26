package pl.koziolekweb.ragecomicsmaker.model;

import coza.opencollab.epub.creator.api.MetadataItem;

import static coza.opencollab.epub.creator.api.MetadataItem.builder;

public class ComicMetadata {
    private Comic comic;

    public ComicMetadata(Comic comic) {
        this.comic = comic;
    }

    public MetadataItem author() {
        return builder()
            .name("dc:creator")
            .id("author")
            .value(comic.getAuthor());
    }

    public MetadataItem illustrator() {
        return builder()
                .name("dc:creator")
                .id("illustrator")
                .value(comic.getIllustrator());
    }

    public MetadataItem illustratorRole() {
        return builder()
                .name("meta")
                .property("role")
                .refines("#illustrator")
                .value("ill");
    }

    public MetadataItem description() {
        return builder().name("dc:description").value(comic.getDescription());
    }

    public MetadataItem publisher() {
        return builder().name("dc:publisher").value(comic.getPublisher());
    }

    public MetadataItem date() {
        return builder().name("dc:date").value(comic.publicationDate.get());
    }

    public MetadataItem rights() {
        return builder().name("dc:rights").value(comic.getRights());
    }

    public MetadataItem isbn() {
        return builder()
                .name("dc:identifier")
                .id("isbn")
                .value(comic.getISBN());
    }

    public MetadataItem isbnScheme() {
        return builder()
                .name("meta")
                .property("scheme")
                .refines("#isbn")
                .value("ISBN");
    }
}
