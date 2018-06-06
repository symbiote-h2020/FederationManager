package eu.h2020.symbiote.fm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.h2020.symbiote.fm.repositories.FederationBackend;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.model.mim.FederationMember;

/**
 * @author RuggenthalerC
 *
 *         Handles the workflow if federation is created, updated, deleted.
 */
@Service
public class FederationService {
	private static final Logger logger = LoggerFactory.getLogger(FederationService.class);

	@Autowired
	private FederationBackend federationBackend;

	@Autowired
	private FederationAMQPService msgHandler;

	@Value("${platform.id}")
	private String platformId;

	/**
	 * Handle process when federation object is created/updated.
	 * 
	 * @param fed
	 *            {@link Federation}
	 */
	public void processUpdate(Federation fed) {
		logger.debug("Processing update {}", fed.getId());

		if (isFederationValid(fed)) {
			if (federationBackend.exists(fed.getId())) {
				msgHandler.publishUpdated(fed);
				federationBackend.update(fed);
			} else {
				msgHandler.publishCreated(fed);
				federationBackend.create(fed);
			}

		} else {
			logger.warn("Update of federation {} is not relevant for platform {}", fed.getId(), platformId);
		}

	}

	/**
	 * Handle process if federation was deleted / removed.
	 * 
	 * @param fedId
	 *            federation ID
	 */
	public void processDelete(String fedId) {
		logger.debug("Processing delete with id: {}", fedId);

		// only publish federation if relevant for platform
		if (federationBackend.exists(fedId)) {
			federationBackend.delete(fedId);
			msgHandler.publishDeleted(fedId);
		} else {
			logger.warn("Deletion of federation {} ignored - Federation not relevant for platform", fedId, platformId);
		}
	}

	private boolean isFederationValid(Federation fed) {
		boolean isValid = false;

		for (FederationMember member : fed.getMembers()) {
			if (platformId.equals(member.getPlatformId())) {
				isValid = true;
			}
		}

		return isValid;
	}
}