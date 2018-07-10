package eu.h2020.symbiote.fm.interfaces.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import eu.h2020.symbiote.fm.repositories.FederationBackend;
import eu.h2020.symbiote.fm.services.FederationService;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.security.commons.SecurityConstants;

/**
 * @author RuggenthalerC
 *
 *         Synchronize data information with core Admin via REST.
 */
@Service
public class DataSynchronization {
	private static final Logger logger = LoggerFactory.getLogger(DataSynchronization.class);

	@Value("${symbIoTe.core.administration.url}")
	private String administrationURL;

	@Value("${platform.id}")
	private String platformId;

	@Autowired
	private AuthManager authManager;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private FederationService federationService;

	@Autowired
	private FederationBackend federationBackend;

	/**
	 * Load all current federation objects for platform.
	 */
	public void synchronizeFederationDB() {
		try {
			String url = administrationURL + "?platformId=" + platformId;
			ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authManager.generateRequestHeaders()), List.class);

			if (!authManager.verifyResponseHeaders("Administration", SecurityConstants.CORE_AAM_INSTANCE_ID, resp.getHeaders())) {
				logger.warn("Response Header verification failed.");
				return;
			}

			if (resp.getStatusCode().equals(HttpStatus.OK) && resp.getBody() != null && !resp.getBody().isEmpty()) {
				List<Federation> coreFedList = resp.getBody();

				Map<String, Federation> localFedList = new HashMap<>();
				federationBackend.findAllFederations().forEach(curFed -> {
					localFedList.put(curFed.getId(), curFed);
				});

				// Process create/update for all entries.
				coreFedList.forEach(fed -> {

					// remove entry from temp list
					Federation localFed = localFedList.remove(fed.getId());

					if (localFed == null || !fed.getLastModified().equals(localFed.getLastModified())) {
						federationService.processUpdate(fed);
					}
				});

				// remove inconsistent local entries
				localFedList.keySet().forEach(fedId -> {
					federationService.processDelete(fedId);
				});
			} else {
				logger.warn("Invalid response received: ", resp);
			}
		} catch (Exception e) {
			logger.warn("Fetching current Federation list from Administration failed", e);
		}
	}
}