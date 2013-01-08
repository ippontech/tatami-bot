package fr.ippon.tatami.robot.route;


import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import fr.ippon.tatami.robot.processor.StatusTransformer;
import fr.ippon.tatami.robot.rest.Status;
import fr.ippon.tatami.robot.test.FakeTatamiStatusSender;

public class CommonRouteBuilderTest extends CamelTestSupport {
	
	private static final Log log = LogFactory.getLog(CommonRouteBuilderTest.class);

    private FakeTatamiStatusSender fakeProcessor;
    
    @Override
    // Called during @Before handling of the super class ...
    protected RouteBuilder createRouteBuilder() throws Exception {
    	fakeProcessor = new FakeTatamiStatusSender();

    	CommonRouteBuilder builder = new CommonRouteBuilder();
    	builder.tatamiStatusSender = fakeProcessor;
    	builder.statusTransformer = new StatusTransformer();
    		    	
    	return builder;
    }
   
    @Test
    public void testTatamiProperties() throws Exception {
   	
    	Map<String, Object> headers = new HashMap<String, Object>();
    	headers.put("SourceId", "TestSource");
    	headers.put("TatamiGroupId", "myGroup");
		template.sendBodyAndHeaders("direct:toTatami","The content",headers);
    	
    	Thread.sleep(1000);
    	
    	assertThat(fakeProcessor.messages.size(), is(1));
    	
    	Status status = fakeProcessor.messages.get(0);
		assertThat(status.getContent(), is("The content #TestSource #TatamiBot"));
		assertThat(status.getGroupId(), is("myGroup"));
    }

}
