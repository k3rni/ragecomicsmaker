package pl.koziolekweb.ragecomicsmaker.event;

public class MetadataUpdateEvent {
    public final String title;
    public final String descr;
    public final String authors;
    public final String illustrators;
    public final String publisher;
    public final String pubDate;
    public final String isbn;
    public final String rights;

    public MetadataUpdateEvent(String title, String descr, String authorsList, String illustratorsList,
                               String publisher, String pubdate, String isbn, String rights) {
        this.title = title;
        this.descr = descr;
        this.authors = authorsList;
        this.illustrators = illustratorsList;
        this.publisher = publisher;
        this.pubDate = pubdate;
        this.isbn = isbn;
        this.rights = rights;
    }
}
