package org.iotacontrolcenter.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.iotacontrolcenter.dto.*;

/**
 * Service for ICCR public ReST API
 **/
@Path("/iccr/rs")
public interface IccrService {

    @GET
    @Path("/app/config")
    @Produces(MediaType.APPLICATION_JSON)
    Response getConfigProperties(@Context HttpServletRequest request);

    @GET
    @Path("/app/config/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getConfigProperty(@Context HttpServletRequest request,
                                      @DefaultValue("") @PathParam("key") String key);

    @PUT
    @Path("/app/config")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateConfigProperties(@Context HttpServletRequest request,
                                    IccrPropertyListDto properties);

    @PUT
    @Path("/app/config/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateConfigProperty(@Context HttpServletRequest request,
                                  @DefaultValue("") @PathParam("key") String key, IccrPropertyDto prop);

    @POST
    @Path("/iota/cmd/{action}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response doIotaAction(@Context HttpServletRequest request,
                       @DefaultValue("") @PathParam("action") String action);
}