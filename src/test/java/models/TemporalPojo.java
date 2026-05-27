package models;

import java.time.*;
import java.util.Objects;

public class TemporalPojo {
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime dateTime;
    private OffsetTime offsetTime;
    private OffsetDateTime offsetDateTime;
    private ZonedDateTime zonedDateTime;
    private Instant instant;
    private Duration duration;
    private Period period;

    public TemporalPojo() {}

    public TemporalPojo(LocalDate date, LocalTime time, LocalDateTime dateTime,
                        OffsetTime offsetTime, OffsetDateTime offsetDateTime,
                        ZonedDateTime zonedDateTime, Instant instant,
                        Duration duration, Period period) {
        this.date = date;
        this.time = time;
        this.dateTime = dateTime;
        this.offsetTime = offsetTime;
        this.offsetDateTime = offsetDateTime;
        this.zonedDateTime = zonedDateTime;
        this.instant = instant;
        this.duration = duration;
        this.period = period;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public OffsetTime getOffsetTime() { return offsetTime; }
    public void setOffsetTime(OffsetTime offsetTime) { this.offsetTime = offsetTime; }

    public OffsetDateTime getOffsetDateTime() { return offsetDateTime; }
    public void setOffsetDateTime(OffsetDateTime offsetDateTime) { this.offsetDateTime = offsetDateTime; }

    public ZonedDateTime getZonedDateTime() { return zonedDateTime; }
    public void setZonedDateTime(ZonedDateTime zonedDateTime) { this.zonedDateTime = zonedDateTime; }

    public Instant getInstant() { return instant; }
    public void setInstant(Instant instant) { this.instant = instant; }

    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }

    public Period getPeriod() { return period; }
    public void setPeriod(Period period) { this.period = period; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemporalPojo that = (TemporalPojo) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(time, that.time) &&
                Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(offsetTime, that.offsetTime) &&
                Objects.equals(offsetDateTime, that.offsetDateTime) &&
                Objects.equals(zonedDateTime, that.zonedDateTime) &&
                Objects.equals(instant, that.instant) &&
                Objects.equals(duration, that.duration) &&
                Objects.equals(period, that.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, dateTime, offsetTime, offsetDateTime, zonedDateTime, instant, duration, period);
    }
}
