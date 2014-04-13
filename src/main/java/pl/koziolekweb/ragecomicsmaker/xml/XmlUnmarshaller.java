package pl.koziolekweb.ragecomicsmaker.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class XmlUnmarshaller {

	private final Unmarshaller unmarshaller;

	private XmlUnmarshaller(Class clazz) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		unmarshaller = jaxbContext.createUnmarshaller();
	}

	public static XmlUnmarshaller startUnmarshallOf(Class clazz) throws JAXBException {
		return new XmlUnmarshaller(clazz);
	}

	public Object from(File file) throws JAXBException {
		Object obj = unmarshaller.unmarshal(file);
		return obj;
	}
}
