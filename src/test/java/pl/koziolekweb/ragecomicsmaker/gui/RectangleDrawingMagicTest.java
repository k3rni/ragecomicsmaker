package pl.koziolekweb.ragecomicsmaker.gui;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.*;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class RectangleDrawingMagicTest {

	@Mock
	private Graphics g;
	private RectangleDrawingMagic rdm;

	@BeforeMethod
	public void setUp() throws Exception {
		initMocks(this);
		rdm = new RectangleDrawingMagic();
	}

	@Test
	public void topLeftRightBottom() throws Exception {
		rdm.paintRectangle(g, 0, 0, 10, 10);
		verify(g, atLeastOnce()).fillRect(0, 0, 10, 10);
	}

	@Test
	public void rightBottomLeftTop() throws Exception {
		rdm.paintRectangle(g, 10, 10, 0, 0);
		verify(g, atLeastOnce()).fillRect(0, 0, 10, 10);
	}

	@Test
	public void rightTopLeftBottom() throws Exception {
		rdm.paintRectangle(g, 10, 0, 0, 10);
		verify(g, atLeastOnce()).fillRect(0, 0, 10, 10);
	}
}
