package pl.koziolekweb.ragecomicsmaker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.Buffer;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.collect.ComparisonChain.start;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class Screen implements Serializable, Comparable<Screen> {

	@JacksonXmlProperty(isAttribute = true)
	private long index;

	@JacksonXmlProperty(isAttribute = true)
	private String bgcolor;

	@JacksonXmlElementWrapper(localName = "frames")
	@JsonProperty(value = "frame")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Set<Frame> frames;

	// a teraz drogie dzieci nie xmlowa część modelu tzw. core
	private File image;

	public Screen(File image, String bgcolor, long index) {
		this();
		this.image = image;
		this.bgcolor = bgcolor;
		this.index = index;
	}

	public Screen() {
		frames = new TreeSet<>();
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public void addFrame(Frame frame) {
		frames.add(frame);
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public Set<Frame> getFrames() {
		int i = 0;
		for (Frame frame : frames) {
			frame.setId(i);
			i++;
		}

		return frames;
	}

	public Frame findFrame(int id) {
		return frames.stream()
				.filter((f) -> f.getId() == id)
				.findFirst().orElse(null);
	}

	public BufferedImage crop(Frame frame) throws IOException {
		BufferedImage image = ImageIO.read(this.image);

		double x = frame.getStartX() * image.getWidth();
		double w = frame.getSizeX() * image.getWidth();
		double y = frame.getStartY() * image.getHeight();
		double h = frame.getSizeY() * image.getHeight();

		return image.getSubimage(
				(int) Math.round(x),
				(int) Math.round(y),
				(int) Math.round(w),
				(int) Math.round(h));
	}

	@Override
	public int compareTo(@Nullable Screen that) {
		if (that == null)
			return 1;
		return start().compare(this.index, that.index)
				.result();
	}

	public void removeFrame(Frame frame) {
		frames.remove(frame);
		recount();
	}

	private void recount() {
		TreeSet<Frame> newFrames = new TreeSet<>();
		int i = 0;
		for (Frame frame : frames) {
			newFrames.add(new Frame(frame, i));
			i++;
		}
		frames = newFrames;
	}

	public void moveFrameUp(Frame frame) {
		int id = frame.getId();
		Frame weGoHere = frames.stream().filter(f -> f.getId() == id - 1).findFirst().get();
		frame.setId(id - 1);
		weGoHere.setId(id);
		frames = frames.stream().collect(TreeSet::new, TreeSet::add, TreeSet::addAll);
	}

	public void moveFrameDown(Frame frame) {
		int id = frame.getId();
		Frame weGoHere = frames.stream().filter(f -> f.getId() == id + 1).findFirst().get();
		frame.setId(id + 1);
		weGoHere.setId(id);
		frames = frames.stream().collect(TreeSet::new, TreeSet::add, TreeSet::addAll);
	}

	@JsonIgnore
	public int getScreenSize() {
		return getFrames().size();
	}

	@JsonIgnore
	public String getLabel() {
		if (frames.isEmpty())
			return image.getName();
		return String.format("%s [%d]", image.getName(), frames.size());
	}
}
