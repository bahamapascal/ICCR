package org.iotacontrolcenter.rest.delegate;

import java.util.TimerTask;

import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.iota.agent.ActionFactory;
import org.iotacontrolcenter.iota.agent.Agent;

public class RefreshIotaActivityTimerTask extends TimerTask {

    private Agent agent = Agent.getInstance();
    private boolean isRunning = false;
    private Object syncObj = new Object();

    private Delegate delegate = Delegate.getInstance();

    @Override
    public void run() {
        if(isRunning()) {
            return;
        }
        setIsRunning(true);
        try {
            ActionResponse resp = runIotaAction(ActionFactory.NEIGHBORS);
            delegate.iotaActionDone(ActionFactory.NEIGHBORS, resp);
        }
        catch(Exception e) {
        }
        finally {
            setIsRunning(false);
        }
    }

    private boolean isRunning() {
        synchronized (syncObj) {
            return isRunning;
        }
    }

    private void setIsRunning(boolean isRunning) {
        synchronized (syncObj) {
            this.isRunning = isRunning;
        }
    }

    private ActionResponse runIotaAction(String action) {
        System.out.println("delegate running Iota action: " + action);
        ActionResponse resp = null;
        try {
            resp = agent.action(action, null);
        }
        catch(IllegalArgumentException iae) {
            System.out.println("delegate runIotaAction illegal arg error: " + iae.getMessage());
            iae.printStackTrace();
            resp.setSuccess(false);
            resp.setMsg(iae.getLocalizedMessage());
        }
        catch(IllegalStateException ise) {
            System.out.println("doIotaAction illegal state error: " + ise.getMessage());
            ise.printStackTrace();
            resp.setSuccess(false);
            resp.setMsg(ise.getLocalizedMessage());
        }
        catch(Exception e) {
            System.out.println("doIotaAction server error: " + e.getMessage());
            e.printStackTrace();
            resp.setSuccess(false);
            resp.setMsg(e.getLocalizedMessage());
        }
        return resp;
    }

}
