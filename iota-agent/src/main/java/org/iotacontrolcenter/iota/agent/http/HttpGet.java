package org.iotacontrolcenter.iota.agent.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

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
            throw new IllegalStateException(localizer.getLocalText("emptyHttpRequestName"));
        }

        if(url == null || url.isEmpty()) {
            throw new IllegalStateException(localizer.getFixedWithLocalText(name + ": ", "emptyHttpRequestUrl"));
        }

        System.out.println(localizer.getLocalTextWithFixed("executingHttpRequest", " (" + name + "): " + url));

        org.apache.http.client.methods.HttpGet get = new org.apache.http.client.methods.HttpGet(url);
        try {

            RequestConfig getConfig = RequestConfig.custom()
                    .setSocketTimeout(timeOutSec * 1000)
                    .setConnectTimeout(timeOutSec * 1000)
                    .setConnectionRequestTimeout(timeOutSec * 1000)
                    .build();

            get.setConfig(getConfig);

            if(headers != null && !headers.isEmpty()) {
                headers.forEach((k,v) -> {
                    get.setHeader(k, v);
                });
            }

            CloseableHttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build();

            response = client.execute(get);

            /*
            System.out.println("consuming entity");

            // This consumes the returned content and closes the stream;
            EntityUtils.consume(response.getEntity());

            System.out.println("done consuming entity");
            */
        }
        catch(IOException ioe) {
            System.out.println("httpget io exe:");
            ioe.printStackTrace();

            System.out.println("startError:");
            startError = localizer.getLocalTextWithFixed("httpRequestException",
                    " (name: " + name + ", URL: " + url + "): " + ioe.getLocalizedMessage());
            System.out.println(startError);
        }
        /*
        finally {
            if(get != null) {
                get.releaseConnection();
            }
        }
        */
    }

}
