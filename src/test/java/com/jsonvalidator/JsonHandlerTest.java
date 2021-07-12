package com.jsonvalidator;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import com.jayway.jsonpath.JsonPath;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;

public class JsonHandlerTest {

    User us = null;
    String json;
    // JsonSchema userSchema;

    @Before
    public void setUp() {
        json = "{\"address\": {\"country\": \"Spain\",\"city\": \"Madrid\",\"street\": \"Calle Alcala\"},\"gender\": \"Male\",\"name\": \"John\",\"id\": 1,\"email\": \"john@gmail.com\"}";
        // HyperSchemaFactoryWrapper userVisitor = new HyperSchemaFactoryWrapper();
        // ObjectMapper mapper = new ObjectMapper();
        // try {
        // mapper.acceptJsonFormatVisitor(User.class, userVisitor);
        // userSchema = userVisitor.finalSchema();
        // String schemaString =
        // mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userSchema);
        // System.out.println(schemaString);
        // } catch (Exception e) {
        // userSchema = null;
        // }
    }

    private boolean isJsonValid(String jsonString) {
        boolean output = true;
        try {
            new JSONObject(jsonString);
        } catch (Exception e) {
            output = false;
        }
        return output;
    }

    private Schema getSchema(String schemaPath) throws IOException{
        try (InputStream inputStream = getClass().getResourceAsStream("/schema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            return schema;
        } catch (IOException e) {
            throw e;
        }
    }

    @Test
    public void givenCreateJsonUserWithoutNameThenSchemaValidationFails() {
        System.out.println(json);
        String validJson = JsonPath.parse(json).delete("$..['name']").jsonString();
        System.out.println(validJson);
        
        assertTrue("Json is not valid", isJsonValid(validJson));
        try {
            Schema schema = getSchema("/schema.json");
            schema.validate(new JSONObject(validJson)); // throws a ValidationException if this object is invalid
        } catch (IOException e) {
            assertTrue("Error loading Schema", false);
            return;
        } catch (ValidationException ve) {
            assertTrue("Schema validation fails because of \"name\" field", true);
            return;
        }
        assertTrue("Name field is mandatory!", false);
    }

    @Test
    public void givenCreateJsonUserWithoutAddressThenSchemaValidationWorks() {
        System.out.println(json);
        String validJson = JsonPath.parse(json).delete("$..['address']").jsonString();
        System.out.println(validJson);
        
        assertTrue("Json is not valid", isJsonValid(validJson));
        try {
            Schema schema = getSchema("/schema.json");
            schema.validate(new JSONObject(validJson)); // throws a ValidationException if this object is invalid
        } catch (IOException e) {
            System.out.print("Error loading Schema");
        } catch (ValidationException ve) {
            assertTrue("Schema validation fails because of \"address\" field is optional", false);
        }
        assertTrue("address field is optional. OK", true);
    }

}
