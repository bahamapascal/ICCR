package org.iotacontrolcenter.iota.agent.http;

public class GetIotaLibrary extends HttpGet {

    public GetIotaLibrary(String url) {
        super("downloadIota", url);
        addHeader("Content-Type", "application/java-archive");
    }
}
