package fr.ippon.tatami.robot;

import fr.ippon.tatami.robot.processor.TatamiStatusProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.spring.Main;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * The Tatami Robot.
 */
@Component
public class TatamiBot extends RouteBuilder {

    private static final Log log = LogFactory.getLog(TatamiBot.class);

    @Inject
    TatamiStatusProcessor tatamiStatusProcessor;

    @Inject
    IdempotentRepository<String> idempotentRepository;

    @Value("${tatami.rss.url}")
    private String rssUrl;

    @Value("${tatami.rss.lastUpdate}")
    private String rssLastUpdate;

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

    public static void main(String[] args) throws Exception {
        log.info("Starting the Tatami Bot");
        Main.main(args);
    }

    @Override
    public void configure() {

        log.info("Configuring RSS support");
        from(getRssEndpointUri()).id("rss"). // return a single SyndFeed each time (with a single SyndEntry)  
        		transform(simple("[${body.entries[0].title}](${body.entries[0].link})")).
                idempotentConsumer(body(), idempotentRepository).
                setHeader("SourceId",constant("BlogIppon")).
                to("direct:toTatami");

        log.info("Configuring Twitter support");
        from(getTwitterEndpointUri()).id("twitter").
                idempotentConsumer(body(), idempotentRepository).
                setHeader("SourceId",constant("Twitter")).
                to("direct:toTatami");

//        log.info("Configuring Github support");
        //https://github.com/eclipse/egit-github/tree/master/org.eclipse.egit.github.core
        
        
        // TODO : switch from direct: endpoint to an asynchronous one : seda: or jms: (for throttling in particular) 
        from("direct:toTatami").
	        transform(body().append(simple(" #${header.SourceId} #TatamiBot"))).
	        process(tatamiStatusProcessor);
    }

    String getRssEndpointUri() {
    	String rssEndpointUrl = "rss:" +
    			rssUrl +
    			"&lastUpdate=" +
    			rssLastUpdate;
    	return rssEndpointUrl;
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
