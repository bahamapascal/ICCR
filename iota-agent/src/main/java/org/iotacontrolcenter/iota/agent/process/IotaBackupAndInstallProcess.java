package org.iotacontrolcenter.iota.agent.process;

import java.io.File;

public class IotaBackupAndInstallProcess extends OsProcess {

    public IotaBackupAndInstallProcess(String dldFilePath, String iriFile) {
        super("installiri");

        String runScript = "installiri";
        runScript += propSource.osIsWindows() ? ".bat" : ".bash";

        setArgs(new String[] { propSource.getIccrBinDir() + "/" + runScript, dldFilePath, iriFile });

        setDir(new File(propSource.getIccrBinDir()));
    }
}
