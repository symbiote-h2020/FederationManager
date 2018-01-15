package eu.h2020.symbiote.fm;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import eu.h2020.symbiote.fm.model.FederationHistoryResponse;

@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations = "classpath:test.properties")
public class FederationManagerApplicationTests {

	@Autowired
	private RabbitTemplate template;

	@Ignore
	@Test
	public void testRPCGetFederationHistory() {
		Queue q = new Queue("symbIoTe.federation.get_federation_history");

		FederationHistoryResponse response = (FederationHistoryResponse) template.convertSendAndReceive(q.getName(), "abc");

		System.out.println("Repsonse: " + response.getPlatformId() + " - " + response.getEvents().size());
	}

}