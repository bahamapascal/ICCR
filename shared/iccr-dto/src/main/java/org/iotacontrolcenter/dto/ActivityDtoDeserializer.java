package org.iotacontrolcenter.dto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;

import org.roaringbitmap.buffer.ImmutableRoaringBitmap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ActivityDtoDeserializer extends StdDeserializer<ActivityDto> {

    private static final long serialVersionUID = 2298774887253844235L;

    public ActivityDtoDeserializer() {
        this(null);
    }

    public ActivityDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ActivityDto deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        ByteBuffer newbb = ByteBuffer
                .wrap(Base64.getDecoder().decode(p.readValueAs(String.class)));
        ImmutableRoaringBitmap imut = new ImmutableRoaringBitmap(newbb);

        return new ActivityDto(imut);
    }


}
