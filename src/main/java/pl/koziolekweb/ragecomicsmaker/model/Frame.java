package pl.koziolekweb.ragecomicsmaker.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ComparisonChain.start;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class Frame implements Serializable, Comparable<Frame> {

	@XmlAttribute(required = true)
	private String relativeArea;

	@XmlAttribute(required = true)
	private int transitionDuration;

	@XmlTransient
	private double startX;
	@XmlTransient
	private double startY;
	@XmlTransient
	private double sizeX;
	@XmlTransient
	private double sizeY;
	@XmlTransient
	private int id;

	public Frame() {
		//
	}

	public Frame(int id) {
		this.id = id;
	}

	@XmlTransient
	public String getRelativeArea() {
		return relativeArea;
	}

	public void setRelativeArea(String relativeArea) {
		checkArgument(relativeArea.matches("\\d.\\d\\d \\d.\\d\\d \\d.\\d\\d \\d.\\d\\d"));
		this.relativeArea = relativeArea;
	}

	@XmlTransient
	public int getTransitionDuration() {
		return transitionDuration;
	}

	public void setTransitionDuration(int transitionDuration) {
		this.transitionDuration = transitionDuration;
	}

	@XmlTransient
	public double getStartX() {
		return startX;
	}

	public void setStartX(double startX) {
		checkArgument(startX <= 1.00);
		this.startX = startX;
		recountRelativeArea();
	}

	@XmlTransient
	public double getStartY() {
		return startY;
	}

	public void setStartY(double startY) {
		checkArgument(startY <= 1.00);
		this.startY = startY;
		recountRelativeArea();
	}

	@XmlTransient
	public double getSizeX() {
		return sizeX;
	}

	public void setSizeX(double sizeX) {
		checkArgument(sizeX <= 1.00);
		this.sizeX = sizeX;
		recountRelativeArea();
	}

	@XmlTransient
	public double getSizeY() {
		return sizeY;
	}

	public void setSizeY(double sizeY) {
		checkArgument(sizeY <= 1.00);
		this.sizeY = sizeY;
		recountRelativeArea();
	}

	private void recountRelativeArea() {
		setRelativeArea(String.format(Locale.ENGLISH, "%.2f %.2f %.2f %.2f", startX, startY, sizeX, sizeY));
	}

	@Override
	public int compareTo(Frame that) {
		if (that == null)
			return 1;
		return start().compare(this.id, that.id).result();
	}
}
