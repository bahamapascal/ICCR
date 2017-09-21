package org.iotacontrolcenter.dto;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ActivityDtoSerializer extends StdSerializer<ActivityDto> {


    public ActivityDtoSerializer() {
        this(null);
    }

    public ActivityDtoSerializer(Class<ActivityDto> t) {
        super(t);
    }

    @Override
    public void serialize(ActivityDto activity,
            JsonGenerator gen,
            SerializerProvider arg2)
                    throws IOException, JsonProcessingException {



        activity.runOptimize();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        activity.serialize(new DataOutputStream(baos));

        String serializedstring = Base64.getEncoder()
                .encodeToString(baos.toByteArray());

        gen.writeString(serializedstring);

    }


}
