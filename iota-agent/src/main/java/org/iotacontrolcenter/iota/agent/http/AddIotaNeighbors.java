package org.iotacontrolcenter.iota.agent.http;

public class AddIotaNeighbors extends HttpPost {

    public AddIotaNeighbors(String url) {
        super("addIotaNeighbors", url);
        addHeader("Content-Type", "application/json");
    }
}
