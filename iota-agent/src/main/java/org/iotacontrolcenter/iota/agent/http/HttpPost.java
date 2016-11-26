package org.iotacontrolcenter.iota.agent.http;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class HttpPost extends HttpMethod  {

    private String payload;

    public HttpPost(String name) {
        this(name, null, null, null);
    }

    public HttpPost(String name, String url, String payload) {
        this(name, url, payload, null);
    }

    public HttpPost(String name, String url, String payload, Map<String, String> headers) {
        super(name, url, headers);
        this.payload = payload;
    }

    public void setPayload(String payload) {
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

        if(payload == null || payload.isEmpty()) {
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

            CloseableHttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build();

            System.out.println("executing post");
            response = client.execute(post);

            System.out.println("consuming entity");

            // This consumes the returned content and closes the stream;
            EntityUtils.consume(response.getEntity());
        }
        catch(IOException ioe) {
            startError = localizer.getLocalTextWithFixed("httpRequestException",
                    " (name: " + name + ", URL: " + url + "): " + ioe.getLocalizedMessage());
            System.out.println(startError);
        }
    }
}
