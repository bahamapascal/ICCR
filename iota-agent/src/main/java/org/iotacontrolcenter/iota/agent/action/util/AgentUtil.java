package org.iotacontrolcenter.iota.agent.action.util;

import java.io.File;

public class AgentUtil {

    public static boolean dirExists(String dirPath) {
        boolean rval = false;
        try {
            File f = new File(dirPath);
            rval = f.exists() && f.isDirectory();
        }
        catch(Exception e) {
            System.out.println("dirExists(" + dirPath + ") exception: " + e.getLocalizedMessage());
        }
        return rval;
    }

    public static boolean fileExists(String filePath) {
        boolean rval = false;
        try {
            File f = new File(filePath);
            rval = f.exists();
        }
        catch(Exception e) {
            System.out.println("fileExists(" + filePath + ") exception: " + e.getLocalizedMessage());
        }
        return rval;
    }
}
