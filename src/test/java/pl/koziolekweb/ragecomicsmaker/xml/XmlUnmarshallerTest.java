package pl.koziolekweb.ragecomicsmaker.xml;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.koziolekweb.ragecomicsmaker.model.Comic;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class XmlUnmarshallerTest {

	private File file;

	@BeforeMethod
	public void setUp() throws Exception {
		file = new File("src/test/resources/comic.xml");
		assertThat(file).exists();
	}

	@Test
	public void testUnmarshalling() throws Exception {
		Comic from = (Comic) XmlUnmarshaller.startUnmarshallOf(Comic.class).from(file);
		assertThat(from).isNotNull();
		assertThat(from.getScreens()).hasSize(50);
	}
}
