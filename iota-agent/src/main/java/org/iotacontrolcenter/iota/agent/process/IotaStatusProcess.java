package org.iotacontrolcenter.iota.agent.process;

import java.io.File;

public class IotaStatusProcess extends OsProcess {

    private String checkScript = "checkiotastatus";

    public IotaStatusProcess() {
        super("iotastatus");

        checkScript += propSource.osIsWindows() ? ".bat" : ".bash";

        setArgs(new String[] { propSource.getIccrBinDir() + "/" + checkScript });

        setDir(new File(propSource.getIccrBinDir()));
    }
}
