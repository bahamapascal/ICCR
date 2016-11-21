package org.iotacontrolcenter.api;

import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.iotacontrolcenter.dto.*;

/**
 * Service for ICCR public API
 **/
@Path("/rs")
public interface IccrService {

    @GET
    @Path("/app/config")
    @Produces(MediaType.APPLICATION_JSON)
    List<IccrPropertyDto> getConfigPropertyList();

    @GET
    @Path("/app/config/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    IccrPropertyDto getConfigProperty(@DefaultValue("") @PathParam("id") String id);
}