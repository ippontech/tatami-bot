package fr.ippon.tatami.robot.route;

import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class GitHubRouteBuilder extends RouteBuilder {

    private static final Log log = LogFactory.getLog(GitHubRouteBuilder.class);

    @Inject
    IdempotentRepository<String> idempotentRepository;

    @Override
    public void configure() {
//      log.info("Configuring Github support");
      //https://github.com/eclipse/egit-github/tree/master/org.eclipse.egit.github.core
    }
}
