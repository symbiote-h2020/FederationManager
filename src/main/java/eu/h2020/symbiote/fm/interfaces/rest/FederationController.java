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
import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;
import eu.h2020.symbiote.security.communication.payloads.SecurityRequest;

/**
 * @author RuggenthalerC
 *
 *         REST entry point for receiving federation updates from core via interworking interface.
 */
@RestController
@RequestMapping("fm/federations")
public class FederationController {
	private static final Logger logger = LoggerFactory.getLogger(FederationController.class);

	@Autowired
	private FederationService federationService;

	// private final IComponentSecurityHandler componentSecurityHandler;

	// TODO: where to get it?
	private String aamAddress;

	// TODO: where to get it?
	private String clientId;

	// @Value("${aam.security.KEY_STORE_FILE_NAME}")
	private String keystoreName;

	// @Value("${aam.security.KEY_STORE_PASSWORD}")
	private String keystorePass;

	// @Value("${aam.deployment.owner.username}")
	private String componentOwnerName;

	// @Value("${aam.deployment.owner.password}")
	private String componentOwnerPassword;

	/*
	 * public FederationController() throws SecurityHandlerException { componentSecurityHandler =
	 * ComponentSecurityHandlerFactory.getComponentSecurityHandler(aamAddress, this.keystoreName, this.keystorePass, this.clientId, this.aamAddress, false,
	 * this.componentOwnerName, this.componentOwnerPassword); }
	 */

	/**
	 * Creates or updates federation object referenced by given fedId.
	 * 
	 * @param fedObj
	 *            {@link Federation}
	 * @param httpHeaders
	 * @return status {@link HttpStatus}
	 * @throws Exception
	 */
	@PostMapping(value = "/")
	public ResponseEntity<String> createUpdateFederation(@RequestBody Federation fedObj, @RequestHeader HttpHeaders httpHeaders) throws Exception {
		validateSecurityHeaders(httpHeaders);

		logger.debug("Create/update fed obj with id: {}", fedObj.getId());
		federationService.processUpdate(fedObj);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Delete federation object with given fedId.
	 * 
	 * @param fedId
	 * @param httpHeaders
	 * @return status {@link HttpStatus}
	 */
	@DeleteMapping(value = "/{fedId}")
	public ResponseEntity<String> deleteFederation(@PathVariable("fedId") String fedId, @RequestHeader HttpHeaders httpHeaders) throws Exception {
		validateSecurityHeaders(httpHeaders);

		logger.debug("Delete fed obj with id: {}", fedId);
		federationService.processDelete(fedId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void validateSecurityHeaders(HttpHeaders httpHeaders) throws InvalidArgumentsException {
		if (httpHeaders == null)
			throw new InvalidArgumentsException();

		SecurityRequest securityRequest = new SecurityRequest(httpHeaders.toSingleValueMap());

		// TODO: checking the securityRequest needed
		// Map<String, IAccessPolicy> accessPolicies;
		// componentSecurityHandler.getSatisfiedPoliciesIdentifiers(accessPolicies, securityRequest);
	}
}