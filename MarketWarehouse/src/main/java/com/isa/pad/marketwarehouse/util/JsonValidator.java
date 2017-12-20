package com.isa.pad.marketwarehouse.util;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.logging.Logger;

/**
 * Created by Faust on 11/13/2017.
 */
public class JsonValidator {
    private static Logger logger = Logger.getLogger(JsonValidator.class.getName());

    private String schemaFilePath;
    private String message;

    public JsonValidator(String schemaFilePath) {
        this.schemaFilePath = schemaFilePath;
    }

    public boolean validate(String jsonData) {
        JSONObject jsonSchema = new JSONObject(
                new JSONTokener(getClass().getClassLoader().getResourceAsStream(schemaFilePath)));
        Schema schema = SchemaLoader.load(jsonSchema);
        try {
            schema.validate(new JSONObject(jsonData));
            return true;
        } catch (ValidationException e) {
            message = e.getMessage();
            return false;
        }

    }

    public String getMessage() {
        return message;
    }
}
