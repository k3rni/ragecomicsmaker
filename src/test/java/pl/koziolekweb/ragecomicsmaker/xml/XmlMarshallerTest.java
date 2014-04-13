package pl.koziolekweb.ragecomicsmaker.xml;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.koziolekweb.ragecomicsmaker.model.Comic;
import pl.koziolekweb.ragecomicsmaker.model.Direction;
import pl.koziolekweb.ragecomicsmaker.model.Frame;
import pl.koziolekweb.ragecomicsmaker.model.Images;
import pl.koziolekweb.ragecomicsmaker.model.Screen;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class XmlMarshallerTest {

	private Comic comic;

	@BeforeMethod
	public void setUp() throws Exception {
		comic = new Comic();
		comic.setId("a1");
		comic.setBgcolor("a1");
		comic.setDirection(Direction.LTR);
		comic.setOrientation("a1");
		comic.setTitle("a1");
		comic.setVersion(1);
		Images images = new Images();
		images.setStartAt(0);
		images.setIndexPattern("000");
		images.setLength(1);
		images.setNamePattern("screen@index");
		comic.setImages(images);
		Screen screen = new Screen();
		screen.setIndex(0);
		screen.setBgcolor("");
		Frame frame = new Frame();
		frame.setStartX(0.);
		frame.setStartY(0.);
		frame.setSizeX(0.1);
		frame.setSizeY(0.1);
		frame.setTransitionDuration(1);
		screen.addFrame(frame);
		comic.addScreen(screen);
	}

	@Test
	public void testMarshallToSysOut() throws Exception {
		XmlMarshaller.startMarshallOf(Comic.class)
				.useFormattedOutput()
				.of(comic);
	}
}
