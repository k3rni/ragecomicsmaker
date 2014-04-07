package pl.koziolekweb.ragecomicsmaker.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class Images implements Serializable {

	@XmlAttribute(required = true)
	private int length;
	@XmlAttribute(required = true)
	private int startAt;
	@XmlAttribute(required = true)
	private String indexPattern;
	@XmlAttribute(required = true)
	private String namePattern;

	public Images() {
		//
	}


	@XmlTransient
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@XmlTransient
	public int getStartAt() {
		return startAt;
	}

	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	@XmlTransient
	public String getIndexPattern() {
		return indexPattern;
	}

	public void setIndexPattern(String indexPattern) {
		this.indexPattern = indexPattern;
	}

	@XmlTransient
	public String getNamePattern() {
		return namePattern;
	}

	public void setNamePattern(String namePattern) {
		this.namePattern = namePattern;
	}

	public Images initDefaults() {
		this.length = 0;
		this.startAt = 0;
		this.indexPattern = "";
		this.namePattern = "";
		return this;
	}
}
