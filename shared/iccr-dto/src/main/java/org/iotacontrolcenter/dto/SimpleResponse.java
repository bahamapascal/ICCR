package org.iotacontrolcenter.dto;

public class SimpleResponse {
    private boolean success;
    private String msg;

    public SimpleResponse() { this(true, ""); }

    public SimpleResponse(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SimpleResponse{" +
                "success=" + success +
                ", msg=" + msg +
                '}';
    }
}