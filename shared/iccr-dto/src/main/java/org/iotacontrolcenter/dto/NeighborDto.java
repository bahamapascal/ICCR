package org.iotacontrolcenter.dto;

import java.time.Duration;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import org.roaringbitmap.RoaringBitmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NeighborDto {

    private boolean active;
    private String  descr;
    private String  key;
    private String  name;
    private String  uri;
    private int     numAt = 0;
    private int     numIt = 0;
    private int     numNt = 0;

    @JsonSerialize(using = ActivityDtoSerializer.class)
    @JsonDeserialize(using = ActivityDtoDeserializer.class)
    private ActivityDto activity = new ActivityDto();

    private int iotaNeighborRefreshTime = 1;

    // Length in real time of a tick (minutes)
    private float iotaActivityGranularity = 15;

    // Don't keep more than this much history (in minutes)
    public static final long trimTrigger = TimeUnit.DAYS.toMinutes(9);

    // Trim down to this much history (in minutes)
    public static final long activityRealTimeLength = TimeUnit.DAYS
            .toMinutes(7);

    // How many times should the server refresh activity per tick
    public static final int ACTIVITY_REFRESH_SAMPLE_RATE = 10;

    public NeighborDto() {
    }

    public NeighborDto(String key, String uri, String name, String descr,
            boolean active, ActivityDto activity
            ) {

        this.key = key;
        this.name = name;
        this.descr = descr;
        this.active = active;
        this.uri = uri;
        this.activity = activity;
    }

    public NeighborDto(String key, String uri, String name, String descr,
            boolean active, ActivityDto activity,
            int iotaNeighborRefreshTime) {
        this(key, uri, name, descr, active);

        this.activity = activity;
    }

    public NeighborDto(String key, String uri, String name, String descr,
            boolean active) {
        this.key = key;
        this.name = name;
        this.descr = descr;
        this.active = active;
        this.uri = uri;
        this.activity = new ActivityDto();
    }

    private int calcActivityPercentageOverPeriod(ZonedDateTime start,
            ZonedDateTime end) {
        
        RoaringBitmap mask = generateMask(start, end);
        int cardinality = RoaringBitmap.andCardinality(this.activity, mask);

        Duration duration = Duration.between(start, end);
        return (int) (100 * cardinality
                / (duration.toMinutes() / iotaActivityGranularity));
    }

    public static ZonedDateTime currentDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC);
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

        boolean sameKey = getKey().equals(that.getKey());
        boolean sameUri = getUri().equals(that.getUri());
        boolean sameActivity = getActivity().hashCode() == that.getActivity()
                .hashCode();
        boolean sameRefreshTime = getIotaNeighborRefreshTime() == that
                .getIotaNeighborRefreshTime();

        boolean sameTransasctions = getNumAt() == that.getNumAt()
                && getNumIt() == that.getNumIt()
                && getNumNt() == that.getNumNt();

        return sameKey && sameUri && sameActivity && sameRefreshTime
                && sameTransasctions;
    }

    public ActivityDto getActivity() {
        return activity;
    }

    public int getActivityPercentageOverLastDay() {
        ZonedDateTime now = currentDateTime();
        ZonedDateTime dayAgo = now.minus(Period.ofDays(1));

        return calcActivityPercentageOverPeriod(dayAgo, now);
    }

    public int getActivityPercentageOverLastWeek() {
        ZonedDateTime now = currentDateTime();
        ZonedDateTime weekAgo = now.minus(Period.ofWeeks(1));

        return calcActivityPercentageOverPeriod(weekAgo, now);
    }

    public long getActivityRealTimeLength() {
        return activityRealTimeLength;
    }

    public float getActivityRefreshTime() {
        return iotaActivityGranularity / ACTIVITY_REFRESH_SAMPLE_RATE;
    }

    public float getActivityGranularity() {
        return iotaActivityGranularity;
    }

    protected int getCurrentTick() {
        return this.getTickAtTime(currentDateTime());
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

    public int getTickAtTime(ZonedDateTime time) {

        long minutes = TimeUnit.SECONDS.toMinutes(time.toEpochSecond());

        return (int) (minutes / this.iotaActivityGranularity);

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

    public void setActivity(ActivityDto activity) {
        this.activity = activity;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumAt(int numAt) {
        // Update activity
        if (this.iotaActivityGranularity > 0 && numAt > this.numAt) {
            this.updateActivity();
        }
        this.numAt = numAt;
    }

    private void updateActivity() {
        // Record current activity
        this.activity.add(this.getCurrentTick());

        // Check if we need to trim
        if (this.activity.getLongCardinality() > trimTrigger) {
            trimActivity();
        }
    }

    private void trimActivity() {
        ZonedDateTime now = currentDateTime();
        ZonedDateTime start = now.minus(Period.ofDays(7)).withHour(0)
                .withMinute(0).withSecond(0).withNano(0);

        RoaringBitmap mask = generateMask(start, now);

        this.activity.and(mask);
    }

    private RoaringBitmap generateMask(ZonedDateTime start, ZonedDateTime end) {
        long startTick = this.getTickAtTime(start);
        long endTick = this.getTickAtTime(end);

        RoaringBitmap mask = new RoaringBitmap();
        mask.add(startTick, endTick);
        return mask;
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
        return "NeighborDto{" + "key='" + key + '\'' + ", name='" + name + '\''
                + ", descr='" + descr + '\'' + ", active='" + active + '\''
                + ", uri='" + uri + '\'' + '}';
    }

}
