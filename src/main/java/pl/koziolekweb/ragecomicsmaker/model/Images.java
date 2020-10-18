package pl.koziolekweb.ragecomicsmaker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class Images implements Serializable {
	@JacksonXmlProperty(isAttribute = true)
	private int length;
	@JacksonXmlProperty(isAttribute = true)
	private int startAt;
	@JacksonXmlProperty(isAttribute = true)
	private String indexPattern;
	@JacksonXmlProperty(isAttribute = true)
	private String namePattern;

	public Images() {
		//
	}


	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}

	public int getStartAt() {
		return startAt;
	}
	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	public String getIndexPattern() {
		return indexPattern;
	}
	public void setIndexPattern(String indexPattern) {
		this.indexPattern = indexPattern;
	}

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
