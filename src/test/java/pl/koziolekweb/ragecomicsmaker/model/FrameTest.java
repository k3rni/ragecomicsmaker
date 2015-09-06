package pl.koziolekweb.ragecomicsmaker.model;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.Offset.offset;

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
		assertThat(frame.getRelativeArea()).isEqualTo("0.000 0.000 0.000 0.000");

		//change one by one
		frame.setStartX(0.1);
		assertThat(frame.getRelativeArea()).isEqualTo("0.100 0.000 0.000 0.000");

		frame.setStartY(0.2);
		assertThat(frame.getRelativeArea()).isEqualTo("0.100 0.200 0.000 0.000");

		frame.setSizeX(0.3);
		assertThat(frame.getRelativeArea()).isEqualTo("0.100 0.200 0.300 0.000");

		frame.setSizeY(0.4);
		assertThat(frame.getRelativeArea()).isEqualTo("0.100 0.200 0.300 0.400");
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

	@Test
	public void testSetRelativeArea() throws Exception {
		frame.setRelativeArea("0.10 0.20 0.30 0.40");
		assertThat(frame.getStartX()).isEqualTo(0.1, offset(0.01));
		assertThat(frame.getStartY()).isEqualTo(0.2, offset(0.01));
		assertThat(frame.getSizeX()).isEqualTo(0.3, offset(0.01));
		assertThat(frame.getSizeY()).isEqualTo(0.4, offset(0.01));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetRelativeArea1Over1() throws Exception {
		frame.setRelativeArea("1.10 0.20 0.30 0.40");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetRelativeArea2Over1() throws Exception {
		frame.setRelativeArea("0.10 1.20 0.30 0.40");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetRelativeArea3Over1() throws Exception {
		frame.setRelativeArea("0.10 0.20 1.30 0.40");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetRelativeArea4Over1() throws Exception {
		frame.setRelativeArea("0.10 0.20 0.30 1.40");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetRelativeArea1Under0() throws Exception {
		frame.setRelativeArea("-0.10 0.20 0.30 0.40");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetRelativeArea2Under0() throws Exception {
		frame.setRelativeArea("0.10 -0.20 0.30 0.40");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetRelativeArea3Under0() throws Exception {
		frame.setRelativeArea("0.10 0.20 -0.30 0.40");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSetRelativeArea4Under0() throws Exception {
		frame.setRelativeArea("0.10 0.20 0.30 -0.40");
	}

}
