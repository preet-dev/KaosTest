package dev.manpreet.rpdtest.providers.duration;

import dev.manpreet.rpdtest.providers.DurationProvider;

import java.util.concurrent.TimeUnit;

public class FixedDurationProvider implements DurationProvider {

    private long remainingSecs = -1;
    private long prevTime;

    public FixedDurationProvider(int time, TimeUnit timeUnit) {
        if (timeUnit.equals(TimeUnit.MICROSECONDS) || timeUnit.equals(TimeUnit.MILLISECONDS) || timeUnit.equals(TimeUnit.NANOSECONDS)) {
            throw new IllegalArgumentException("Time unit must be in seconds or a higher unit");
        }
        setStartTime(time, timeUnit);
    }

    @Override
    public boolean stopTests() {
        remainingSecs = remainingSecs - (System.currentTimeMillis() - prevTime);
        prevTime = System.currentTimeMillis();
        return remainingSecs <= 0;
    }

    public void setStartTime(int time, TimeUnit timeUnit) {
        if (remainingSecs < 0) {
            switch (timeUnit) {
                case SECONDS:
                    remainingSecs = time;
                    break;
                case MINUTES:
                    remainingSecs = time * 60L;
                    break;
                case HOURS:
                    remainingSecs = time * 60L * 60L;
                    break;
                default:
                    remainingSecs = time * 24L * 60L * 60L;
            }
            prevTime = System.currentTimeMillis();
        }
    }
}