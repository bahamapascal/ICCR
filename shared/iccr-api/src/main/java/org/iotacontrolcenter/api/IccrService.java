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
    @Path("/icc/languages")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIccLanguageChoices(@Context HttpServletRequest request);

    @GET
    @Path("/icc/languages/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIccLanguageProperties(@Context HttpServletRequest request,
                                      @DefaultValue("") @PathParam("key") String key);

    @GET
    @Path("/app/eventlog")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIccrEventLog(@Context HttpServletRequest request);

    @DELETE
    @Path("/app/eventlog")
    @Produces(MediaType.APPLICATION_JSON)
    Response deleteIccrEventLog(@Context HttpServletRequest request);

    @GET
    @Path("/app/config")
    @Produces(MediaType.APPLICATION_JSON)
    Response getConfigProperties(@Context HttpServletRequest request);

    @GET
    @Path("/app/config/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getConfigProperty(@Context HttpServletRequest request,
                                      @DefaultValue("") @PathParam("key") String key);

    @GET
    @Path("/app/config/iota/nbrs")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIotaNbrsConfig(@Context HttpServletRequest request);

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

    @PUT
    @Path("/app/config/iota/nbrs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateIotaNbrsConfig(@Context HttpServletRequest request, IccrIotaNeighborsPropertyDto nbrs);

    @POST
    @Path("/iccr/cmd/{action}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response doIccrAction(@Context HttpServletRequest request,
                          @DefaultValue("") @PathParam("action") String action, IccrPropertyListDto actionProps);

    @POST
    @Path("/iota/cmd/{action}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response doIotaAction(@Context HttpServletRequest request,
                       @DefaultValue("") @PathParam("action") String action, IccrPropertyListDto actionProps);

    @GET
    @Path("/iota/neighbors")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIotaNeighbors(@Context HttpServletRequest request);

    @GET
    @Path("/iota/nodeinfo")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIotaNodeInfo(@Context HttpServletRequest request);

    @GET
    @Path("/iota/log")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIotaLog(@Context HttpServletRequest request,
                        @QueryParam("fileDirection") String fileDirection,
                        @QueryParam("numLines") Long numLines,
                        @QueryParam("lastFileLength") Long lastFileLength,
                        @QueryParam("lastFilePosition") Long lastFilePosition);
}
