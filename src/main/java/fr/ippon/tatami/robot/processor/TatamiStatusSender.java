package fr.ippon.tatami.robot.processor;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import fr.ippon.tatami.robot.rest.Status;
import fr.ippon.tatami.robot.rest.TatamiRestClient;

/**
 * To be used as a camel Bean
 *
 */
@Component
public class TatamiStatusSender {

    private final Log log = LogFactory.getLog(TatamiStatusSender.class);

    @Inject
    private TatamiRestClient tatamiRestClient;

   	public void sendStatus(Status status) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Posting content to Tatami : " + status);
        }
        tatamiRestClient.postStatus(status);
    }
}
