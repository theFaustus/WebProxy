package com.isa.pad.marketwarehouse.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Faust on 11/12/2017.
 */
public class XmlSerializer {
    private static Logger logger = Logger.getLogger(XmlSerializer.class.getName());

    public static <T> String toXml(T data, Class<T> targetClass) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(data, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException e) {
            logger.log(Level.SEVERE, "JAXBException", e);
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    public static <T> T fromXml(String xmlData, Class<T> targetClass) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader stringReader = new StringReader(xmlData);
            return (T) unmarshaller.unmarshal(stringReader);
        } catch (JAXBException e) {
            logger.log(Level.SEVERE, "JAXBException", e);
        }
        return null;
    }
}
