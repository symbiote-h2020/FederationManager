package eu.h2020.symbiote.fm.interfaces.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.h2020.symbiote.fm.repositories.FederationBackend;

/**
 * @author RuggenthalerC
 *
 *         AMQP listener endpoints for federation requests.
 */
@Service
public class FederationListeners {
	private static final Logger logger = LoggerFactory.getLogger(FederationListeners.class);

	@Autowired
	private FederationBackend federationBackend;

	/**
	 * RMQ listener to consume federation history requests for given platformIds.
	 * 
	 * @param platformId
	 * @return FederationHistory
	 */
	@RabbitListener(queues = "${rabbit.queue.federation.get_federation_history}")
	public String getFederationHistoryByPlatformId(String platformId) {
		// TODO: get fed history and return object
		logger.info(platformId);
		return platformId + "-" + System.currentTimeMillis();
	}
}