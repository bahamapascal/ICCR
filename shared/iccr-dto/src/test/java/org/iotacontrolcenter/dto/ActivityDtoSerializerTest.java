/**
 * 
 */
package org.iotacontrolcenter.dto;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * @author David Landry <david@dmwl.net>
 *
 */
public class ActivityDtoSerializerTest {

    ActivityDto           testMe;
    ActivityDtoSerializer serializer;

    /**
     */
    @Before
    public void setUp() {
        testMe = new ActivityDto();
        testMe.add(1L, 4L);
        testMe.add(7);
        testMe.add(127);
        
        serializer = new ActivityDtoSerializer();
    }

    /**
     * Test method for
     * {@link org.iotacontrolcenter.dto.ActivityDtoSerializer#serialize(java.util.BitSet, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
     * 
     * @throws IOException
     */
    @Test
    public void testSerializeActivityDtoJsonGeneratorSerializerProvider()
            throws IOException {
        StringWriter jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory()
                .createGenerator(jsonWriter);

        serializer.serialize(testMe, jsonGenerator, null);
        jsonGenerator.flush();

        assertEquals(jsonWriter.toString(),
                "\"OjAAAAEAAAAAAAQAEAAAAAEAAgADAAcAfwA=\"");
    }

}