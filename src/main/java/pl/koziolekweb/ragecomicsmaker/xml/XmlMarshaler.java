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
public class XmlMarshaler {

	private final Marshaller jaxbMarshaller;
	private OutputStream file;

	private XmlMarshaler(Class<?> clazz) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		jaxbMarshaller = jaxbContext.createMarshaller();
		file = System.out;
	}

	public static XmlMarshaler startMarshallOf(Class<?> clazz) throws JAXBException {
		return new XmlMarshaler(clazz);
	}

	public XmlMarshaler useFormattedOutput() throws PropertyException {
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		return this;
	}

	public XmlMarshaler useUnformattedOutput() throws PropertyException {
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		return this;
	}

	public XmlMarshaler to(OutputStream file) throws PropertyException {
		this.file = file;
		return this;
	}

	public <T> void of(T object) throws JAXBException {
		checkNotNull(object);
		jaxbMarshaller.marshal(object, file);
	}
}
