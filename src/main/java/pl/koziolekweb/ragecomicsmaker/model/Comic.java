package pl.koziolekweb.ragecomicsmaker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.collect.Collections2;
import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEvent;
import pl.koziolekweb.ragecomicsmaker.event.MetadataUpdateEvent;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.Serializable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
@SuppressWarnings("UnstableApiUsage")
@XmlRootElement(name = "comic")
public class Comic implements Serializable {
	@JacksonXmlProperty(isAttribute = true)
	private int version;
	@JacksonXmlProperty(isAttribute = true)
	private String id;

	public String title;

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
	private TreeSet<Screen> screens = new TreeSet<>();

	@JsonProperty
	public String description;
	@JsonProperty
	public String author;
	@JsonProperty
	public String illustrator;
	@JsonProperty
	public String publisher;
	@JsonProperty
	public String publicationDate;
	@JsonProperty
	public String isbn;
	@JsonProperty
	public String rights;

	public Comic() {
		initDefaults();
	}

	/**
	 * Methods to init object with default values. Need in first sprint.
	 *
	 * @return Comic with default values
	 */
	@SuppressWarnings("UnusedReturnValue")
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
		String filename = lastSelectedPathComponent.split(" ", 2)[0];
		Collection<Screen> filtered = Collections2.filter(screens, input -> {
			File image = Objects.requireNonNull(input).getImage();
			if (image == null) return false;
			return filename.equals(image.getName());
		});
		checkState(filtered.size() == 1);
		return filtered.iterator().next();
	}

	public Screen findScreenByIndex(String number) {
		try {
			final int intNumber = Integer.parseInt(number);
			Collection<Screen> filtered = Collections2.filter(screens,
					input -> Objects.requireNonNull(input).getIndex() == intNumber);
			if (filtered.iterator().hasNext())
				return filtered.iterator().next();
			return new Screen(); // tak naprawdę do niczego nie podpiety null object
		} catch (Exception e) {
			App.EVENT_BUS.post(new ErrorEvent("Nieoczekiwany błąd odczytu - nieprawidłowy numer pliku " + number, e));
			return new Screen();
		}
	}

	public void updateMetadata(MetadataUpdateEvent event) {
		this.title = event.title;
		this.description = event.descr;
		this.author = event.authors;
		this.illustrator = event.illustrators;
		this.publisher = event.publisher;
		this.publicationDate = event.pubDate;
		this.isbn = event.isbn;
		this.rights = event.rights;
	}

	@JsonIgnore
    public String getLabel() {
        return "BOOK";
    }
}
