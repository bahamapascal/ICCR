package org.iotacontrolcenter.rest.delegate;


import org.iotacontrolcenter.iccr.agent.IccrActionFactory;
import org.iotacontrolcenter.iota.agent.ActionFactory;
import org.iotacontrolcenter.properties.source.PropertySource;

public class Delegate {



    private static Delegate instance;
    private static Object SYNC_INST = new Object();
    public static Delegate getInstance() {
        synchronized (SYNC_INST) {
            if(Delegate.instance == null) {
                Delegate.instance = new Delegate();
            }
            return Delegate.instance;
        }
    }

    private java.util.Timer iotaNeighborRefreshTimer;
    private Integer refreshTimeMin = null;
    private PropertySource propertySource = PropertySource.getInstance();

    private Delegate() {
        System.out.println("new Delegate");
    }

    public synchronized  void iotaActionDone(String action) {
        if(action.equals(ActionFactory.STOP)) {
            stopNeighborRefresh();
        }
        else if(action.equals(ActionFactory.START)) {
            startNeighborRefresh();
        }
    }

    public synchronized  void iccrActionDone(String action) {
        if(action.equals(IccrActionFactory.RESTART)) {
            //startNeighborRefresh();
        }
        /*
        else if(action.equals(IccrActionFactory.STOP)) {
            //stopNeighborRefresh();
        }
        */
    }

    public synchronized  void iccrPropSet(String prop) {
        if(prop.equals(PropertySource.IOTA_NEIGHBORS_PROP)) {
            stopNeighborRefresh();
            startNeighborRefresh();
        }
        else if(prop.equals(PropertySource.IOTA_NBR_REFRESH_TIME_PROP)) {
            Integer prevRefreshTimeMin = refreshTimeMin;
            if(propertySource.getIotaNeighborRefreshTime() == prevRefreshTimeMin) {
                return;
            }
            stopNeighborRefresh();
            startNeighborRefresh();
        }
    }

    public synchronized void startNeighborRefresh() {
        System.out.println("startNeighborRefresh");

        refreshTimeMin = propertySource.getIotaNeighborRefreshTime();
        if(refreshTimeMin <= 0) {
            System.out.println("Ignoring neighbor refresh start, refresh time is: " + refreshTimeMin);
            return;
        }

        try {
            if(iotaNeighborRefreshTimer == null) {
                iotaNeighborRefreshTimer = new java.util.Timer();
                iotaNeighborRefreshTimer.schedule(new RefreshIotaNeighborTimerTask(),
                        refreshTimeMin * 60 * 1000,
                        refreshTimeMin * 60 * 1000);
            }
        }
        catch(Exception e) {
            System.out.println("startTimers iota nbrs refresh exception: " + e);
        }
    }

    public synchronized void stopNeighborRefresh() {
        System.out.println("stopNeighborRefresh");

        if(iotaNeighborRefreshTimer != null) {
            iotaNeighborRefreshTimer.cancel();
            iotaNeighborRefreshTimer = null;
        }
    }

}
