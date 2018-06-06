package eu.h2020.symbiote.fm.interfaces.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.h2020.symbiote.fm.services.FederationService;
import eu.h2020.symbiote.model.mim.Federation;

/**
 * @author RuggenthalerC
 *
 *         REST entry point for receiving federation updates from core via interworking interface.
 */
@RestController
@RequestMapping("/fm/federations")
public class FederationController {
	private static final Logger logger = LoggerFactory.getLogger(FederationController.class);

	@Autowired
	private FederationService federationService;

	@Autowired
	private AuthManager authManager;

	/**
	 * Creates or updates federation object referenced by given fedId.
	 * 
	 * @param fedObj
	 *            {@link Federation}
	 * @param httpHeaders
	 *            security headers
	 * @return status {@link HttpStatus}
	 */
	@PostMapping
	public ResponseEntity<?> createUpdateFederation(@RequestBody Federation fedObj, @RequestHeader HttpHeaders httpHeaders) {
		logger.trace("Post request to /fm/federations");
		ResponseEntity<?> resp = authManager.validateSecurityHeaders(httpHeaders);

		if (HttpStatus.OK.equals(resp.getStatusCode())) {
			logger.debug("Create/update fed obj with id: {}", fedObj.getId());
			federationService.processUpdate(fedObj);
		}

		return resp;
	}

	/**
	 * Delete federation object with given fedId.
	 * 
	 * @param fedId
	 *            federation ID
	 * @param httpHeaders
	 *            security headers
	 * @return status {@link HttpStatus}
	 */
	@DeleteMapping(value = "/{fedId}")
	public ResponseEntity<?> deleteFederation(@PathVariable("fedId") String fedId, @RequestHeader HttpHeaders httpHeaders) {
		logger.trace("Delete request to /fm/federations/" + fedId);

		ResponseEntity<?> resp = authManager.validateSecurityHeaders(httpHeaders);

		if (HttpStatus.OK.equals(resp.getStatusCode())) {
			logger.debug("Delete fed obj with id: {}", fedId);
			federationService.processDelete(fedId);
		}

		return resp;
	}
}