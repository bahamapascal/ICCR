package org.iotacontrolcenter.rest.resource;

import org.iotacontrolcenter.api.*;
import org.iotacontrolcenter.dto.*;
import org.iotacontrolcenter.properties.source.PropertySource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class IccrServiceImpl implements IccrService {

    private PropertySource props = PropertySource.getInstance();

    public IccrServiceImpl() {
        System.out.println("creating new IccrServiceImpl");
    }

    @Override
    public Response getConfigProperties(HttpServletRequest request) {
        if(!authorizedRequest(request)) {
            return unauthorizedResponse(request);
        }
        Response.ResponseBuilder r = Response.status(HttpURLConnection.HTTP_OK);

        IccrPropertyListDto propList = new IccrPropertyListDto();

        for(String key : props.getPropertyKeys()) {
            propList.addProperty(new IccrPropertyDto(key, props.getString(key)));
        }
        propList.addProperty(getIotaNeighborsProperty());
        r.entity(propList);
        return r.build();
    }

    @Override
    public Response getConfigProperty(HttpServletRequest request, String key) {
        if(!authorizedRequest(request)) {
            return unauthorizedResponse(request);
        }
        Response.ResponseBuilder r;

        if(key == null || key.isEmpty()) {
            r = Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(new SimpleResponse(false, "Invalid request: empty property key"));
            return r.build();
        }

        r = Response.status(HttpURLConnection.HTTP_OK);
        IccrPropertyDto prop;

        if(key.equals(PropertySource.IOTA_NEIGHBORS_PROP)) {
            prop = getIotaNeighborsProperty();
        }
        else {
            prop = new IccrPropertyDto(key, props.getString(key));
        }

        r.entity(prop);
        return r.build();
    }

    @Override
    public Response updateConfigProperties(HttpServletRequest request, IccrPropertyListDto properties) {
        if(!authorizedRequest(request)) {
            return unauthorizedResponse(request);
        }
        Response.ResponseBuilder r;

        if(properties == null) {
            r = Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(new SimpleResponse(false, "Invalid request: empty properties object"));
            return r.build();
        }

        try {
            for(IccrPropertyDto prop : properties.getProperties()) {
                props.setProperty(prop.getKey(), prop.getValue());
            }
            r = Response.status(HttpURLConnection.HTTP_OK);
            r.entity(new SimpleResponse(true, "properties updated successfully"));
        }
        catch(Exception e) {
            r = Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity(new SimpleResponse(false, "Server error: " + e.getLocalizedMessage()));
        }

        return r.build();
    }

    @Override
    public Response updateConfigProperty(HttpServletRequest request, String key, IccrPropertyDto prop) {
        if(!authorizedRequest(request)) {
            return unauthorizedResponse(request);
        }
        Response.ResponseBuilder r;

        if(prop == null) {
            r = Response.status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity(new SimpleResponse(false, "Invalid request: empty property object"));
            return r.build();
        }

        try {
            props.setProperty(prop.getKey(), prop.getValue());
            r = Response.status(HttpURLConnection.HTTP_OK);
            r.entity(new SimpleResponse(true, "property updated successfully"));
        }
        catch(Exception e) {
            r = Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity(new SimpleResponse(false, "Server error: " + e.getLocalizedMessage()));
        }

        return r.build();
    }

    @Override
    public Response doIotaCmd(HttpServletRequest request, String action) {
        if (!authorizedRequest(request)) {
            return unauthorizedResponse(request);
        }
        Response.ResponseBuilder r;

        r = Response.status(HttpURLConnection.HTTP_OK);
        r.entity(new SimpleResponse(true, "property updated successfully"));

        return r.build();
    }

    private IccrPropertyDto getIotaNeighborsProperty() {
        List<NeighborDto> nbrs = new ArrayList<>();
        for(String id : props.getNeighborKeys()) {
            try {
                nbrs.add(new NeighborDto(
                        props.getString(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".key." + id),
                        props.getString(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".ip." + id),
                        props.getString(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".name." + id),
                        props.getString(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".descr." + id),
                        props.getBoolean(PropertySource.IOTA_NEIGHBOR_PROP_PREFIX + ".active." + id)));
            }
            catch(Exception e) {
                System.out.println("getIotaNeighborsProperty exception: " + e.getLocalizedMessage());
            }
        }
        return new IccrPropertyDto(PropertySource.IOTA_NEIGHBORS_PROP, nbrs);
    }

    private boolean authorizedRequest(HttpServletRequest request) {
        String apiAccessKey = request.getHeader(ResourceUtil.API_ACCESS_KEY_PROP);
        // TODO
        return true; //apiAccessKey != null && !apiAccessKey.isEmpty() && apiAccessKey.equals();
    }

    private Response unauthorizedResponse(HttpServletRequest request) {
        return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).
                entity(new SimpleResponse(false, "Not authorized to use " + request.getRequestURL())).
                build();
    }
}