package org.iotacontrolcenter.persistence;

import org.apache.commons.io.FileUtils;
import org.iotacontrolcenter.dto.LogLinesResponse;
import org.iotacontrolcenter.properties.locale.Localizer;
import org.iotacontrolcenter.properties.source.PropertySource;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class PersistenceService {

    public static final String IOTA_DLD = "download";
    public static final String IOTA_DLD_FAIL = "downloadFail";
    public static final String IOTA_INSTALL = "install";
    public static final String IOTA_INSTALL_FAIL = "installFail";
    public static final String IOTA_STOP = "stop";
    public static final String IOTA_STOP_FAIL = "stopFail";
    public static final String IOTA_START = "start";
    public static final String IOTA_START_FAIL = "startFail";
    public static final String IOTA_RESTART = "restart";
    public static final String IOTA_RESTART_FAIL = "restartFail";
    public static final String IOTA_DELETE = "deleteIota";
    public static final String IOTA_DELETE_FAIL = "deleteIotaFail";
    public static final String IOTA_DELETE_DB = "deleteIotaDb";
    public static final String IOTA_DELETE_DB_FAIL = "deleteIotaDbFail";
    public static final String IOTA_ADD_NBRS_FAIL = "addIotaNeighbors";
    public static final String IOTA_ADD_NBRS = "addIotaNeighbors";
    public static final String IOTA_REMOVE_NBRS_FAIL = "removeIotaNeighborsFail";
    public static final String IOTA_REMOVE_NBRS = "removeIotaNeighbors";

    public static final String ICCR_RESTART = "restartIccr";
    public static final String ICCR_RESTART_FAIL = "restartIccrFail";

    private static PersistenceService instance;
    private static Object SYNC_INST = new Object();
    public static PersistenceService getInstance() {
        synchronized (SYNC_INST) {
            if(PersistenceService.instance == null) {
                PersistenceService.instance = new PersistenceService();
            }
            return PersistenceService.instance;
        }
    }

    private static final String ICCR_IOTA_EVENT_FILE = "iota-event.csv";
    private static final String ICCR_LOG_FILE = "iccr.log";
    private static final String IOTA_LOG_FILE = "console.log";
    private static final String HEAD_DIRECTIVE = "head";
    private static final String TAIL_DIRECTIVE = "tail";

    private Localizer localizer;
    private PropertySource propSource;
    private String iccrEventFilepath;
    private String iotaLogFilepath;
    private String iccrLogFilepath;

    private PersistenceService() {
        System.out.println("new PersistenceService");
        propSource = PropertySource.getInstance();
        localizer = Localizer.getInstance();
        iccrEventFilepath = propSource.getIccrDataDir() + "/" + ICCR_IOTA_EVENT_FILE;
        iotaLogFilepath = propSource.getIotaAppDir() + "/" + IOTA_LOG_FILE;
        iccrLogFilepath = propSource.getIccrLogDir() + "/" + ICCR_LOG_FILE;

        //iotaLogFilepath = iccrLogFilepath;
    }

    public LogLinesResponse getIotaLog(String fileDirection,
                                       Long lastFilePosition,
                                       Long lastFileLength,
                                       Long numLines) throws IOException {

        if(fileDirection == null || fileDirection.isEmpty()) {
            return getAllIotaLogLines();
        }
        else if(fileDirection.equalsIgnoreCase(HEAD_DIRECTIVE)) {
            return getIotaLogFromHead(lastFilePosition, lastFileLength, numLines);
        }
        else if(fileDirection.equalsIgnoreCase(TAIL_DIRECTIVE)) {
            return getIotaLogFromTail(lastFilePosition, lastFileLength, numLines);
        }
        else {
            System.out.println("Unrecognized file direction: " + fileDirection);
            LogLinesResponse resp = new LogLinesResponse(false, "Unsupported fileDirection parameter: '" + fileDirection + "'");
            return resp;
        }
    }

    private LogLinesResponse getIotaLogFromHead(Long lastFilePosition, Long lastFileLength, Long numLines) throws IOException {

        System.out.println("getIotaLogFromHead lastFilePosition: "  + lastFilePosition +
                ", lastFileLength: "  + lastFileLength +
                ", numLines: "  + numLines);

        if(numLines == null) {
            numLines = 500L;
        }

        LogLinesResponse resp = new LogLinesResponse();

        File f = new File(iotaLogFilepath);
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        long curFileLen = raf.length();

        System.out.println("getIotaLogFromHead curFileLen: " + curFileLen);

        /*
        if(lastFileLength != null && lastFileLength == curFileLen) {
            resp.setLastFilePosition(curFileLen);
            resp.setLastFileSize(curFileLen);
            raf.close();
            return resp;
        }
        */

        if(lastFilePosition != null && lastFilePosition > 0) {
            if(lastFilePosition <= curFileLen) {
                raf.seek(lastFilePosition);
            }
        }
        else {
            raf.seek(0L);
        }

        long lineNum = 0;
        String curLine = null;
        while(lineNum < numLines && (curLine = raf.readLine()) != null) {
            lineNum++;
            resp.addLine(curLine);
        }
        System.out.println("getIotaLogFromHead, read " + lineNum + " lines" +
                ", curFileLength: " + curFileLen +
                ", lastFilePosition: " + raf.getFilePointer());
        resp.setLastFilePosition(raf.getFilePointer());
        resp.setLastFileSize(curFileLen);

        raf.close();
        return resp;
    }

    private LogLinesResponse getIotaLogFromTail(Long lastFilePosition, Long lastFileLength, Long numLines) throws IOException {

        System.out.println("getIotaLogFromTail lastFilePosition: "  + lastFilePosition +
                ", lastFileLength: "  + lastFileLength +
                ", numLines: "  + numLines);

        if(numLines == null) {
            numLines = 500L;
        }

        LogLinesResponse resp = new LogLinesResponse();

        File f = new File(iotaLogFilepath);
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        long curFileLen = raf.length();

        System.out.println("getIotaLogFromTail curFileLen: " + curFileLen);

        /*
        if(lastFileLength != null && lastFileLength == curFileLen) {
            resp.setLastFilePosition(curFileLen);
            resp.setLastFileSize(curFileLen);
            raf.close();
            return resp;
        }
        */


        // On first query lastFilePosition will be null, but no easy way to determine the desired number
        // of lines backward from bottom of file:
        long firstSeekPosition = 0L;
        long firstSeekOffset = 0L;
        if(lastFilePosition == null) {
            firstSeekOffset = numLines * 132;
            firstSeekPosition = curFileLen - firstSeekOffset;
            if(firstSeekPosition < 0) {
                firstSeekPosition = 0L;
            }
            System.out.println("first seek offset: " + firstSeekOffset + ", first seek position: " + firstSeekPosition);
            raf.seek(firstSeekPosition);
        }
        else if(lastFilePosition != null && lastFilePosition > 0) {
            if(lastFilePosition <= curFileLen) {
                raf.seek(lastFilePosition);
            }
        }

        long lineNum = 0;
        String curLine = null;
        while(lineNum < numLines && (curLine = raf.readLine()) != null) {
            lineNum++;
            resp.addLine(curLine);
        }

        // On first time through, try to correct if we got it badly wrong:
        if(lastFilePosition ==  null) {

            System.out.println("getIotaLogFromTail, first tail query, read " + lineNum + " lines, " +
                    ", fileLength: " + curFileLen +
                    ", lastFilePosition: " + raf.getFilePointer());

            if(lineNum != numLines && firstSeekPosition > 0) {
                System.out.println("getIotaLogFromTail, first tail query, correcting seek offset");
                firstSeekOffset =  (firstSeekOffset / lineNum) * numLines;
                firstSeekPosition = curFileLen - firstSeekOffset;
                if(firstSeekPosition < 0) {
                    firstSeekPosition = 0L;
                }
                System.out.println("corrected first seek offset: " + firstSeekOffset + ", first seek position: " + firstSeekPosition);
                raf.seek(firstSeekPosition);
                lineNum = 0;
                curLine = null;
                resp.getLines().clear();
                while(lineNum < numLines && (curLine = raf.readLine()) != null) {
                    lineNum++;
                    resp.addLine(curLine);
                }
            }
        }

        System.out.println("getIotaLogFromTail, read " + lineNum + " lines" +
                ", fileLength: " + curFileLen +
                ", lastFilePosition: " + raf.getFilePointer());

        resp.setLastFilePosition(raf.getFilePointer());
        resp.setLastFileSize(curFileLen);

        raf.close();
        return resp;
    }

    private LogLinesResponse getAllIotaLogLines() {
        LogLinesResponse resp = new LogLinesResponse();

        File f = new File(iotaLogFilepath);
        //if(f.exists()) {
        try {
            resp.setLines(FileUtils.readLines(f));
            resp.setLastFilePosition(f.length());
            resp.setLastFileSize(f.length());
        } catch (Exception e) {
            resp.setSuccess(false);
            resp.setMsg(e.getLocalizedMessage());
        }

        return resp;
    }

    public List<String> getEventLog() throws IOException {
        File f = new File(iccrEventFilepath);

        //if(f.exists()) {
        try {
            return FileUtils.readLines(f);
        }
        catch(Exception e) {
            return new ArrayList<>();
        }
    }

    public void deleteEventLog() throws IOException {
        System.out.println("deleting Event log");
        FileUtils.deleteQuietly(new File(iccrEventFilepath));
    }

    public void logIotaAction(String event) {
        this.logIotaAction(event, "", "");
    }

    public void logIotaAction(String event, String data, String msg) {
        //System.out.println("logIotaAction : " + event);
        try {
            String line = localizer.getEventTime() + "," +  localizer.getLocalText(event) + "," + data;
            if(msg != null && !msg.isEmpty()) {
                line += "," + msg;
            }
            line += System.lineSeparator();

            //System.out.println(line);

            File f = new File(iccrEventFilepath);
            /*
            if(!f.exists()) {
                f.createNewFile();
            }
            */

            FileUtils.write(f, line, true);
        }
        catch(IOException ioe) {
            System.out.println("logIotaAction, exception writing to file (" +
                    iccrEventFilepath + "): " +ioe.getLocalizedMessage());
        }
    }

}