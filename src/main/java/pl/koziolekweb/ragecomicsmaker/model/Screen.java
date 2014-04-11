package pl.koziolekweb.ragecomicsmaker.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.io.Serializable;
import java.util.TreeSet;

import static com.google.common.collect.ComparisonChain.start;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class Screen implements Serializable, Comparable<Screen> {

	@XmlAttribute(required = true)
	private int index;
	@XmlAttribute(required = true)
	private String bgcolor;

	@XmlElement
	private TreeSet<Frame> frames;

	// a teraz drogie dzieci nie xmlowa część modelu tzw. core
	@XmlTransient
	private File image;

	public Screen() {
		frames = new TreeSet<Frame>();
	}

	@XmlTransient
	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public void addFrame(Frame frame) {
		frames.add(frame);
	}

	@XmlTransient
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@XmlTransient
	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	@XmlTransient
	public TreeSet<Frame> getFrames() {
		return frames;
	}

	public void setFrames(TreeSet<Frame> frames) {
		this.frames = frames;
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
}
