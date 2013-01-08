package fr.ippon.tatami.robot.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ippon.tatami.robot.processor.TatamiStatusSender;
import fr.ippon.tatami.robot.rest.Status;

public class FakeTatamiStatusSender extends TatamiStatusSender {
	
	private static final Log log = LogFactory.getLog(FakeTatamiStatusSender.class);
	
	public List<Status> messages = new ArrayList<Status>();
	
	@Override
	public void sendStatus(Status status) throws Exception {
		log.info("Received status : "+status);
		messages.add(status);
	}
}