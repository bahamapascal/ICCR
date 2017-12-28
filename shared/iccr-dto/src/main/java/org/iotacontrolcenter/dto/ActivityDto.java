/**
 * 
 */
package org.iotacontrolcenter.dto;

import java.io.IOException;
import java.io.StringWriter;

import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author David Landry <david@dmwl.net>
 *
 */
public class ActivityDto extends RoaringBitmap {

    /**
     * 
     */
    public ActivityDto() {
        super();
    }

    /**
     * @param rb
     */
    public ActivityDto(ImmutableRoaringBitmap rb) {
        super(rb);
    }

    public String serialize() throws IOException {

        StringWriter jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory()
                .createGenerator(jsonWriter);
        (new ActivityDtoSerializer()).serialize(this, jsonGenerator, null);
        jsonGenerator.flush();

        return jsonWriter.toString();
    }

    public static ActivityDto deserialize(String unparsed)
            throws IOException {

        ActivityDto output = new ActivityDto();

        if (unparsed.isEmpty()) {
            return output;
        }

        try {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ActivityDto.class,
                new ActivityDtoDeserializer());
        objectMapper.registerModule(module);

        output = objectMapper.readValue(unparsed, ActivityDto.class);
        }
        catch (JsonParseException | JsonMappingException e) {
            // Return empty ActivityDto and log error
            System.out.println(
                    "PropertySource JsonParseException or JsonMappingException loading ActivityDto: "
                            + e.getLocalizedMessage());
        }
        return output;

    }

}
