package pl.koziolekweb.ragecomicsmaker.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import static com.google.common.collect.ComparisonChain.start;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class Screen implements Serializable, Comparable<Screen> {

	@JacksonXmlProperty(isAttribute = true)
	private int index;

	@JacksonXmlProperty(isAttribute = true)
	private String bgcolor;

	@JacksonXmlElementWrapper(localName = "frames")
	@JsonProperty(value = "frame")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Collection<Frame> frames;

	// a teraz drogie dzieci nie xmlowa część modelu tzw. core
	private File image;

	public Screen() {
		frames = new ArrayList<>();
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

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public Collection<Frame> getFrames() {
		int i = 0;
		for (Frame frame : frames) {
			frame.setId(i);
			i++;
		}

		return frames;
	}

	@Override
	public int compareTo(Screen that) {
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
		TreeSet<Frame> newFrames = new TreeSet<Frame>();
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
}
