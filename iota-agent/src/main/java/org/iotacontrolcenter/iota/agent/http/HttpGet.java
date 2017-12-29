package org.iotacontrolcenter.iota.agent.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;

public class HttpGet extends HttpMethod {

    public HttpGet(String name) {
        this(name, null, null);
    }

    public HttpGet(String name, String url) {
        this(name, url, null);
    }

    public HttpGet(String name, String url, Map<String, String> headers) {
        super(name, url, headers);
    }

    @Override
    public void execute() {
        if(name == null || name.isEmpty()) {
            throw new IllegalStateException(localization.getLocalText("emptyHttpRequestName"));
        }

        if(url == null || url.isEmpty()) {
            throw new IllegalStateException(localization.getFixedWithLocalText(name + ": ", "emptyHttpRequestUrl"));
        }

        System.out.println(localization.getLocalTextWithFixed("executingHttpRequest", " (" + name + "): " + url));

        org.apache.http.client.methods.HttpGet get = new org.apache.http.client.methods.HttpGet(url);
        try {

            RequestConfig getConfig = RequestConfig.custom()
                    .setSocketTimeout(timeOutSec * 1000)
                    .setConnectTimeout(timeOutSec * 1000)
                    .setConnectionRequestTimeout(timeOutSec * 1000)
                    .build();

            get.setConfig(getConfig);

            if(headers != null && !headers.isEmpty()) {
                headers.forEach(get::setHeader);
            }

            CloseableHttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build();

            response = client.execute(get);

            // For releasing connection in calls from the base:
            httpRequestBase = get;
        }
        catch(IOException ioe) {
            System.out.println("http get io exe:");
            ioe.printStackTrace();

            System.out.println("startError:");
            startError = localization.getLocalTextWithFixed("httpRequestException",
                    " (name: " + name + ", URL: " + url + "): " + ioe.getLocalizedMessage());
            System.out.println(startError);
        }
    }

}
