package org.iotacontrolcenter.iota.agent.process;

import java.io.File;

public class IccrRestartProcess extends OsProcess {

    private String runScript = "restarticcr";

    public IccrRestartProcess() {
        super("iccrrestart");

        runScript += propSource.osIsWindows() ? ".bat" : ".bash";

        setArgs(new String[] { propSource.getIccrBinDir() + "/" + runScript });

        setDir(new File(propSource.getIccrBinDir()));
    }

    @Override
    public String getCmd() {
        return args[0];
    }
}
