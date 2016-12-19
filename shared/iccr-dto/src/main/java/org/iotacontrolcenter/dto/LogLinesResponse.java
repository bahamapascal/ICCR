package org.iotacontrolcenter.dto;

import java.util.ArrayList;
import java.util.List;

public class LogLinesResponse extends SimpleResponse {

    private List<String> lines = new ArrayList<>();
    private Long lastFilePosition;
    private Long lastFileSize;

    public LogLinesResponse() {
        super();
    }

    public LogLinesResponse(boolean success, String msg) {
        super(success, msg);
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public Long getLastFilePosition() {
        return lastFilePosition;
    }

    public void setLastFilePosition(Long lastFilePosition) {
        this.lastFilePosition = lastFilePosition;
    }

    public Long getLastFileSize() {
        return lastFileSize;
    }

    public void setLastFileSize(Long lastFileSize) {
        this.lastFileSize = lastFileSize;
    }
}
