package pl.koziolekweb.ragecomicsmaker.gui.action;

import net.miginfocom.swing.MigLayout;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEventListener;
import pl.koziolekweb.ragecomicsmaker.event.MetadataUpdateEvent;
import pl.koziolekweb.ragecomicsmaker.model.Comic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

public class EditMetaAction extends AbstractAction implements DirSelectedEventListener {
    private Comic comic;

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame win = new JFrame("Metadata Editor");
        win.setLayout(new MigLayout("wrap 3"));

        win.add(new JLabel("Title"), "gap para");
        JTextField title = new JTextField(getTitle());
        win.add(title, "span, growx, wrap");

        win.add(new JLabel("Author"), "gap para");
        JTextField authors = new JTextField(getAuthors());
        win.add(authors, "span, growx, wrap");

        win.add(new JLabel("Illustrator"), "gap para");
        JTextField illustrators = new JTextField(getIllustrators());
        win.add(illustrators, "span, growx, wrap");

        win.add(new JLabel("Publisher"), "gap para");
        JTextField publisher = new JTextField(getPublisher());
        win.add(publisher, "span, growx, wrap");

        win.add(new JLabel("Publication date (YYYY[-MM[-DD]]"), "gap para");
        JTextField publicationDate = new JTextField(getPublicationDate());
        win.add(publicationDate, "span, growx, wrap");

        win.add(new JLabel("ISBN"), "gap para");
        JTextField isbn = new JTextField(getISBN());
        win.add(isbn, "span, growx, wrap");

        win.add(new JLabel("Description"));
        JTextArea descr = new JTextArea(getDescr(),10, 60);
        win.add(descr, "span, growx, growy, wrap");

        win.add(new JLabel("Copyrights"));
        JTextArea copyrights = new JTextArea(getCopyright(),10, 60);
        win.add(copyrights, "span, growx, growy, wrap");

        win.setResizable(true);

        win.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e1) {
                super.windowClosing(e1);
                EditMetaAction.this.saveMetadata(
                        title.getText(),
                        authors.getText(),
                        illustrators.getText(),
                        descr.getText(),
                        publisher.getText(),
                        publicationDate.getText(),
                        isbn.getText(),
                        copyrights.getText());
            }
        });

        win.pack();
        win.setVisible(true);
    }

    private String getAuthors() {
        return comic == null ? "" : comic.author;
    }

    private String getIllustrators() {
        return comic == null ? "" : comic.illustrator;
    }

    private String getDescr() {
        return comic == null ? "" : comic.description;
    }

    private String getTitle() {
        return comic == null ? "" : comic.title;
    }

    private String getPublisher() {
        return comic == null ? "" : comic.publisher;
    }

    private String getPublicationDate() {
        return comic == null ? "" : comic.publicationDate;
    }

    private String getISBN() {
        return comic == null ? "" : comic.isbn;
    }

    private String getCopyright() {
        return comic == null ? "" : comic.rights;
    }

    void saveMetadata(String title, String authors, String illustrators, String descr, String publisher, String pubdate, String isbn, String rights) {
        App.EVENT_BUS.post(new MetadataUpdateEvent(title, descr, authors, illustrators, publisher, pubdate, isbn, rights));
    }

    public void handleDirSelectedEvent(DirSelectedEvent event) {
        this.comic = event.getModel();
    }
}
