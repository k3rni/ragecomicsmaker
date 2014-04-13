package pl.koziolekweb.ragecomicsmaker.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class to marshall pl.koziolekweb.model.* to comic.xml file;
 * User: koziolek
 */
public class XmlMarshaller {

	private final Marshaller jaxbMarshaller;
	private OutputStream file;

	private XmlMarshaller(Class<?> clazz) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		jaxbMarshaller = jaxbContext.createMarshaller();
		file = System.out;
	}

	public static XmlMarshaller startMarshallOf(Class<?> clazz) throws JAXBException {
		return new XmlMarshaller(clazz);
	}

	public XmlMarshaller useFormattedOutput() throws PropertyException {
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		return this;
	}

	public XmlMarshaller useUnformattedOutput() throws PropertyException {
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		return this;
	}

	public XmlMarshaller to(OutputStream file) throws PropertyException {
		this.file = file;
		return this;
	}

	public <T> void of(T object) throws JAXBException {
		checkNotNull(object);
		jaxbMarshaller.marshal(object, file);
	}
}
