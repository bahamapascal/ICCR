package org.iotacontrolcenter.rest.resource;

import org.iotacontrolcenter.dto.SimpleResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ext.ExceptionMapper;


public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException e) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new SimpleResponse(false, e.getLocalizedMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
