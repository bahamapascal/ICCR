package org.iotacontrolcenter.iota.agent.process;

import java.io.File;

public class IotaStopProcess extends OsProcess {

    private String runScript = "stopiota";

    public IotaStopProcess() {
        super("iotastop");

        runScript += propSource.osIsWindows() ? ".bat" : ".bash";

        setArgs(new String[] { propSource.getIccrBinDir() + "/" + runScript });

        setDir(new File(propSource.getIccrBinDir()));
    }
}
