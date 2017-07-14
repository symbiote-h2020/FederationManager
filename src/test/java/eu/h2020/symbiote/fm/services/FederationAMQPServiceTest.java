package eu.h2020.symbiote.fm.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import eu.h2020.symbiote.fm.repositories.FederationObject;
import eu.h2020.symbiote.fm.utils.Utils;

@RunWith(SpringRunner.class)
public class FederationAMQPServiceTest {

	@Mock
	private RabbitTemplate template;

	@Mock
	private TopicExchange federationTopic;

	@InjectMocks
	private final FederationAMQPService msgHandler = new FederationAMQPService();

	@Before
	public void setup() throws Exception {
		ReflectionTestUtils.setField(msgHandler, "routingKeyFederationCreated", "symbIoTe.federation.created");
		ReflectionTestUtils.setField(msgHandler, "routingKeyFederationChanged", "symbIoTe.federation.changed");
		ReflectionTestUtils.setField(msgHandler, "routingKeyFederationDeleted", "symbIoTe.federation.deleted");

		Mockito.when(federationTopic.getName()).thenReturn("symbIoTe.federation");
	}

	@Test
	public void testPublishCreated() throws Exception {
		FederationObject fed = new FederationObject();

		msgHandler.publishCreated(fed);

		Mockito.verify(federationTopic, Mockito.times(1)).getName();
		Mockito.verify(template, Mockito.times(1)).convertAndSend(Mockito.eq("symbIoTe.federation"), Mockito.eq("symbIoTe.federation.created"),
				Mockito.eq(Utils.convertObjectToJson(fed)));
	}

	@Test
	public void testPublishUpdated() throws Exception {
		FederationObject fed = new FederationObject();

		msgHandler.publishUpdated(fed);

		Mockito.verify(template, Mockito.times(1)).convertAndSend(Mockito.eq("symbIoTe.federation"), Mockito.eq("symbIoTe.federation.changed"),
				Mockito.eq(Utils.convertObjectToJson(fed)));
	}

	@Test
	public void testPublishDeleted() throws Exception {
		msgHandler.publishDeleted("123");

		Mockito.verify(template, Mockito.times(1)).convertAndSend(Mockito.eq("symbIoTe.federation"), Mockito.eq("symbIoTe.federation.deleted"),
				Mockito.eq("123"));
	}

}