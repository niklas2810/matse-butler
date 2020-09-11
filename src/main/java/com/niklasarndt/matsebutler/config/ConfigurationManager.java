package com.niklasarndt.matsebutler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.niklasarndt.matsebutler.Butler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ConfigurationManager {

    private final File f;
    private final Butler butler;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Configuration config;

    public ConfigurationManager(Butler butler) {
        this("data/config.yml", butler);
    }

    public ConfigurationManager(String path, Butler butler) {
        f = new File(path);
        this.butler = butler;

        if (!f.exists()) {
            logger.warn("Configuration file does not exist in {}!", f.getAbsolutePath());
            config = new Configuration();
            return;
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            this.config = mapper.readValue(f, Configuration.class);
        } catch (IOException e) {
            logger.error("Can not parse config file", e);
        }

        logger.info("There are {} administrators registered.", config.getAdmins().size());
    }

    public Configuration getConfig() {
        return config;
    }
}
