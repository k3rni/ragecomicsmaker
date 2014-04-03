package pl.koziolekweb.ragecomicsmaker.model;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FrameTest {

	private Frame frame;

	@BeforeMethod
	public void setUp() throws Exception {
		frame = new Frame();
	}

	@Test
	public void testSetAllSizes() throws Exception {
		//when
		frame.setStartX(0.);
		frame.setStartY(0.);
		frame.setSizeX(0.);
		frame.setSizeY(0.);
		//then
		assertThat(frame.getRelativeArea()).isEqualTo("0.00 0.00 0.00 0.00");

		//change one by one
		frame.setStartX(0.1);
		assertThat(frame.getRelativeArea()).isEqualTo("0.10 0.00 0.00 0.00");

		frame.setStartY(0.2);
		assertThat(frame.getRelativeArea()).isEqualTo("0.10 0.20 0.00 0.00");

		frame.setSizeX(0.3);
		assertThat(frame.getRelativeArea()).isEqualTo("0.10 0.20 0.30 0.00");

		frame.setSizeY(0.4);
		assertThat(frame.getRelativeArea()).isEqualTo("0.10 0.20 0.30 0.40");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetStartXOver1() throws Exception {
		frame.setStartX(1.1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetStartYOver1() throws Exception {
		frame.setStartY(1.1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetSizeXOver1() throws Exception {
		frame.setSizeX(1.1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetSizeYOver1() throws Exception {
		frame.setSizeY(1.1);
	}
}
