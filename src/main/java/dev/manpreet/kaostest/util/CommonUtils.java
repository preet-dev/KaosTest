package dev.manpreet.kaostest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.manpreet.kaostest.dto.MonitoringConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class CommonUtils {

    public static MonitoringConfig getMonitoringConfigFromEnvironment() {
        if (StringUtils.defaultString(System.getenv(Constants.CONFIG_FILE_ENV)).isBlank()) {
            return new MonitoringConfig();
        }
        return getMonitoringConfigFromYAMLFile(System.getenv(Constants.CONFIG_FILE_ENV));
    }

    public static MonitoringConfig getMonitoringConfigFromYAMLFile(String yamlFile) {
        File configFile = new File(yamlFile);
        if (!configFile.exists() || !configFile.canRead()) {
            log.warn("Unable to read config file at " + yamlFile);
            return new MonitoringConfig();
        }
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        try {
            return yamlMapper.readValue(configFile, MonitoringConfig.class);
        } catch (IOException ioException) {
            log.error("Exception occurred while trying to read YAML file at " + yamlFile);
        }
        return new MonitoringConfig();
    }
}
