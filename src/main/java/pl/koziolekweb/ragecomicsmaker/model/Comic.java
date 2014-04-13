package pl.koziolekweb.ragecomicsmaker.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import pl.koziolekweb.ragecomicsmaker.xml.DirectionAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Collection;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
	private TreeSet<Screen> screens = new TreeSet<Screen>();

	public Comic() {
		initDefaults();
	}

	/**
	 * Methods to init object with default values. Need in first sprint.
	 *
	 * @return Comic with default values
	 */
	public Comic initDefaults() {
		this.version = 0;
		this.id = "";
		this.title = "";
		this.direction = Direction.LTR;
		this.orientation = "";
		this.transition = "";
		this.bgcolor = "#FFFFFF";
		this.images = new Images().initDefaults();
		return this;
	}

	public void addScreen(Screen screen) {
		screens.add(screen);
		images.setLength(screens.size());
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

	public Screen findScreenByFileName(final String lastSelectedPathComponent) {
		checkNotNull(lastSelectedPathComponent);
		Collection<Screen> filtered = Collections2.filter(screens, new Predicate<Screen>() {
			@Override
			public boolean apply(Screen input) {
				return lastSelectedPathComponent.equals(input.getImage().getName());
			}
		});
		checkState(filtered.size() == 1);
		return filtered.iterator().next();
	}
}
