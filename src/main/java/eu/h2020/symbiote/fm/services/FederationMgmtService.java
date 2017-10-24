package eu.h2020.symbiote.fm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.h2020.symbiote.fm.repositories.FederationRepository;
import eu.h2020.symbiote.model.mim.Federation;

/**
 * @author RuggenthalerC
 *
 *         Handles the workflow if federation is created, updated, deleted.
 */
@Service
public class FederationMgmtService {
	private static final Logger logger = LoggerFactory.getLogger(FederationMgmtService.class);

	@Autowired
	private FederationRepository repository;

	@Autowired
	private FederationAMQPService msgHandler;

	/**
	 * Handle process when federation object is created/updated.
	 * 
	 * @param fed
	 *            {@link FederationEntity}
	 */
	public void processUpdate(Federation fed) {
		logger.debug("Processing update {}", fed.getId());

		if (repository.exists(fed.getId())) {
			msgHandler.publishUpdated(fed);
		} else {
			msgHandler.publishCreated(fed);
		}
		repository.save(fed);
	}

	/**
	 * Handle process if federation was deleted / removed.
	 * 
	 * @param fedId
	 */
	public void processDelete(String fedId) {
		logger.debug("Processing delete with id: {}", fedId);

		// only publish federation if relevant for platform
		if (repository.exists(fedId)) {
			repository.delete(fedId);
			msgHandler.publishDeleted(fedId);
		}
	}
}