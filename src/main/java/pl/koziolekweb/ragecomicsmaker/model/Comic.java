package pl.koziolekweb.ragecomicsmaker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Metadata;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEvent;
import pl.koziolekweb.ragecomicsmaker.xml.DirectionAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
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
	@JacksonXmlProperty(isAttribute = true)
	private int version;
	@JacksonXmlProperty(isAttribute = true)
	private String id;

	private String title;

	@JacksonXmlProperty(isAttribute = true)
	private Direction direction;
	@JacksonXmlProperty(isAttribute = true)
	private String orientation;
	@JacksonXmlProperty(isAttribute = true)
	private String transition;
	@JacksonXmlProperty(isAttribute = true)
	private String bgcolor;

	private Images images;

	@JacksonXmlElementWrapper(localName = "screens")
	@JsonProperty(value = "screen") // Applies to elements of this set
	private TreeSet<Screen> screens = new TreeSet<Screen>();

	@JsonProperty
	private Metadata metadata;

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
		this.metadata = new Metadata();
		return this;
	}

	public void addScreen(Screen screen) {
		screens.add(screen);
		images.setLength(screens.size());
	}

	public Images getImages() {
		return images;
	}

	public void setImages(Images images) {
		this.images = images;
	}

	public TreeSet<Screen> getScreens() {
		return screens;
	}

	public void setScreens(TreeSet<Screen> screens) {
		this.screens = screens;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getTransition() {
		return transition;
	}

	public void setTransition(String transition) {
		this.transition = transition;
	}

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
				File image = input.getImage();
				if (image == null) return false;
				return lastSelectedPathComponent.equals(image.getName());
			}
		});
		checkState(filtered.size() == 1);
		return filtered.iterator().next();
	}

	public Screen findScreenByIndex(String number) {
		try {
			final int intNumber = Integer.parseInt(number);
			Collection<Screen> filtered = Collections2.filter(screens, input -> input.getIndex() == intNumber);
			if (filtered.iterator().hasNext())
				return filtered.iterator().next();
			return new Screen(); // tak naprawdę do niczego nie podpiety null object
		} catch (Exception e) {
			App.EVENT_BUS.post(new ErrorEvent("Nieoczekiwany błąd odczytu - nieprawidłowy numer pliku " + number, e));
			return new Screen();
		}
	}
}
