package com.isa.pad.marketwarehouse.util;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Faust on 11/12/2017.
 */
public class XmlValidator implements ValidationEventHandler {
    private static Logger logger = Logger.getLogger(XmlValidator.class.getName());

    private boolean isValid = true;
    private String schemaFilePath;
    private Class<?> targetClass;
    private String message;

    public XmlValidator(String schemaFilePath, Class<?> targetClass) {
        this.schemaFilePath = schemaFilePath;
        this.targetClass = targetClass;
    }

    public boolean validate(String xmlData) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(getClass().getClassLoader().getResource(schemaFilePath).getFile()));
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(this);
            StringReader stringReader = new StringReader(xmlData);
            unmarshaller.unmarshal(stringReader);
        } catch (JAXBException e) {
            logger.log(Level.SEVERE, "JAXBException", e);
        } catch (SAXException e) {
            logger.log(Level.SEVERE, "SAXException", e);
        }
        return isValid;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean handleEvent(ValidationEvent event) {
        isValid = event.getSeverity() != ValidationEvent.ERROR && event.getSeverity() != ValidationEvent.FATAL_ERROR;
        message = event.getMessage();
        return isValid;
    }
}
