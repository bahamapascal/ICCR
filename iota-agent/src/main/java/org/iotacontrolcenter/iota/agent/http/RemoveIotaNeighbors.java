package org.iotacontrolcenter.iota.agent.http;

public class RemoveIotaNeighbors extends HttpPost {

    public RemoveIotaNeighbors(String url) {
        super("removeIotaNeighbors", url);
        addHeader("Content-Type", "application/json");
        addHeader("X-IOTA-API-Version", "1.4.1");
    }
}
