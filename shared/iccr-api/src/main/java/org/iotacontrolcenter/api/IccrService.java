package org.iotacontrolcenter.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import org.iotacontrolcenter.dto.IccrIotaNeighborsPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyDto;
import org.iotacontrolcenter.dto.IccrPropertyListDto;

/**
 * Service for ICCR public ReST API
 **/
@Path("/iccr/rs")
//@Api(value="ICCR", description="ICCR ReST API")
public interface IccrService {

    @DELETE
    @Path("/app/eventlog")
    @Produces(MediaType.APPLICATION_JSON)
    Response deleteIccrEventLog(@Context HttpServletRequest request);

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
    @Path("/app/config")
    @Produces(MediaType.APPLICATION_JSON)
    Response getConfigProperties(@Context HttpServletRequest request);

    @GET
    @Path("/app/config/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getConfigProperty(@Context HttpServletRequest request,
            @DefaultValue("") @PathParam("key") String key);

    //@ApiOperation(value="Get Supported Languages")
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

    @GET
    @Path("/iota/log")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIotaLog(@Context HttpServletRequest request,
            @QueryParam("fileDirection") String fileDirection,
            @QueryParam("numLines") Long numLines,
            @QueryParam("lastFileLength") Long lastFileLength,
            @QueryParam("lastFilePosition") Long lastFilePosition);

    @GET
    @Path("/app/config/iota/nbrs")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIotaNbrsConfig(@Context HttpServletRequest request);

    @GET
    @Path("/iota/neighbors")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIotaNeighbors(@Context HttpServletRequest request, IccrPropertyListDto actionProps);

    @GET
    @Path("/iota/nodeinfo")
    @Produces(MediaType.APPLICATION_JSON)
    Response getIotaNodeInfo(@Context HttpServletRequest request);

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
}
