package eu.h2020.symbiote.fm.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import eu.h2020.symbiote.fm.repositories.FederationObject;
import eu.h2020.symbiote.fm.utils.Utils;

@RunWith(SpringRunner.class)
public class FederationAMQPServiceTest {

	@Mock
	private RabbitTemplate template;

	@InjectMocks
	private final FederationAMQPService msgHandler = new FederationAMQPService();

	@Test
	public void testPublishCreated() throws Exception {
		FederationObject fed = new FederationObject();

		msgHandler.publishCreated(fed);

		Mockito.verify(template, Mockito.times(1)).setExchange("symbIoTe.federation");
		Mockito.verify(template, Mockito.times(1)).convertAndSend(Mockito.eq("symbIoTe.federation.created"), Mockito.eq(Utils.convertObjectToJson(fed)));
	}

	@Test
	public void testPublishUpdated() throws Exception {
		FederationObject fed = new FederationObject();

		msgHandler.publishUpdated(fed);

		Mockito.verify(template, Mockito.times(1)).setExchange("symbIoTe.federation");
		Mockito.verify(template, Mockito.times(1)).convertAndSend(Mockito.eq("symbIoTe.federation.changed"), Mockito.eq(Utils.convertObjectToJson(fed)));
	}

	@Test
	public void testPublishDeleted() throws Exception {
		msgHandler.publishDeleted("123");

		Mockito.verify(template, Mockito.times(1)).setExchange("symbIoTe.federation");
		Mockito.verify(template, Mockito.times(1)).convertAndSend(Mockito.eq("symbIoTe.federation.deleted"), Mockito.eq("123"));
	}

}