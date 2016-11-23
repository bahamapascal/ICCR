package org.iotacontrolcenter.dto;

public class ActionResponse {
    public boolean status;
    public String msg;
    public IccrPropertyListDto props;

    public ActionResponse(boolean status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
