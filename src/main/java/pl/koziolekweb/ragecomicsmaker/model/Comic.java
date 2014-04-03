package pl.koziolekweb.ragecomicsmaker.model;

import pl.koziolekweb.ragecomicsmaker.xml.DirectionAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.TreeSet;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
@XmlRootElement(name = "comic")
public class Comic implements Serializable {

	@XmlAttribute(required = true)
	private int version;
	@XmlAttribute(required = true)
	private String id;
	@XmlAttribute(required = true)
	private String title;
	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(DirectionAdapter.class)
	private Direction direction;
	@XmlAttribute(required = true)
	private String orientation;
	@XmlAttribute(required = true)
	private String transition;
	@XmlAttribute(required = true)
	private String bgcolor;

	@XmlElement
	private Images images;

	@XmlElement(name = "screen")
	private TreeSet<Screen> screens;

	public Comic() {
		screens = new TreeSet<Screen>();
	}

	public void addScreen(Screen screen) {
		screens.add(screen);
	}

	@XmlTransient
	public Images getImages() {
		return images;
	}

	public void setImages(Images images) {
		this.images = images;
	}

	@XmlTransient
	public TreeSet<Screen> getScreens() {
		return screens;
	}

	public void setScreens(TreeSet<Screen> screens) {
		this.screens = screens;
	}

	@XmlTransient
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@XmlTransient
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlTransient
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlTransient
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@XmlTransient
	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	@XmlTransient
	public String getTransition() {
		return transition;
	}

	public void setTransition(String transition) {
		this.transition = transition;
	}

	@XmlTransient
	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
}
