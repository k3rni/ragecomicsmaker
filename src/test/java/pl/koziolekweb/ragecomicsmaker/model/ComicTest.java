package pl.koziolekweb.ragecomicsmaker.model;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class ComicTest {

	private Comic comic;

	@BeforeMethod
	public void setUp() throws Exception {
		comic = new Comic();
		comic.addScreen(buildScreen("s1"));
		comic.addScreen(buildScreen("s2"));
		comic.addScreen(buildScreen("s3"));
		comic.addScreen(buildScreen("DD"));
		comic.addScreen(buildScreen("DD"));
	}

	private Screen buildScreen(String pathname) {
		Screen s1 = new Screen();
		s1.setImage(new File(pathname));
		return s1;
	}

	@Test
	public void shoudlFindScreen() throws Exception {
		Screen s1 = comic.findScreenByFileName("s1");
		assertThat(s1).isNotNull();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldThrowExWhenDuplicatedFileName() throws Exception {
		comic.findScreenByFileName("DD");
	}
}
