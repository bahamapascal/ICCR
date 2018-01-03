package org.iotacontrolcenter.iota.agent;

import org.iotacontrolcenter.iota.agent.process.IotaStatusProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.properties.locale.Localizer;

public class ProcessFactory {

    public static final String IOTASTATUS = "iotastatus";


    public static OsProcess getProcess(String cmd) {
        if(IOTASTATUS.equals(cmd)) {
            return new IotaStatusProcess();
        }
        throw new IllegalArgumentException(Localizer.getInstance().getFixedWithLocalText("ProcessFactory (" + cmd + "): ", "unsupportedProcess"));
    }

    public static boolean isValidProcess(String cmd) {
        return cmd != null &&
                !cmd.isEmpty() && (
                cmd.equals(IOTASTATUS) );
    }
}
