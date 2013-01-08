package fr.ippon.tatami.robot.route;


import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import twitter4j.Status;
import twitter4j.User;
import fr.ippon.tatami.robot.processor.StatusTransformer;
import fr.ippon.tatami.robot.test.FakeTatamiStatusSender;

public class TwitterRouteBuilderTest extends CamelTestSupport {
	
	private static final Log log = LogFactory.getLog(TwitterRouteBuilderTest.class);

    private FakeTatamiStatusSender fakeProcessor;
    
    @Override
    // Called during @Before handling of the super class ...
    protected RouteBuilder[] createRouteBuilders() throws Exception {
    	fakeProcessor = new FakeTatamiStatusSender();

    	CommonRouteBuilder commonRoutebuilder = new CommonRouteBuilder();
    	commonRoutebuilder.tatamiStatusSender = fakeProcessor;
    	commonRoutebuilder.statusTransformer = new StatusTransformer();
    		
    	TwitterRouteBuilder routeBuilder = new TwitterRouteBuilder();
    	routeBuilder.idempotentRepository = new MemoryIdempotentRepository();
    	    	
    	return new RouteBuilder[] { commonRoutebuilder, routeBuilder } ;
    }
       
    @Override
    public boolean isUseAdviceWith() {
    	return true;
    }
   
    @Test
    public void testTwitterRoute() throws Exception {
    	
    	context.getRouteDefinition("twitter").adviceWith(context, new AdviceWithRouteBuilder() {
    		@Override
    		public void configure() throws Exception {
    			replaceFromWith("direct:twitterRouteTest");
    		}
    	});
    	RouteDefinition rssRoute = context.getRouteDefinition("rss");
    	context.getRouteDefinitions().remove(rssRoute);
    	
    	context.start(); // necessary because of isUseAdviceWith=true
    	
    	Status fakeStatus = fakeStatus("2013/01/05 12:34", "ippontech", "a first tweet");
    	template.sendBody("direct:twitterRouteTest",fakeStatus);
    	
    	Thread.sleep(1000); // will be necessary when send will be asynchronous
    	
    	assertThat(fakeProcessor.messages.size(), is(1));
//    	assertThat(mockProcessor.messages.get(0).getContent(), is("Sat Jan 05 12:34:00 CET 2013 (ippontech) a first tweet #Twitter #TatamiBot")); // Doesn't work : Timezone differ in CI server...
    	assertThat(fakeProcessor.messages.get(0).getContent(), is(fakeStatus.getCreatedAt()+" (ippontech) a first tweet #Twitter #TatamiBot"));

    }

	private Status fakeStatus(String createdAtAsStr, String screenName, String text) throws ParseException {
		// cf {@link TwitterConverter} : on a besoin que de la date, le user et le texte pour le moment
    	User user = mock(User.class);
    	when(user.getScreenName()).thenReturn(screenName);
    	Status status = mock(Status.class);
    	Date createdAt = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(createdAtAsStr);
    	when(status.getUser()).thenReturn(user);
    	when(status.getCreatedAt()).thenReturn(createdAt);
    	when(status.getText()).thenReturn(text);
    	
    	return status;
	}

}
