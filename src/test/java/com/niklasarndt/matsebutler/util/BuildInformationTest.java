package com.niklasarndt.matsebutler.util;

import com.niklasarndt.testing.util.ButlerTest;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

/**
 * Created by Niklas on 2020/07/25.
 */
public class BuildInformationTest extends ButlerTest {

    @Test
    public void isSet() {
        logger.info(BuildInfo.NAME);
        logger.debug(BuildInfo.VERSION);

        assertNotEquals("UNKNOWN", BuildInfo.NAME);
        assertNotEquals("UNKNOWN", BuildInfo.DESCRIPTION);
        assertNotEquals("UNKNOWN", BuildInfo.VERSION);
        assertNotEquals("UNKNOWN", BuildInfo.TARGET_JDK);
        assertNotEquals("UNKNOWN", BuildInfo.TIMESTAMP);
        assertNotEquals("UNKNOWN", BuildInfo.URL);
    }

}