package org.iotacontrolcenter.iota.agent.process;

import java.io.File;

public class IotaStartProcess extends OsProcess {

    public IotaStartProcess() {
        super("iotastart");

        String runScript = "startiota";
        runScript += propSource.osIsWindows() ? ".bat" : ".bash";

        setArgs(new String[] { propSource.getIccrBinDir() + "/" + runScript});

        setDir(new File(propSource.getIccrBinDir()));
    }
}
