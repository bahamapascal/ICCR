package org.iotacontrolcenter.iota.agent.process;


import org.iotacontrolcenter.properties.locale.Localization;
import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

public abstract class OsProcess {

    protected String[] args;
    protected File dir;
    protected InputStream errorStream;
    protected Map<String,String> env;
    protected String exeCmd;
    protected final Localization localization;
    protected final String name;
    protected InputStream outputStream;
    protected ProcessBuilder pb;
    protected Process p;
    protected final PropertySource propSource;
    protected int resultCode;
    protected String startError;

    protected OsProcess(String name) {
        this.name = name;
        localization = Localization.getInstance();
        propSource = PropertySource.getInstance();
    }

    public String getCmd() { return getName(); }

    public String getName() {
        return name;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public String getProcessActionName() {
        return getClass().getSimpleName() + " " + name;
    }

    public boolean start() {
        if(exeCmd == null || exeCmd.isEmpty()) {
            setup();
        }

        startError = null;
        boolean success = true;
        System.out.println(localization.getLocalTextWithFixed("executingCmd", " (" + getName() + "): " + exeCmd));
        try {
            p = pb.start();
            errorStream = p.getErrorStream();
            outputStream = p.getInputStream();
        }
        catch(IOException ioe) {
            startError = localization.getLocalTextWithFixed("startActionException",
                    " (name: " + getName() + ", cmd: " + exeCmd + "): " + ioe.getLocalizedMessage());
            System.out.println(startError);
            success = false;
        }
        if(success) {
            try {
                resultCode = p.waitFor();
            } catch (InterruptedException ie) {
                System.out.println(localization.getLocalTextWithFixed("actionException",
                        " (name: " + getName() + ", cmd: " + exeCmd + "): " + ie.getLocalizedMessage()));
                success = false;
            }
        }
        return success;
    }

    public boolean isStartError() {
        return startError != null && !startError.isEmpty();
    }

    public String getStartError() {
        return startError;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getStdErr() {
        return loadStream(errorStream);
    }

    public String getStdOut() {
        return loadStream(outputStream);
    }

    private String loadStream(InputStream s)  {
        BufferedReader br = new BufferedReader(new InputStreamReader(s));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        catch(IOException ioe) {
            System.out.println(localization.getLocalTextWithFixed("actionOutputException",
                    " (name: " + getName() + ", cmd: " + exeCmd + "): " + ioe.getLocalizedMessage()));
        }
        return sb.toString();
    }

    protected void generateExeCmd() {
        exeCmd = "";
        if(args != null && args.length > 0) {
            Arrays.stream(args).forEach((s) -> exeCmd += " " + s);
        }
    }

    protected void setup() {
        validateCommand();

        generateExeCmd();

        pb = new ProcessBuilder();
        if(env != null && !env.isEmpty()) {
            env.forEach((k,v) -> pb.environment().put(k,v));
        }
        if(dir != null) {
            pb.directory(dir);
        }

        pb.command(args);
    }

    protected void validateCommand() {
        if(args == null || args.length == 0) {
            throw new IllegalStateException(localization.getFixedWithLocalText(getProcessActionName() + ": ", "emptyCmd"));
        }
    }

}
