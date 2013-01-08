package fr.ippon.tatami.robot.route;


import static org.hamcrest.CoreMatchers.is;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import fr.ippon.tatami.robot.processor.StatusTransformer;
import fr.ippon.tatami.robot.test.FakeTatamiStatusSender;

public class RssRouteBuilderTest extends CamelTestSupport {
	
	private static final Log log = LogFactory.getLog(RssRouteBuilderTest.class);

    private FakeTatamiStatusSender fakeProcessor;
    
    @Override
    // Called during @Before handling of the super class ...
    protected RouteBuilder[] createRouteBuilders() throws Exception {
    	fakeProcessor = new FakeTatamiStatusSender();

    	CommonRouteBuilder commonRoutebuilder = new CommonRouteBuilder();
    	commonRoutebuilder.tatamiStatusSender = fakeProcessor;
    	commonRoutebuilder.statusTransformer = new StatusTransformer();
    		    	
    	RssRouteBuilder routeBuilder = new RssRouteBuilder();
    	routeBuilder.idempotentRepository = new MemoryIdempotentRepository();
    	
    	return new RouteBuilder[] { commonRoutebuilder, routeBuilder } ;
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
    	
    	context.start(); // necessary because of isUseAdviceWith=true
    	
    	Thread.sleep(1000);
    	
    	assertThat(fakeProcessor.messages.size(), is(3));
    	assertThat(fakeProcessor.messages.get(0).getContent(), is("[Ippevent Mobilité – Applications mobiles – ouverture des inscriptions](http://feedproxy.google.com/~r/LeBlogDesExpertsJ2ee/~3/GcJYERHTfoQ/) #BlogIppon #TatamiBot"));
    	assertThat(fakeProcessor.messages.get(1).getContent(), is("[Business – Ippon Technologies acquiert Atomes et renforce son offre Cloud](http://feedproxy.google.com/~r/LeBlogDesExpertsJ2ee/~3/wK-Y47WGZBQ/) #BlogIppon #TatamiBot"));
    	assertThat(fakeProcessor.messages.get(2).getContent(), is("[Les Méthodes Agiles – Définition de l’Agilité](http://feedproxy.google.com/~r/LeBlogDesExpertsJ2ee/~3/hSqyt1MCOoo/) #BlogIppon #TatamiBot"));
    }

}
