package fr.ippon.tatami.robot.processor;

import java.util.Map;

import org.apache.camel.Headers;
import org.springframework.stereotype.Component;

import fr.ippon.tatami.robot.rest.Status;

@Component
public class StatusTransformer {
	
	public Status toStatus(String content, @Headers Map<String, ?> headers) {
		
        Status status = new Status();
        status.setContent(content);
        
        // note : we can enrich message with header data :
        String groupId = (String) headers.get("TatamiGroupId");
        if(groupId != null) {
        	status.setGroupId(groupId);
        }
        
        return status;
	}
}
