package fr.ippon.tatami.robot;

import org.apache.camel.spring.Main;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Tatami Robot.
 */
public class TatamiBot {

    private static final Log log = LogFactory.getLog(TatamiBot.class);

    public static void main(String[] args) throws Exception {
        log.info("Starting the Tatami Bot");
        Main.main(args);
    }

}
