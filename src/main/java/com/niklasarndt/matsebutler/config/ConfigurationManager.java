package com.niklasarndt.matsebutler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

public class ConfigurationManager {

    private final File file;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Configuration config;

    public ConfigurationManager() {
        this("data/config.yml");
    }

    public ConfigurationManager(String path) {
        file = new File(path);

        reload();
    }

    public void reload() {
        logger.info("Reloading configuration");

        if (!file.exists()) {
            logger.warn("Configuration file does not exist in {}!", file.getAbsolutePath());
            config = new Configuration();
            return;
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            this.config = mapper.readValue(file, Configuration.class);
        } catch (IOException e) {
            logger.error("Can not parse config file", e);
        }

        logger.info("There are {} administrators registered.", config.getAdmins().size());

    }

    public Configuration getConfig() {
        return config;
    }
}
