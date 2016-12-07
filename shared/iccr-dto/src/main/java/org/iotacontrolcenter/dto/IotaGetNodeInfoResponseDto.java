package org.iotacontrolcenter.dto;


public class IotaGetNodeInfoResponseDto {

    private String appName;
    private String appVersion;
    private int jreAvailableProcessors;
    private long jreFreeMemory;
    private long jreMaxMemory;
    private long jreTotalMemory;
    private String latestMilestone;
    private int latestMilestoneIndex;
    private String latestSolidSubtangleMilestone;
    private int latestSolidSubtangleMilestoneIndex;
    private int neighbors;
    private int packetsQueueSize;
    private long time;
    private long tips;
    private int transactionsToRequest;
    private int duration;

    public IotaGetNodeInfoResponseDto() { }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getJreAvailableProcessors() {
        return jreAvailableProcessors;
    }

    public void setJreAvailableProcessors(int jreAvailableProcessors) {
        this.jreAvailableProcessors = jreAvailableProcessors;
    }

    public long getJreFreeMemory() {
        return jreFreeMemory;
    }

    public void setJreFreeMemory(long jreFreeMemory) {
        this.jreFreeMemory = jreFreeMemory;
    }

    public long getJreMaxMemory() {
        return jreMaxMemory;
    }

    public void setJreMaxMemory(long jreMaxMemory) {
        this.jreMaxMemory = jreMaxMemory;
    }

    public long getJreTotalMemory() {
        return jreTotalMemory;
    }

    public void setJreTotalMemory(long jreTotalMemory) {
        this.jreTotalMemory = jreTotalMemory;
    }

    public String getLatestMilestone() {
        return latestMilestone;
    }

    public void setLatestMilestone(String latestMilestone) {
        this.latestMilestone = latestMilestone;
    }

    public int getLatestMilestoneIndex() {
        return latestMilestoneIndex;
    }

    public void setLatestMilestoneIndex(int latestMilestoneIndex) {
        this.latestMilestoneIndex = latestMilestoneIndex;
    }

    public String getLatestSolidSubtangleMilestone() {
        return latestSolidSubtangleMilestone;
    }

    public void setLatestSolidSubtangleMilestone(String latestSolidSubtangleMilestone) {
        this.latestSolidSubtangleMilestone = latestSolidSubtangleMilestone;
    }

    public int getLatestSolidSubtangleMilestoneIndex() {
        return latestSolidSubtangleMilestoneIndex;
    }

    public void setLatestSolidSubtangleMilestoneIndex(int latestSolidSubtangleMilestoneIndex) {
        this.latestSolidSubtangleMilestoneIndex = latestSolidSubtangleMilestoneIndex;
    }

    public int getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(int neighbors) {
        this.neighbors = neighbors;
    }

    public int getPacketsQueueSize() {
        return packetsQueueSize;
    }

    public void setPacketsQueueSize(int packetsQueueSize) {
        this.packetsQueueSize = packetsQueueSize;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTips() {
        return tips;
    }

    public void setTips(long tips) {
        this.tips = tips;
    }

    public int getTransactionsToRequest() {
        return transactionsToRequest;
    }

    public void setTransactionsToRequest(int transactionsToRequest) {
        this.transactionsToRequest = transactionsToRequest;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "IotaGetNodeInfoResponseDto{" +
                "appName='" + appName + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", jreAvailableProcessors=" + jreAvailableProcessors +
                ", jreFreeMemory=" + jreFreeMemory +
                ", jreMaxMemory=" + jreMaxMemory +
                ", jreTotalMemory=" + jreTotalMemory +
                ", latestMilestone='" + latestMilestone + '\'' +
                ", latestMilestoneIndex=" + latestMilestoneIndex +
                ", latestSolidSubtangleMilestone='" + latestSolidSubtangleMilestone + '\'' +
                ", latestSolidSubtangleMilestoneIndex=" + latestSolidSubtangleMilestoneIndex +
                ", neighbors=" + neighbors +
                ", packetsQueueSize=" + packetsQueueSize +
                ", time=" + time +
                ", tips=" + tips +
                ", transactionsToRequest=" + transactionsToRequest +
                ", duration=" + duration +
                '}';
    }
}
