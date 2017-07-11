package eu.h2020.symbiote.fm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.h2020.symbiote.fm.repositories.FederationObject;
import eu.h2020.symbiote.fm.repositories.FederationRepository;

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
	 *            {@link FederationObject}
	 */
	public boolean processUpdate(FederationObject fed) {
		logger.debug("Processing update {}", fed.getId());

		if (repository.exists(fed.getId())) {
			msgHandler.publishUpdated(fed);
		} else {
			msgHandler.publishCreated(fed);
		}
		repository.save(fed);

		return true;
	}

	/**
	 * Handle process if federation was deleted / removed.
	 * 
	 * @param fedId
	 */
	public boolean processDelete(String fedId) {
		logger.debug("Processing delete with id: {}", fedId);

		if (repository.exists(fedId)) {
			repository.delete(fedId);
			msgHandler.publishDeleted(fedId);
		} else {
			// TODO: Handle this situation
		}

		return true;
	}
}