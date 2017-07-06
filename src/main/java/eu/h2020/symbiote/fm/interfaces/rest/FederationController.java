package eu.h2020.symbiote.fm.interfaces.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.h2020.symbiote.fm.repositories.FederationObject;
import eu.h2020.symbiote.fm.services.FederationMgmtService;

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
	private FederationMgmtService federationMgmtService;

	/**
	 * Creates or updates federation object referenced by given fedId.
	 * 
	 * @param fedId
	 * @param {@link
	 * 			FederationObject}
	 * @return {@link HttpStatus}
	 */
	@PutMapping(value = "/{fedId}")
	public ResponseEntity<String> createUpdateFederation(@PathVariable("fedId") String fedId, @RequestBody FederationObject fedObj) {

		logger.debug("Create/update fed obj with id: {}", fedId, fedObj);
		federationMgmtService.processUpdate(fedObj);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Delete federation object with given fedId.
	 * 
	 * @param fedId
	 * @return {@link HttpStatus}
	 */
	@DeleteMapping(value = "/{fedId}")
	public ResponseEntity<String> deleteFederation(@PathVariable("fedId") String fedId) {

		logger.debug("Delete fed obj with id: {}", fedId);
		federationMgmtService.processDelete(fedId);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}