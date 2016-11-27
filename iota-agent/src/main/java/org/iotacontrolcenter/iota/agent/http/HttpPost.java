package org.iotacontrolcenter.iota.agent.http;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.config.RequestConfig;
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
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public void execute() {
        if(name == null || name.isEmpty()) {
            throw new IllegalStateException(localizer.getLocalText("emptyHttpRequestName"));
        }

        if(url == null || url.isEmpty()) {
            throw new IllegalStateException(localizer.getFixedWithLocalText(name + ": ", "emptyHttpRequestUrl"));
        }

        if(payload == null) {
            throw new IllegalStateException(localizer.getLocalTextWithFixed("emptyHttpRequestPayload",
                    " (" + name + "): " + url));
        }

        System.out.println(localizer.getLocalTextWithFixed("executingHttpRequest", " (" + name + "): " + url));

        org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(url);
        try {

            RequestConfig conf = RequestConfig.custom()
                    .setSocketTimeout(timeOutSec * 1000)
                    .setConnectTimeout(timeOutSec * 1000)
                    .setConnectionRequestTimeout(timeOutSec * 1000)
                    .build();

            post.setConfig(conf);

            if(headers != null && !headers.isEmpty()) {
                headers.forEach((k,v) -> {
                    post.setHeader(k, v);
                });
            }
            Gson gson = new GsonBuilder().create();
            post.setEntity(new StringEntity(gson.toJson(payload), "UTF-8"));

            CloseableHttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build();

            response = client.execute(post);

            // For releasing connection in calls from the base:
            httpRequestBase = post;
        }
        catch(IOException ioe) {
            startError = localizer.getLocalTextWithFixed("httpRequestException",
                    " (name: " + name + ", URL: " + url + "): " + ioe.getLocalizedMessage());
            System.out.println(startError);
        }
    }
}
