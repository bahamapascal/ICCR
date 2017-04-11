package org.iotacontrolcenter.iota.agent;

import org.iotacontrolcenter.iota.agent.process.IotaStatusProcess;
import org.iotacontrolcenter.iota.agent.process.OsProcess;
import org.iotacontrolcenter.properties.locale.Localization;

public class ProcessFactory {

    public static final String IOTA_STATUS = "iotastatus";


    public static OsProcess getProcess(String cmd) {
        if(IOTA_STATUS.equals(cmd)) {
            return new IotaStatusProcess();
        }
        throw new IllegalArgumentException(Localization.getInstance().getFixedWithLocalText("ProcessFactory (" + cmd + "): ", "unsupportedProcess"));
    }

    public static boolean isValidProcess(String cmd) {
        return cmd != null &&
                !cmd.isEmpty() && (
                cmd.equals(IOTA_STATUS) );
    }
}
