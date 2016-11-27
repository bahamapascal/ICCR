package org.iotacontrolcenter.iota.agent.http;


public class GetIotaNeighbors extends HttpPost {

    public GetIotaNeighbors(String url) {
        super("getIotaNeighbors", url, "{\"command\": \"getNeighbors\"}");
        addHeader("Content-Type", "application/json");
    }
}


