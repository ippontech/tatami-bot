package fr.ippon.tatami.robot.route;

import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class TwitterRouteBuilder extends RouteBuilder {

    private static final Log log = LogFactory.getLog(TwitterRouteBuilder.class);

    @Inject
    IdempotentRepository<String> idempotentRepository;

    @Value("${tatami.twitter.consumerKey}")
    private String twitterConsumerKey;

    @Value("${tatami.twitter.consumerSecret}")
    private String twitterConsumerSecret;

    @Value("${tatami.twitter.accessToken}")
    private String twitterAccessToken;

    @Value("${tatami.twitter.accessTokenSecret}")
    private String twitterAccessTokenSecret;

    @Value("${tatami.twitter.user}")
    private String twitterUser;

    @Override
    public void configure() {

        log.info("Configuring Twitter support");
        from(getTwitterEndpointUri()).id("twitter").
                idempotentConsumer(body(), idempotentRepository).
                setHeader("SourceId",constant("Twitter")).
                to("direct:toTatami");
    }

    String getTwitterEndpointUri() {
		String twitterEndpointUrl = "twitter://timeline/user?user=" +
                twitterUser +
                "&type=polling&delay=60&consumerKey=" +
                twitterConsumerKey +
                "&consumerSecret=" +
                twitterConsumerSecret +
                "&accessToken=" +
                twitterAccessToken +
                "&accessTokenSecret=" +
                twitterAccessTokenSecret;
	 	
		return twitterEndpointUrl;
	}

}
