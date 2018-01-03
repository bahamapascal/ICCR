package org.iotacontrolcenter.iota.agent.process;

import java.io.File;

public class IotaBakupAndInstallProcess extends OsProcess {

    private String runScript = "installiri";

    public IotaBakupAndInstallProcess(String dldFilePath, String iriFile) {
        super("installiri");

        runScript += propSource.osIsWindows() ? ".bat" : ".bash";

        setArgs(new String[] { propSource.getIccrBinDir() + "/" + runScript, dldFilePath, iriFile });

        setDir(new File(propSource.getIccrBinDir()));
    }
}
