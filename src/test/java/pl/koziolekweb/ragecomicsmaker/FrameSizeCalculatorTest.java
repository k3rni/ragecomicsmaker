package pl.koziolekweb.ragecomicsmaker;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.koziolekweb.ragecomicsmaker.event.AddFrameEvent;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.Offset.offset;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FrameSizeCalculatorTest {

	private FrameSizeCalculator fsc;

	@BeforeMethod
	public void setUp() throws Exception {
		fsc = new FrameSizeCalculator();
	}

	@Test
	public void testCalculateProportionBasic() throws Exception {
		double proportion = fsc.calculateProportion(250, 500);
		assertThat(proportion).isEqualTo(0.5, offset(0.01));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testCalculateProportionArg1() throws Exception {
		fsc.calculateProportion(-250, 500);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testCalculateProportionArg2() throws Exception {
		fsc.calculateProportion(250, -500);
	}

	@Test
	public void testBuildFrameRec() throws Exception {
		AddFrameEvent.FrameRect frameRect = fsc.buildFrameRec(10, 10, 50, 50, 100, 100);
		assertThat(frameRect.startX).isEqualTo(0.1, offset(0.01));
		assertThat(frameRect.startY).isEqualTo(0.1, offset(0.01));
		assertThat(frameRect.width).isEqualTo(0.5, offset(0.01));
		assertThat(frameRect.height).isEqualTo(0.5, offset(0.01));
	}

	@Test
	public void testCalculateSize() throws Exception {
		int calculatedSize = fsc.calculateSize(0.5, 100);
		assertThat(calculatedSize).isEqualTo(50);
	}
}
