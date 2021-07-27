package com.jsonvalidator;

import static org.junit.Assert.assertEquals;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JsonHandlerTest {

    User us = null;
    String json;
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        json = "{\"address\": {\"country\": \"Spain\",\"city\": \"Madrid\",\"street\": \"Calle Alcala\"},\"gender\": \"Male\",\"name\": \"John\",\"id\": 1,\"email\": \"john@gmail.com\"}";

        Address addr = new Address();
        addr.setStreet("Calle Alcala");
        addr.setCity("Madrid");
        addr.setCountry("Spain");

        us = new User();
        us.setId(1);
        us.setName("John");
        us.setGender("Male");
        us.setEmail("john@gmail.com");
        us.setAddress(addr);
    }


    private void generateSchema() {
        // HyperSchemaFactoryWrapper userVisitor = new HyperSchemaFactoryWrapper();
        // ObjectMapper mapper = new ObjectMapper();
        // try {
        //     mapper.acceptJsonFormatVisitor(User.class, userVisitor);
        //     userSchema = userVisitor.finalSchema();
        //     String schemaString =
        //     mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userSchema);
        //     System.out.println(schemaString);
        // } catch (Exception e) {
        //     userSchema = null;
        // }
    }

    private boolean isJsonWellFormed(String jsonString) {
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
    public void givenUserWithoutName_whenValidateJson_ThenSchemaValidationFails() {

        System.out.println("\t-" + new Object(){}.getClass().getEnclosingMethod().getName() + "()");
        // THEN
        exceptionRule.expect(ValidationException.class);
        exceptionRule.expectMessage("required key [name] not found");

        // GIVEN
        String validJson = JsonPath.parse(json).delete("$..['name']").jsonString();
        System.out.println(validJson);
        
        assertTrue("Json is not valid", isJsonWellFormed(validJson));
        try {
            // WHEN
            Schema schema = getSchema("/schema.json");
            schema.validate(new JSONObject(validJson)); // throws a ValidationException if this object is invalid
        } catch (IOException e) {
            assertTrue("Error loading Schema", false);
            return;
        } 
        
    }

    @Test
    public void givenUserWithoutAddress_whenValidateJson_ThenSchemaValidationWorks() {

        System.out.println("\t-" + new Object(){}.getClass().getEnclosingMethod().getName() + "()");

        //GIVEN
        String validJson = JsonPath.parse(json).delete("$..['address']").jsonString();
        System.out.println(validJson);
        
        assertTrue("Json is not valid", isJsonWellFormed(validJson));
        try {
            //WHEN
            Schema schema = getSchema("/schema.json");
            schema.validate(new JSONObject(validJson)); // throws a ValidationException if this object is invalid
        } catch (IOException e) {
            assertTrue("Error loading Schema", false);
            return;
        //THEN
        } catch (ValidationException ve) {
            assertTrue("Schema validation fails because of \"address\" field is optional", false);
        }
        assertTrue("address field is optional. OK", true);
    }

    @Test
    public void givenUserWrongTypeOfId_whenValidateJson_ThenSchemaValidationFails() {

        System.out.println("\t-" + new Object(){}.getClass().getEnclosingMethod().getName() + "()");
        //THEN
        exceptionRule.expect(ValidationException.class);
        exceptionRule.expectMessage("id: expected type: Number, found: String");

        //GIVEN
        String validJson = new JSONObject(json).put("id","algo").toString();
        System.out.println(validJson);
        
        assertTrue("Json is not valid", isJsonWellFormed(validJson));
        try {
            //WHEN
            Schema schema = getSchema("/schema.json");
            schema.validate(new JSONObject(validJson)); // throws a ValidationException if this object is invalid
        } catch (IOException e) {
            assertTrue("Error loading Schema", false);
            return;
        } 
        
    }

    @Test
    public void givenUserWrongGender_ThenPluginAccessMethodsWorks() {

        System.out.println("\t-" + new Object(){}.getClass().getEnclosingMethod().getName() + "()");

        //GIVEN
        String wrongGenderJson = json.replaceAll("Male", "Masculino");
        
        //WHEN
        String value = JsonPath.read(wrongGenderJson, "$.gender");
        //THEN
        System.out.println("Gender: " + value);
        assertEquals("Masculino", value);
        
    }

    @Test
    public void givenUserWrongGender_WhenCreateJSON_ThenExceptionRaised() {

        System.out.println("\t-" + new Object(){}.getClass().getEnclosingMethod().getName() + "()");
        //THEN
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Gender value not supported! Expected: Male or Female.");

        //GIVEN
        us.setGender("Masculino");
        
        //WHEN
        new JsonHandler().createJsonUser(us);
    }
 

}
