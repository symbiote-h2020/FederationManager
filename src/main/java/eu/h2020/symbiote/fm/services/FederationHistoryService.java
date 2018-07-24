package eu.h2020.symbiote.fm.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
	 *            platform ID
	 * @return list of {@link FederationHistory}
	 */
	public List<FederationHistory> getFederationHistoriesById(String platformId) {
		List<FederationHistory> events = new ArrayList<>();

		List<FederationEvent> pEvents = this.federationBackend.getPlatformEventsById(platformId);
		HashMap<String, List<FederationEvent>> pfList = new HashMap<>();

		// map platform events to Federation ID
		pEvents.forEach(pEvent -> {
			if (!pfList.containsKey(pEvent.getFederationId())) {
				pfList.put(pEvent.getFederationId(), new ArrayList<FederationEvent>());
			}
			pfList.get(pEvent.getFederationId()).add(pEvent);
		});

		// load all relevant federation events
		List<FederationEvent> fedEvents = this.federationBackend.getFederationEventsByIds(new ArrayList(pfList.keySet()));

		for (int i = 0; i < fedEvents.size(); i++) {
			FederationEvent fedEvent = fedEvents.get(i);

			if (fedEvent.getEventType().equals(FederationEvent.EventType.FEDERATION_CREATED)) {
				FederationHistory fh = new FederationHistory(fedEvent.getFederationId());
				fh.setDateFederationCreated(fedEvent.getDateEvent());

				for (int ii = i; ii < fedEvents.size(); ii++) {
					FederationEvent fedCloseEvent = fedEvents.get(ii);

					if (fh.getFederationId().equals(fedCloseEvent.getFederationId())
							&& fedCloseEvent.getEventType().equals(FederationEvent.EventType.FEDERATION_REMOVED)) {
						fh.setDateFederationRemoved(fedCloseEvent.getDateEvent());
						break;
					}
				}

				events.add(fh);
			}
		}

		// add platform specific infos
		for (FederationHistory event : events) {
			List<FederationEvent> pList = pfList.get(event.getFederationId());

			// add platform events to object
			for (FederationEvent pFe : pList) {
				if (pFe.getEventType().equals(FederationEvent.EventType.PLATFORM_JOINED)
						&& isWithinTime(event.getDateFederationCreated(), event.getDateFederationRemoved(), pFe.getDateEvent())) {
					event.setDatePlatformJoined(pFe.getDateEvent());
				} else if (pFe.getEventType().equals(FederationEvent.EventType.PLATFORM_LEFT)
						&& isWithinTime(event.getDateFederationCreated(), event.getDateFederationRemoved(), pFe.getDateEvent())) {
					event.setDatePlatformLeft(pFe.getDateEvent());
				}
			}

		}

		return events;
	}

	private boolean isWithinTime(Date from, Date to, Date check) {
		return from.before(check) && (to == null || to.after(check));
	}
}