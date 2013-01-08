package fr.ippon.tatami.robot.route;

import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.Main;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import fr.ippon.tatami.robot.processor.StatusTransformer;
import fr.ippon.tatami.robot.processor.TatamiStatusSender;
import fr.ippon.tatami.robot.rest.Status;

@Component
public class CommonRouteBuilder extends RouteBuilder {

    private static final Log log = LogFactory.getLog(CommonRouteBuilder.class);

    @Inject
    TatamiStatusSender tatamiStatusSender;
    
    @Inject
    StatusTransformer statusTransformer;

//    @Inject
//    IdempotentRepository<String> idempotentRepository;

    @Override
    public void configure() {

        // Final endpoint used to send status to Tatami : 
         
        from("direct:toTatami"). // TODO : switch from direct: endpoint to an asynchronous one : seda: or jms: (for throttling in particular)
	        transform(body().append(simple(" #${header.SourceId} #TatamiBot"))).
	        bean(statusTransformer). 
	        bean(tatamiStatusSender);
    }

}
