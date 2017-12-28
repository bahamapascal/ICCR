/**
 * 
 */
package org.iotacontrolcenter.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author David Landry <david@dmwl.net>
 *
 */
public class ActivityDtoDeserializerTest {
    ActivityDto expected;
    String        json;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        expected = new ActivityDto();
        expected.add(1L, 4L);
        expected.add(7);
        expected.add(127);

        json = "\"OjAAAAEAAAAAAAQAEAAAAAEAAgADAAcAfwA=\"";
    }

    /**
     * Test method for
     * {@link org.iotacontrolcenter.dto.ActivityDtoDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)}.
     */
    @Test
    public void testDeserialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ActivityDto.class,
                new ActivityDtoDeserializer());
        objectMapper.registerModule(module);
        ActivityDto actual = objectMapper.readValue(json, ActivityDto.class);
        assertEquals(expected, actual);
    }

}
