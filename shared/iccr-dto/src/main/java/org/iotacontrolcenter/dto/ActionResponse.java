package org.iotacontrolcenter.dto;

public class ActionResponse {
    public boolean success;
    public String msg;
    public IccrPropertyListDto props;

    public ActionResponse(boolean success, String msg) {
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

    public IccrPropertyListDto getProps() {
        return props;
    }

    public void setProps(IccrPropertyListDto props) {
        this.props = props;
    }

    public void addProperty(IccrPropertyDto prop) {
        if(props == null) {
            props = new IccrPropertyListDto();
        }
        props.addProperty(prop);
    }
}
