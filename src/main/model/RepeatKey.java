package model;

import java.time.temporal.ChronoUnit;

public class RepeatKey {
    private final ChronoUnit timeScale;
    private final int dayOf;

    public RepeatKey(ChronoUnit timeScale, int dayOf) {
        this.timeScale = timeScale;
        this.dayOf = dayOf;
    }

    public ChronoUnit getTimeScale() {
        return timeScale;
    }

    public int getDayOf() {
        return dayOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepeatKey)) {
            return false;
        }
        RepeatKey repeatKey = (RepeatKey) o;
        if (dayOf != repeatKey.dayOf) {
            return false;
        }
        return timeScale == repeatKey.timeScale;
    }

    @Override
    public int hashCode() {
        int result = timeScale.hashCode();
        result = 31 * result + dayOf;
        return result;
    }
}
