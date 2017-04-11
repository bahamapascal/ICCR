package org.iotacontrolcenter.iota.agent.http;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class HttpPost extends HttpMethod  {

    private Object payload;

    public HttpPost(String name, String url) {
        this(name, url, null, null);
    }

    public HttpPost(String name, String url, Object payload) {
        this(name, url, payload, null);
    }

    public HttpPost(String name, String url, Object payload, Map<String, String> headers) {
        super(name, url, headers);
        this.payload = payload;
        System.out.println("constructor: payload: " + payload);
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public void execute() {
        if(name == null || name.isEmpty()) {
            throw new IllegalStateException(localization.getLocalText("emptyHttpRequestName"));
        }

        if(url == null || url.isEmpty()) {
            throw new IllegalStateException(localization.getFixedWithLocalText(name + ": ", "emptyHttpRequestUrl"));
        }

        if(payload == null) {
            throw new IllegalStateException(localization.getLocalTextWithFixed("emptyHttpRequestPayload",
                    " (" + name + "): " + url));
        }

        System.out.println(localization.getLocalTextWithFixed("executingHttpRequest", " (" + name + "): " + url));

        org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(url);
        try {

            RequestConfig conf = RequestConfig.custom()
                    .setSocketTimeout(timeOutSec * 1000)
                    .setConnectTimeout(timeOutSec * 1000)
                    .setConnectionRequestTimeout(timeOutSec * 1000)
                    .build();

            post.setConfig(conf);

            if(headers != null && !headers.isEmpty()) {
                headers.forEach(post::setHeader);
            }

            StringEntity entity;
            if(payload instanceof  String) {
                entity = new StringEntity((String)payload, ContentType.create("application/json", "UTF-8"));
                post.setEntity(entity);

                System.out.println("Post string payload entity: '" + EntityUtils.toString(entity) + "'" +
                        ", len: " + entity.getContentLength());
            }
            else {
                Gson gson = new GsonBuilder().create();
                entity = new StringEntity(gson.toJson(payload), ContentType.create("application/json", "UTF-8"));
                post.setEntity(entity);

                System.out.println("Post object payload entity: '" + EntityUtils.toString(entity) + "'" +
                        ", len: " + entity.getContentLength());
            }
            CloseableHttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build();

            response = client.execute(post);

            // For releasing connection in calls from the base:
            httpRequestBase = post;
        }
        catch(IOException ioe) {
            startError = localization.getLocalTextWithFixed("httpRequestException",
                    " (name: " + name + ", URL: " + url + "): " + ioe.getLocalizedMessage());
            System.out.println(startError);
        }
    }
}
