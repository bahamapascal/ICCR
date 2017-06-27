/**
 * 
 */
package org.iotacontrolcenter.dto;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * @author David Landry <david@dmwl.net>
 *
 */
public class BitSetSerializerTest {

    BitSet testMe;
    BitSetSerializer bitSetSerializer;

    /**
     */
    @Before
    public void setUp() {
        testMe = new BitSet();
        testMe.set(1,4);
        testMe.set(7);
        testMe.set(127);
        
        bitSetSerializer = new BitSetSerializer();
    }

    /**
     * Test method for
     * {@link org.iotacontrolcenter.dto.BitSetSerializer#serialize(java.util.BitSet, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
     * 
     * @throws IOException
     */
    @Test
    public void testSerializeBitSetJsonGeneratorSerializerProvider()
            throws IOException {
        StringWriter jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory()
                .createGenerator(jsonWriter);

        bitSetSerializer.serialize(testMe, jsonGenerator, null);
        jsonGenerator.flush();

        assertEquals(jsonWriter.toString(), "[142, -9223372036854775808]");
    }

}
