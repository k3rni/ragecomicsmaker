package pl.koziolekweb.ragecomicsmaker.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ComparisonChain.start;
import static java.util.Arrays.asList;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class Frame implements Serializable, Comparable<Frame> {

	@XmlAttribute(required = true)
	private String relativeArea;

	@XmlAttribute(required = true)
	private double transitionDuration;

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

	/**
	 * Frame is visible by default. This field is @XmlTransient and will be ignored by JAXB (un)marshaller.
	 */
	@XmlTransient
	private boolean visibility = true;


	public Frame() {
		//
	}

	public Frame(int id) {
		this.id = id;
	}

	public Frame(Frame other, int newId) {
		this.relativeArea = other.relativeArea;
		this.transitionDuration = other.transitionDuration;
		this.startX = other.startX;
		this.startY = other.startY;
		this.sizeX = other.sizeX;
		this.sizeY = other.sizeY;
		this.id = newId;
	}

	@XmlTransient
	public boolean isVisible() {
		return visibility;
	}

	public void visible() {
		this.visibility = true;
	}

	public void unvisible() {
		this.visibility = false;
	}

	@XmlTransient
	public String getRelativeArea() {
		return relativeArea;
	}

	public void setRelativeArea(String relativeArea) {
		checkArgument(relativeArea.matches("\\d.\\d\\d\\d? \\d.\\d\\d\\d? \\d.\\d\\d\\d? \\d.\\d\\d\\d?"));
		this.relativeArea = relativeArea;
		recountValues();
	}

	@XmlTransient
	public double getTransitionDuration() {
		return transitionDuration;
	}

	public void setTransitionDuration(double transitionDuration) {
		this.transitionDuration = transitionDuration;
	}

	@XmlTransient
	public double getStartX() {
		recountValues();
		return startX;
	}

	public void setStartX(double startX) {
		checkArgument(startX <= 1.00);
		this.startX = startX;
		recountRelativeArea();
	}

	@XmlTransient
	public double getStartY() {
		recountValues();
		return startY;
	}

	public void setStartY(double startY) {
		checkArgument(startY <= 1.00);
		this.startY = startY;
		recountRelativeArea();
	}

	@XmlTransient
	public double getSizeX() {
		recountValues();
		return sizeX;
	}

	public void setSizeX(double sizeX) {
		checkArgument(sizeX <= 1.00);
		this.sizeX = sizeX;
		recountRelativeArea();
	}

	@XmlTransient
	public double getSizeY() {
		recountValues();
		return sizeY;
	}

	public void setSizeY(double sizeY) {
		checkArgument(sizeY <= 1.00);
		this.sizeY = sizeY;
		recountRelativeArea();
	}

	@XmlTransient
	public int getId() {
		return id;
	}

	public void setId(int i) {
		id = i;
	}

	private void recountRelativeArea() {
		relativeArea = String.format(Locale.ENGLISH, "%.3f %.3f %.3f %.3f", startX, startY, sizeX, sizeY);
	}

	private void recountValues() {
		String[] split = relativeArea.split(" ");
		Double[] doubles =
				transform(
						transform(asList(split),
								input -> Double.valueOf(input)),
						input -> {
							checkArgument(input >= 0.);
							checkArgument(input <= 1.);
							return input;
						}
				).toArray(new Double[4]);
		startX = doubles[0];
		startY = doubles[1];
		sizeX = doubles[2];
		sizeY = doubles[3];

	}

	@Override
	public int compareTo(Frame that) {
		if (that == null)
			return 1;
		return start().compare(this.id, that.id).result();
	}

}
