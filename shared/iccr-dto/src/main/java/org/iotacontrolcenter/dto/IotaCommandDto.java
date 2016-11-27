package org.iotacontrolcenter.dto;

public class IotaCommandDto {
    private String command;

    public IotaCommandDto(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
