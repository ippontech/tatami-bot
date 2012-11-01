package fr.ippon.tatami.robot.processor;

import fr.ippon.tatami.robot.rest.TatamiRestClient;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class TatamiStatusProcessor implements Processor {

    private final Log log = LogFactory.getLog(TatamiStatusProcessor.class);

    @Inject
    private TatamiRestClient tatamiRestClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        String content = exchange.getIn().getBody(String.class);
        if (log.isDebugEnabled()) {
            log.debug("Posting content to Tatami : " + content);
        }
        tatamiRestClient.postStatus(content);
    }
}
