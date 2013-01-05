package fr.ippon.tatami.robot;


import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
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
import fr.ippon.tatami.robot.processor.TatamiStatusProcessor;

public class TatamiBotTest extends CamelTestSupport {
	
	private static final Log log = LogFactory.getLog(TatamiBotTest.class);

	private final class MockTatamiStatusProcessor extends TatamiStatusProcessor {
		public List<String> messages = new ArrayList<String>();
		@Override
		public void process(Exchange exchange) throws Exception {
			String status = exchange.getIn().getBody(String.class);
			log.info("Received status : "+status);
			messages.add(status);
		}
	}

    private TatamiBot sut;
    private MockTatamiStatusProcessor mockProcessor;
    
    @Override
    // Called during @Before handling of the super class ...
    protected RouteBuilder createRouteBuilder() throws Exception {
    	mockProcessor = new MockTatamiStatusProcessor();

    	sut = new TatamiBot();
    	sut.idempotentRepository = new MemoryIdempotentRepository();
    	sut.tatamiStatusProcessor = mockProcessor;
    			
    	return sut;
    }
       
    @Override
    public boolean isUseAdviceWith() {
    	return true;
    }
   
    @Test
    public void testRssRoute() throws Exception {
    	final String fileUrl = this.getClass().getResource("rss.xml").toExternalForm();
    	
    	context.getRouteDefinition("rss").adviceWith(context, new AdviceWithRouteBuilder() {
    		@Override
    		public void configure() throws Exception {
    			replaceFromWith("rss:"+fileUrl+"?consumer.delay=10&consumer.initialDelay=0");
    		}
    	});
    	RouteDefinition twitterRoute = context.getRouteDefinition("twitter");
    	context.getRouteDefinitions().remove(twitterRoute);
    	
    	context.start(); // necessary because of isUseAdviceWith=true
    	
    	Thread.sleep(1000);
    	
    	assertThat(mockProcessor.messages.size(), is(3));
    	assertThat(mockProcessor.messages.get(0), is("[Ippevent Mobilité – Applications mobiles – ouverture des inscriptions](http://feedproxy.google.com/~r/LeBlogDesExpertsJ2ee/~3/GcJYERHTfoQ/) #BlogIppon #TatamiBot"));
    	assertThat(mockProcessor.messages.get(1), is("[Business – Ippon Technologies acquiert Atomes et renforce son offre Cloud](http://feedproxy.google.com/~r/LeBlogDesExpertsJ2ee/~3/wK-Y47WGZBQ/) #BlogIppon #TatamiBot"));
    	assertThat(mockProcessor.messages.get(2), is("[Les Méthodes Agiles – Définition de l’Agilité](http://feedproxy.google.com/~r/LeBlogDesExpertsJ2ee/~3/hSqyt1MCOoo/) #BlogIppon #TatamiBot"));
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
    	
    	assertThat(mockProcessor.messages.size(), is(1));
//    	assertThat(mockProcessor.messages.get(0), is("Sat Jan 05 12:34:00 CET 2013 (ippontech) a first tweet #Twitter #TatamiBot")); // Doesn't work : Timezone differ in CI server...
    	assertThat(mockProcessor.messages.get(0), is(fakeStatus.getCreatedAt()+" (ippontech) a first tweet #Twitter #TatamiBot"));

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
