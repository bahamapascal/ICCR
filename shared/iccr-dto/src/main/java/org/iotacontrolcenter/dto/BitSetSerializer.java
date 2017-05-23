package org.iotacontrolcenter.dto;

import java.io.IOException;
import java.util.BitSet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class BitSetSerializer extends StdSerializer<BitSet> {

    private static final long serialVersionUID = 6401269449283859213L;

    public BitSetSerializer() {
        this(null);
    }

    public BitSetSerializer(Class<BitSet> t) {
        super(t);
    }

    @Override
    public void serialize(BitSet bitset,
            JsonGenerator gen,
            SerializerProvider arg2)
                    throws IOException, JsonProcessingException {

        long[] value = bitset.toLongArray();
        gen.writeArray(value, 0, value.length);

    }


}
