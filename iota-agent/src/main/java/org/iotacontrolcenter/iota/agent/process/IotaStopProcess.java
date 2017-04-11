package org.iotacontrolcenter.iota.agent.process;

import java.io.File;

public class IotaStopProcess extends OsProcess {

    public IotaStopProcess() {
        super("iotastop");

        String runScript = "stopiota";
        runScript += propSource.osIsWindows() ? ".bat" : ".bash";

        setArgs(new String[] { propSource.getIccrBinDir() + "/" + runScript});

        setDir(new File(propSource.getIccrBinDir()));
    }
}
