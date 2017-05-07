package org.iotacontrolcenter.dto;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.BitSet;

public class NeighborDto {

    private boolean active;
    private String descr;
    private String key;
    private String name;
    private String uri;
    private int numAt = 0;
    private int numIt = 0;
    private int numNt = 0;
    private BitSet activity = new BitSet();


    private int iotaNeighborRefreshTime = 0;

    // Length in real time to keep history (two weeks in minutes)
    private final int activityRealTimeLength = 2*7*24*60;
    // Number of ticks to store activity
    private int activityTickLength;

    public NeighborDto() {
    }

    public NeighborDto(String key, String uri, String name, String descr,
            boolean active, BitSet activity, int iotaNeighborRefreshTime) {
        this.key = key;
        this.name = name;
        this.descr = descr;
        this.active = active;
        this.uri = uri;
        this.activity = activity;
        this.iotaNeighborRefreshTime = iotaNeighborRefreshTime;

        this.updateTickLenth();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NeighborDto)) {
            return false;
        }

        NeighborDto that = (NeighborDto) o;

        if (getUri() != that.getUri()) {
            return false;
        }
        return getKey().equals(that.getKey());
    }

    public BitSet getActivity() {
        return activity;
    }

    public int getActivityPercentageOverLastDay() {
        LocalDateTime now    = LocalDateTime.now();
        LocalDateTime dayAgo = now.minus(Period.ofDays(1));

        return calcActivityPercentageOverPeriod(dayAgo, now);
    }

    public int getActivityPercentageOverLastWeek() {
        LocalDateTime now    = LocalDateTime.now();
        LocalDateTime weekAgo = now.minus(Period.ofWeeks(1));

        return calcActivityPercentageOverPeriod(weekAgo, now);
    }
    public int getActivityRealTimeLength() {
        return activityRealTimeLength;
    }

    public int getActivityTickLength() {
        return activityTickLength;
    }

    public String getDescr() {
        return descr;
    }

    public int getIotaNeighborRefreshTime() {
        return iotaNeighborRefreshTime;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getNumAt() {
        return numAt;
    }

    public int getNumIt() {
        return numIt;
    }

    public int getNumNt() {
        return numNt;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public int hashCode() {
        int result = getUri().hashCode();
        result = 31 * result + getKey().hashCode();
        return result;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setActivity(BitSet activity) {
        this.activity = activity;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setIotaNeighborRefreshTime(int iotaNeighborRefreshTime) {
        this.iotaNeighborRefreshTime = iotaNeighborRefreshTime;
        this.updateTickLenth();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumAt(int numAt) {
        // Update activity
        if (this.iotaNeighborRefreshTime > 0 &&
                numAt > this.numAt) {
            this.activity.set(this.getCurrentTick());
        }
        this.numAt = numAt;
    }

    public void setNumIt(int numIt) {
        this.numIt = numIt;
    }

    public void setNumNt(int numNt) {
        this.numNt = numNt;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "NeighborDto{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", active='" + active + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }

    private int calcActivityPercentageOverPeriod(LocalDateTime start, LocalDateTime end) {
        int startTick = this.getTickAtTime(start);
        int endTick   = this.getTickAtTime(end);
        BitSet activity = this.activity.get(startTick, endTick);

        if(activity.length() < 1) {
            return 0;
        } else {
            return 100*activity.cardinality()/activity.length();
        }
    }

    private void updateTickLenth(){
        this.activityTickLength = this.activityRealTimeLength/this.iotaNeighborRefreshTime;
    }

    protected int getCurrentTick() {
        return this.getTickAtTime(LocalDateTime.now());
    }

    protected int getTickAtTime(LocalDateTime time) {

        // Two week span starting two Sundays ago
        LocalDateTime two_sundays_ago = LocalDate.now().minus(Period.ofWeeks(1)).with(
                TemporalAdjusters.previous(DayOfWeek.SUNDAY)).atStartOfDay();

        Duration location_in_period = Duration.between(two_sundays_ago, time);
        return (int) (location_in_period.toMinutes()/this.iotaNeighborRefreshTime);

    }
}
