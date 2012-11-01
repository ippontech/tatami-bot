package fr.ippon.tatami.robot;

import fr.ippon.tatami.robot.processor.TatamiStatusProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.XPathBuilder;
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

    private static final Log log = LogFactory.getLog(RouteBuilder.class);

    @Inject
    private TatamiStatusProcessor tatamiStatusProcessor;

    @Inject
    IdempotentRepository idempotentRepository;

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

    public void configure() {

        log.info("Configuring RSS support");
        from("rss:" +
                rssUrl +
                "&lastUpdate=" +
                rssLastUpdate).
                marshal().rss().
                setBody(XPathBuilder.xpath("concat('[', /rss/channel/item/title/text(), '](', /rss/channel/item/link/text(), ')')", String.class)).
                idempotentConsumer(body(), idempotentRepository).
                transform(body().append(" #BlogIppon #TatamiBot")).
                process(tatamiStatusProcessor);

        log.info("Configuring Twitter support");
        from("twitter://timeline/user?user=" +
                twitterUser +
                "&type=polling&delay=60&consumerKey=" +
                twitterConsumerKey +
                "&consumerSecret=" +
                twitterConsumerSecret +
                "&accessToken=" +
                twitterAccessToken +
                "&accessTokenSecret=" +
                twitterAccessTokenSecret).
                idempotentConsumer(body(), idempotentRepository).
                transform(body().append(" #Twitter #TatamiBot")).
                process(tatamiStatusProcessor);

        log.info("Configuring Github support");
        //https://github.com/eclipse/egit-github/tree/master/org.eclipse.egit.github.core
    }
}
