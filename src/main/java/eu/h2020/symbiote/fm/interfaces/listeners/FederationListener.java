package eu.h2020.symbiote.fm.interfaces.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.h2020.symbiote.fm.model.FederationHistoryResponse;
import eu.h2020.symbiote.fm.services.FederationHistoryService;

/**
 * @author RuggenthalerC
 *
 *         AMQP listener endpoints for federation requests.
 */
@Service
public class FederationListener {
	private static final Logger logger = LoggerFactory.getLogger(FederationListener.class);

	@Autowired
	private FederationHistoryService federationHistoryService;

	/**
	 * RMQ listener to consume federation history requests for given platformIds.
	 * 
	 * @param platformId
	 * @return FederationHistory
	 */
	@RabbitListener(queues = "${rabbit.queue.federation.get_federation_history}")
	public FederationHistoryResponse getFederationHistoryByPlatformId(String platformId) {
		logger.debug("Received platform ID: {}", platformId);

		FederationHistoryResponse fh = new FederationHistoryResponse(platformId);
		fh.getEvents().addAll(federationHistoryService.getFederationHistoriesById(platformId));

		return fh;
	}
}