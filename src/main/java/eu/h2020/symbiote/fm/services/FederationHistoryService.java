package eu.h2020.symbiote.fm.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.h2020.symbiote.cloud.federation.model.FederationHistory;
import eu.h2020.symbiote.fm.model.FederationEvent;
import eu.h2020.symbiote.fm.repositories.FederationBackend;

/**
 * @author RuggenthalerC
 *
 *         Handles the workflow if federation is created, updated, deleted.
 */
@Service
public class FederationHistoryService {
	private static final Logger logger = LoggerFactory.getLogger(FederationHistoryService.class);

	@Autowired
	private FederationBackend federationBackend;

	/**
	 * Returning all associated Federation and platform events for given platform ID
	 * 
	 * @param platformId
	 * @return
	 */
	public List<FederationHistory> getFederationHistoriesById(String platformId) {
		// XXX: Assuming right now that federation and platforms can only join+leave once!
		Map<String, FederationHistory> events = new HashMap<>();

		List<FederationEvent> pEvents = this.federationBackend.getPlatformEventsById(platformId);

		List<String> fList = new ArrayList<>();

		pEvents.forEach(pEvent -> {
			if (!fList.contains(pEvent.getFederationId())) {
				fList.add(pEvent.getFederationId());
			}

			// add history object if not exists
			if (!events.containsKey(pEvent.getFederationId())) {
				events.put(pEvent.getFederationId(), new FederationHistory(pEvent.getFederationId()));
			}

			// update history objects
			if (pEvent.getEventType().equals(FederationEvent.EventType.PLATFORM_JOINED)) {
				events.get(pEvent.getFederationId()).setDatePlatformJoined(pEvent.getDateEvent());
			} else if (pEvent.getEventType().equals(FederationEvent.EventType.PLATFORM_LEFT)) {
				events.get(pEvent.getFederationId()).setDatePlatformLeft(pEvent.getDateEvent());
			} else {
				logger.error("Platform Event not covered: {}", pEvent.getEventType());
			}
		});

		List<FederationEvent> fedEvents = this.federationBackend.getFederationEventsByIds(fList);

		fedEvents.forEach(fedEvent -> {
			// update history objects
			if (fedEvent.getEventType().equals(FederationEvent.EventType.FEDERATION_CREATED)) {
				events.get(fedEvent.getFederationId()).setDateFederationCreated(fedEvent.getDateEvent());
			} else if (fedEvent.getEventType().equals(FederationEvent.EventType.FEDERATION_REMOVED)) {
				events.get(fedEvent.getFederationId()).setDateFederationRemoved(fedEvent.getDateEvent());
			} else {
				logger.warn("Federation Event not covered: {}", fedEvent.getEventType());
			}
		});

		return new ArrayList<>(events.values());
	}
}