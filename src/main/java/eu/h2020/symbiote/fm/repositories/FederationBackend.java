package eu.h2020.symbiote.fm.repositories;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.h2020.symbiote.fm.model.FederationEvent;
import eu.h2020.symbiote.fm.model.FederationEvent.EventType;
import eu.h2020.symbiote.model.mim.Federation;

/**
 * @author RuggenthalerC
 *
 *         Handles the federation storage and event creation.
 */
@Service
public class FederationBackend {
	private static final Logger logger = LoggerFactory.getLogger(FederationBackend.class);

	@Autowired
	private FederationRepository fedRepo;

	@Autowired
	private FederationEventRepository fedEventRepo;

	/**
	 * Create and store federation object
	 * 
	 * @param fed
	 *            {@link Federation}
	 */
	public void create(Federation fed) {
		storeCreateEvents(fed);
		fedRepo.save(fed);
	}

	/**
	 * Create and store federation object
	 * 
	 * @param fed
	 *            {@link Federation}
	 */
	public void update(Federation fed) {
		if (exists(fed.getId())) {
			storeUpdateEvents(fed, fedRepo.findOne(fed.getId()));
			fedRepo.save(fed);
		}
	}

	/**
	 * Create and store federation object
	 * 
	 * @param fed
	 *            {@link Federation}
	 */
	public void delete(String fedId) {
		if (exists(fedId)) {
			storeDeleteEvents(fedRepo.findOne(fedId));
			fedRepo.delete(fedId);
		}
	}

	/**
	 * Returns all platform related Events applicable for the given platform ID.
	 * 
	 * @param platformId
	 * @return
	 */
	public List<FederationEvent> getPlatformEventsById(String platformId) {
		return this.fedEventRepo.findEventsByPlatformId(platformId);
	}

	/**
	 * Returns all federation related Events applicable for the given federation ID.
	 * 
	 * @param fedId
	 * @return
	 */
	public List<FederationEvent> getFederationEventsById(String fedId) {
		return this.fedEventRepo.findEventsByFederationId(fedId);
	}

	/**
	 * Returns all federation related Events applicable for the given list of federation IDs.
	 * 
	 * @param fedIdList
	 * @return
	 */
	public List<FederationEvent> getFederationEventsByIds(List<String> fedIdList) {
		List<FederationEvent> eventList = new ArrayList<>();

		fedIdList.forEach(fedId -> {
			eventList.addAll(getFederationEventsById(fedId));
		});

		return eventList;
	}

	public boolean exists(String fedId) {
		return fedRepo.exists(fedId);
	}

	private void storeCreateEvents(Federation newFed) {
		fedEventRepo.save(new FederationEvent(EventType.FEDERATION_CREATED, newFed.getId(), null));
		logger.debug("FEDERATION_CREATED: federation {}", newFed.getId());

		newFed.getMembers().forEach(member -> {
			fedEventRepo.save(new FederationEvent(EventType.PLATFORM_JOINED, newFed.getId(), member.getPlatformId()));
			logger.debug("PLATFORM_ADDED: federation {} Platform {}", newFed.getId(), member.getPlatformId());
		});
	}

	private void storeUpdateEvents(Federation newFed, Federation curFed) {
		List<String> curPlatforms = new ArrayList<>();
		curFed.getMembers().forEach(member -> curPlatforms.add(member.getPlatformId()));

		newFed.getMembers().forEach(member -> {
			// Check if platform member changed
			if (curPlatforms.contains(member.getPlatformId())) {
				curPlatforms.remove(member.getPlatformId());
			} else {
				// if member is not present in current object
				fedEventRepo.save(new FederationEvent(EventType.PLATFORM_JOINED, newFed.getId(), member.getPlatformId()));
				logger.debug("PLATFORM_ADDED: federation {} Platform {}", newFed.getId(), member.getPlatformId());
			}
		});

		// Members not found in newFed -> Platforms removed
		curPlatforms.forEach(platform -> {
			fedEventRepo.save(new FederationEvent(EventType.PLATFORM_LEFT, newFed.getId(), platform));
			logger.debug("PLATFORM_REMOVED: federation {} Platform {}", newFed.getId(), platform);
		});
	}

	private void storeDeleteEvents(Federation curFed) {
		curFed.getMembers().forEach(member -> {
			fedEventRepo.save(new FederationEvent(EventType.PLATFORM_LEFT, curFed.getId(), member.getPlatformId()));
			logger.debug("PLATFORM_REMOVED: federation {} Platform {}", curFed.getId(), member.getPlatformId());
		});

		fedEventRepo.save(new FederationEvent(EventType.FEDERATION_REMOVED, curFed.getId(), null));
		logger.debug("FEDERATION_DELETED: federation {}", curFed.getId());
	}
}