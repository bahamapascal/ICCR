package org.iotacontrolcenter.iota.agent.process;

public class IotaStatusProcess extends OsProcess {

    public IotaStatusProcess() {
        super("iotastatus", new String [] {"ps fax | grep IRI | grep -v grep"}, null, null);
    }
}
