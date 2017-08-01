package org.iotacontrolcenter.rest.delegate;


import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.iotacontrolcenter.dto.ActionResponse;
import org.iotacontrolcenter.dto.IccrIotaNeighborsPropertyDto;
import org.iotacontrolcenter.dto.IotaGetNeighborsResponseDto;
import org.iotacontrolcenter.dto.IotaNeighborDto;
import org.iotacontrolcenter.dto.NeighborDto;
import org.iotacontrolcenter.iccr.agent.IccrActionFactory;
import org.iotacontrolcenter.iota.agent.ActionFactory;
import org.iotacontrolcenter.properties.source.PropertySource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static boolean isSameHost(String ip1, String ip2) {
        //[2a01:4f8:190:32cc::2]
        // and
        // 2a01:4f8:190:32cc:0:0:0:2

        if(ip1 == null || ip2 == null || ip1.isEmpty() | ip2.isEmpty()) {
            // InetAddress.getByName() resolves null and blank strings as localhost
            return false;
        }

        String normalIp1 = normalizeIp(ip1);
        String normalIp2 = normalizeIp(ip2);

        return normalIp1 != null && normalIp2 != null && normalIp1.equals(normalIp2);
    }
    public static boolean isSameNbr ( NeighborDto nbr, IotaNeighborDto iotaNbr) {
        // Partially borrowed from ICCJ to match NeighborDto with the result of Iota's getNeighbors
        // In our model, the nbr uri is something like this:
        //         udp://fred.com:14265
        //  or
        //         udp://10.0.0.0:14265
        //  or
        //         udp://[2a01:4f8:190:32cc::2]:14265

        // The problem: nbr coming from IOTA getNeighbors, the address is something like this:
        //         fred.com/93.188.173.198:14265
        //  or
        //         /10.0.0.0:14265
        //  or iv ipv6:
        //         /2a01:4f8:190:32cc:0:0:0:2:14265
        //  or soon, no embedded "/":
        //         93.188.173.198:14265
        String nbrUri = nbr.getUri();

        int addrSepIdx = nbrUri.indexOf("://");
        int portIdx = nbrUri.lastIndexOf(":");
        if(addrSepIdx < 0) {
            addrSepIdx = 0;
        }
        else {
            addrSepIdx += 3;
        }

        String nbrHost = nbrUri.substring(addrSepIdx, portIdx);
        String nbrPort = nbrUri.substring(portIdx);



        String iotaUri = iotaNbr.getAddress();

        portIdx = iotaUri.lastIndexOf(":");
        String iotaPort = iotaUri.substring(portIdx);

        addrSepIdx = iotaUri.indexOf("/");
        String iotaAddr1 = addrSepIdx < 0 ? "" : iotaUri.substring(0, addrSepIdx);


        int addr2StartIdx = addrSepIdx >= 0 ? addrSepIdx+1 : 0;
        String iotaAddr2 = iotaUri.substring(addr2StartIdx, portIdx);

        boolean samePort = StringUtils.isNotBlank(nbrPort) && StringUtils.equals(nbrPort, iotaPort);
        boolean sameHost = isSameHost(nbrHost, iotaAddr2) || isSameHost(nbrHost, iotaAddr1);

        return samePort && sameHost;

    }
    public static String normalizeIp (String input) {
        String output = null;
        try {
            output = InetAddress.getByName(input).toString();
        }
        catch(UnknownHostException e) {
            System.out.println("Swallowing an UnknownHostException. " +
                    "Probably just means someone entered an invalid hostname for a neighbor. " + e);
        }
        return output;
    }

    private java.util.Timer iotaNeighborRefreshTimer, iotaActivityRefreshTimer;

    private Integer nbrRefreshTime = null;
    private Float   activityRefreshTime = null;

    private PropertySource propertySource = PropertySource.getInstance();

    private Delegate() {
        System.out.println("new Delegate");
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
            restartNeighborRefresh();
            restartActivityRefresh();
        }
        else if(prop.equals(PropertySource.IOTA_NBR_REFRESH_TIME_PROP)) {
            Integer prevRefreshTimeMin = nbrRefreshTime;
            if(propertySource.getIotaNeighborRefreshTime() == prevRefreshTimeMin) {
                return;
            }
            restartNeighborRefresh();
        }
        else if(prop.equals(PropertySource.IOTA_ACTIVITY_GRANULARITY_PROP)) {
            // Granularity affects refresh time. Decide if we restart the timer.
            Float prevActivityRefreshTime = activityRefreshTime;
            if (propertySource
                    .getIotaActivityRefreshTime() == prevActivityRefreshTime) {
                return;
             }
            restartActivityRefresh();
        }
    }

    public synchronized  void iotaActionDone(String action, ActionResponse resp) {
        if(action.equals(ActionFactory.STOP)) {
            stopNeighborRefresh();
            stopActivityRefresh();
        }
        else if(action.equals(ActionFactory.START)) {
            startNeighborRefresh();
            startActivityRefresh();
        }
        else if(action.equals(ActionFactory.NEIGHBORS)){
            IotaGetNeighborsResponseDto dto = null;
            IccrIotaNeighborsPropertyDto iccrNbrs = propertySource.getIotaNeighbors();
            int iotaNeighborRefreshTime           = propertySource.getIotaNeighborRefreshTime();
            float iotaActivityGranularity         = propertySource.getIotaActivityGranularity();

            try {
                Gson gson = new GsonBuilder().create();
                dto = gson.fromJson(resp.getContent(), IotaGetNeighborsResponseDto.class);

                for( IotaNeighborDto iotaNbr : dto.getNeighbors()) {
                    for ( NeighborDto iccrNbr : iccrNbrs.getNbrs()) {
                        if( isSameNbr(iccrNbr, iotaNbr) ) {
                            iccrNbr.setIotaNeighborRefreshTime(
                                    iotaNeighborRefreshTime);
                            iccrNbr.setActivityGranularity(
                                    iotaActivityGranularity);
                            iccrNbr.setNumAt(iotaNbr.getNumberOfAllTransactions());
                            iccrNbr.setNumIt(iotaNbr.getNumberOfInvalidTransactions());
                            iccrNbr.setNumNt(iotaNbr.getNumberOfNewTransactions());

                            // Update the Iota neighbor being sent to the client
                            iotaNbr.setActivityPercentageDay(iccrNbr.getActivityPercentageOverLastDay());
                            iotaNbr.setActivityPercentageWeek(iccrNbr.getActivityPercentageOverLastWeek());
                        }
                    }
                }
            }
            catch(Exception e) {
                System.out.println(action + ", exception mapping json response: " + e);
            }

            propertySource.setIotaNeighborsConfig(iccrNbrs);
        }
    }


    public synchronized void startNeighborRefresh() {
        System.out.println("startNeighborRefresh");

        nbrRefreshTime = propertySource.getIotaNeighborRefreshTime();
        if (nbrRefreshTime <= 0) {
            System.out.println(
                    "Ignoring neighbor refresh start, refresh time is: "
                            + nbrRefreshTime);
            return;
        }

        try {
            if(iotaNeighborRefreshTimer == null) {
                iotaNeighborRefreshTimer = new java.util.Timer();
                iotaNeighborRefreshTimer.schedule(new RefreshIotaNeighborTimerTask(),
                        nbrRefreshTime * 60 * 1000, nbrRefreshTime * 60 * 1000);
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

    public synchronized void startActivityRefresh() {
        System.out.println("startActivityRefresh");

        // refresh time in minutes
        activityRefreshTime = propertySource.getIotaActivityRefreshTime();
        if (activityRefreshTime <= 0) {
            activityRefreshTime = 1f;
        }

        try {
            int refreshTimeMilli = Math.round(activityRefreshTime * 60 * 1000);
            if (iotaActivityRefreshTimer == null) {
                iotaActivityRefreshTimer = new java.util.Timer();
                iotaActivityRefreshTimer.schedule(
                        new RefreshIotaActivityTimerTask(),
                        refreshTimeMilli, refreshTimeMilli);
            }
        }
        catch (Exception e) {
            System.out.println(
                    "startTimers iota activity refresh exception: " + e);
        }
    }

    public synchronized void stopActivityRefresh() {
        System.out.println("stopNeighborRefresh");

        if (iotaActivityRefreshTimer != null) {
            iotaActivityRefreshTimer.cancel();
            iotaActivityRefreshTimer = null;
        }
    }

    public synchronized void restartNeighborRefresh() {
        stopNeighborRefresh();
        startNeighborRefresh();
    }

    public synchronized void restartActivityRefresh() {
        stopActivityRefresh();
        startActivityRefresh();
    }

}
