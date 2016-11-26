package org.iotacontrolcenter.iota.agent.process;

import java.io.File;

public class IotaStartProcess extends OsProcess {

    private String runScript = "startiota";

    public IotaStartProcess() {
        super("iotastart");

        runScript += propSource.osIsWindows() ? ".bat" : ".bash";

        setArgs(new String[] { propSource.getIccrBinDir() + "/" + runScript });

        setDir(new File(propSource.getIccrBinDir()));
    }
}
