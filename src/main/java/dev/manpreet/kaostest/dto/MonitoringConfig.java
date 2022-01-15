package dev.manpreet.kaostest.dto;

import dev.manpreet.kaostest.providers.PollingProvider;
import dev.manpreet.kaostest.providers.ThreadCountProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class MonitoringConfig {

    private Integer failureRatePollSeconds;

    public void sanitizeConfig(ThreadCountProvider threadCountProvider) {
        if (failureRatePollSeconds == null || failureRatePollSeconds < 10) {
            log.warn("Invalid value set for failureRatePollSeconds in monitoringConfig");
            failureRatePollSeconds = threadCountProvider instanceof PollingProvider ?
                    ((PollingProvider) threadCountProvider).getPollSeconds() : 60;
            log.warn("failureRatePollSeconds set to default value of " + failureRatePollSeconds);
        }
    }
}
