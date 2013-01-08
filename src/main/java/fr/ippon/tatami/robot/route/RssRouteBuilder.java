package fr.ippon.tatami.robot.route;

import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RssRouteBuilder extends RouteBuilder {

    private static final Log log = LogFactory.getLog(RssRouteBuilder.class);

    @Inject
    IdempotentRepository<String> idempotentRepository;

    @Value("${tatami.rss.url}")
    private String rssUrl;

    @Value("${tatami.rss.lastUpdate}")
    private String rssLastUpdate;

    @Override
    public void configure() {

        log.info("Configuring RSS support");
        from(getRssEndpointUri()).id("rss"). // return a single SyndFeed each time (with a single SyndEntry)  
        		transform(simple("[${body.entries[0].title}](${body.entries[0].link})")).
                idempotentConsumer(body(), idempotentRepository).
                setHeader("SourceId",constant("BlogIppon")).
                to("direct:toTatami");
    }

    String getRssEndpointUri() {
    	String rssEndpointUrl = "rss:" +
    			rssUrl +
    			"&lastUpdate=" +
    			rssLastUpdate;
    	return rssEndpointUrl;
    }

}
