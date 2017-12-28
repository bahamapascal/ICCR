/**
 * 
 */
package org.iotacontrolcenter.dto;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author davad
 *
 */
public class BitSetDeserializerTest {
    private BitSet             expected;
    private String             json;

    /**
    * @throws java.lang.Exception
    */
    @Before
    public void setUp() throws Exception {
        expected = new BitSet();
        expected.set(1, 4);
        expected.set(7);
        expected.set(127);

        json = "[142, -9223372036854775808]";

    }

    /**
     * Test method for {@link org.iotacontrolcenter.dto.BitSetDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)}.
     */
    @Test
    public void testDeserialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BitSet.class, new BitSetDeserializer());
        objectMapper.registerModule(module);
        BitSet actual = objectMapper.readValue(json, BitSet.class);
        assertEquals(expected, actual);
    }

}
