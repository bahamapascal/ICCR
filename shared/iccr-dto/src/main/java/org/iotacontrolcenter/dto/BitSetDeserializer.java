package org.iotacontrolcenter.dto;

import java.io.IOException;
import java.util.BitSet;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class BitSetDeserializer extends StdDeserializer<BitSet> {

    private static final long serialVersionUID = 6401269449283859213L;

    public BitSetDeserializer() {
        this(null);
    }

    public BitSetDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BitSet deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        long[] input = ArrayUtils.toPrimitive( p.readValueAs(Long[].class) );
        BitSet output = BitSet.valueOf(input);
        return output;
    }


}
