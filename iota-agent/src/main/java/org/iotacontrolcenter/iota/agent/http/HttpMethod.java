package org.iotacontrolcenter.iota.agent.http;


import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.iotacontrolcenter.properties.locale.Localization;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpMethod {

    protected Map<String, String> headers;
    protected HttpRequestBase httpRequestBase;
    protected final Localization localization;
    protected final String name;
    public CloseableHttpResponse response;
    protected String startError;
    protected int timeOutSec = 30;
    protected String url;

    public HttpMethod(String name) {
        this(name, null, null);
    }

    public HttpMethod(String name, String url) {
        this(name, url, null);
    }

    public HttpMethod(String name, String url, Map<String, String> headers) {
        this.name = name;
        this.url = url;
        this.headers = headers;
        localization = Localization.getInstance();
    }

    public String getName() {
        return name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        if(headers ==  null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
    }

    public void setTimeOutSec(int timeOutSec) {
        this.timeOutSec = timeOutSec;
    }

    public boolean isStartError() {
        return startError != null && !startError.isEmpty();
    }

    public String getStartError() {
        return startError;
    }

    public boolean isResponseContent() {
        return response != null;
    }

    public boolean isResponseSuccess() {
        return isResponseContent() && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    public String getResponseReason() {
        return isResponseContent() ? response.getStatusLine().getReasonPhrase() : "";
    }

    public InputStream getResponseContent() {
        if(response == null) {
            throw new IllegalStateException(localization.getLocalTextWithFixed("emptyHttpResponse",
                    " (name: " + name + ", URL: " + url));
        }
        try {
            return response.getEntity().getContent();
        }
        catch(IOException ioe) {
            throw new IllegalStateException(localization.getLocalTextWithFixed("httpResponseException",
                    " (name: " + name + ", URL: " + url));
        }
    }

    public byte[] responseAsByteArray() {
        try {
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            if(httpRequestBase != null) {
                httpRequestBase.releaseConnection();
                httpRequestBase = null;
            }
            return bytes;
        }
        catch(IOException ioe) {
            System.out.println("to byte array ioe:");
            ioe.printStackTrace();
            throw new IllegalStateException(localization.getLocalTextWithFixed("httpResponseException",
                    " (name: " + name + ", URL: " + url + "): " + ioe.getLocalizedMessage()));
        }
    }

    public String responseAsString() {
        try {
            String resp = EntityUtils.toString(response.getEntity());

            if(httpRequestBase != null) {
                httpRequestBase.releaseConnection();
                httpRequestBase = null;
            }

            return resp;
        }
        catch(IOException ioe) {
            System.out.println("to string ioe:");
            ioe.printStackTrace();

            throw new IllegalStateException(localization.getLocalTextWithFixed("httpResponseException",
                    " (name: " + name + ", URL: " + url + "): " + ioe.getLocalizedMessage()));
        }
    }

    public abstract void execute();

}


