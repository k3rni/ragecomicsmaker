package pl.koziolekweb.ragecomicsmaker.xml;

import pl.koziolekweb.ragecomicsmaker.model.Direction;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class DirectionAdapter extends XmlAdapter<String, Direction> {
	@Override
	public Direction unmarshal(String v) throws Exception {
		return Direction.valueOf(v.toUpperCase());
	}

	@Override
	public String marshal(Direction v) throws Exception {
		return v.name().toLowerCase();
	}
}
