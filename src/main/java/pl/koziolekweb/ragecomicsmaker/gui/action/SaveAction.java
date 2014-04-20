package pl.koziolekweb.ragecomicsmaker.gui.action;

import com.google.common.io.Files;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEvent;
import pl.koziolekweb.ragecomicsmaker.event.DirSelectedEventListener;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.xml.XmlMarshaller;

import javax.xml.bind.JAXBException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class SaveAction extends MouseAdapter implements DirSelectedEventListener {


    private Comic comic;
    private File targetDir;
    private SimpleDateFormat sdf = new SimpleDateFormat();

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        try {

            String name = targetDir.getAbsolutePath() + File.separator + "comic.xml";
            File comicFile = new File(name);
            if (comicFile.exists()) {
                File backup = new File(targetDir.getAbsolutePath() + File.separator + "backup-" +
                        sdf.format(new Date())
                        + "-comic.xml");
                Files.move(comicFile, backup);
            }
            comicFile.createNewFile();
            XmlMarshaller.startMarshallOf(Comic.class)
                    .useFormattedOutput()
                    .to(new FileOutputStream(name))
                    .of(comic);
        } catch (JAXBException | IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void handleDirSelectedEvent(DirSelectedEvent event) {
        this.comic = event.getModel();
        this.targetDir = event.getSelectedDir();
    }
}
