package pl.koziolekweb.ragecomicsmaker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import javafx.geometry.Point3D;

import javax.annotation.Nullable;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Collections2.transform;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static pl.koziolekweb.ragecomicsmaker.MathUtil.clamp;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
@JacksonXmlRootElement(localName = "frame")
public class Frame implements Comparable<Frame> {
	@JsonProperty
	@JacksonXmlProperty(isAttribute = true)
	private String relativeArea;
	@JsonProperty
	@JacksonXmlProperty(isAttribute = true)
	private double transitionDuration;
	@JsonIgnore
	private double startX;
	@JsonIgnore
	private double startY;
	@JsonIgnore
	private double sizeX;
	@JsonIgnore
	private double sizeY;
	@JsonProperty
	@JacksonXmlProperty(isAttribute = true)
	private int id;

	@JsonIgnore
	private boolean visibility = true;

	// Must exist for Jackson to create Frame objects
	public Frame() {
	}

	public static Frame createNewFrame(int frameId, Point3D start, Point3D end, double imgWidth, double imgHeight) {
		if (end == null) return null;

		double sy = clamp(0, start.getY(), imgHeight);
		double ey = clamp(0, end.getY(), imgHeight);
		double sx = clamp(0, start.getX(), imgWidth);
		double ex = clamp(0, end.getX(), imgWidth);

		double top = min(sy, ey);
		double left = min(sx, ex);

		double width = Math.abs(ex - sx);
		double height = Math.abs(ey - sy);

		if (width / imgWidth < 0.05 || height / imgHeight < 0.05)
			return null;

		Frame frame = new Frame(frameId);
		frame.setStartX(left / imgWidth);
		frame.setStartY(top / imgHeight);
		frame.setSizeX(width / imgWidth);
		frame.setSizeY(height / imgHeight);
		frame.setTransitionDuration(1);
		return frame;
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

	@JsonIgnore
	public boolean isVisible() {
		return visibility;
	}

	public void visible() {
		this.visibility = true;
	}

	public void unvisible() {
		this.visibility = false;
	}

	public String getRelativeArea() {
		return relativeArea;
	}

	public void setRelativeArea(String relativeArea) {
		checkArgument(relativeArea.matches("\\d.\\d\\d\\d? \\d.\\d\\d\\d? \\d.\\d\\d\\d? \\d.\\d\\d\\d?"));
		this.relativeArea = relativeArea;
		recountValues();
	}

	public double getTransitionDuration() {
		return transitionDuration;
	}

	public void setTransitionDuration(double transitionDuration) {
		this.transitionDuration = transitionDuration;
	}

	public double getStartX() {
		recountValues();
		return startX;
	}

	public void setStartX(double startX) {
		checkArgument(startX <= 1.00);
		this.startX = startX;
		recountRelativeArea();
	}

	public double getStartY() {
		recountValues();
		return startY;
	}

	public void setStartY(double startY) {
		checkArgument(startY <= 1.00);
		this.startY = startY;
		recountRelativeArea();
	}

	public double getSizeX() {
		recountValues();
		return sizeX;
	}

	public void setSizeX(double sizeX) {
		checkArgument(sizeX <= 1.00);
		this.sizeX = sizeX;
		recountRelativeArea();
	}

	public double getSizeY() {
		recountValues();
		return sizeY;
	}

	public void setSizeY(double sizeY) {
		checkArgument(sizeY <= 1.00);
		this.sizeY = sizeY;
		recountRelativeArea();
	}

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
						transform(asList(split), Double::valueOf),
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
	public int compareTo(@Nullable Frame that) {
		if (that == null) return 1;
		return Integer.compare(this.id, that.id);
	}

	@JsonIgnore
	public String getLabel() {
		return String.format("%d", id);
	}

}
