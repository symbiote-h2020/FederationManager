package eu.h2020.symbiote.fm.interfaces.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.security.ComponentSecurityHandlerFactory;
import eu.h2020.symbiote.security.accesspolicies.IAccessPolicy;
import eu.h2020.symbiote.security.accesspolicies.common.singletoken.ComponentHomeTokenAccessPolicy;
import eu.h2020.symbiote.security.commons.SecurityConstants;
import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;
import eu.h2020.symbiote.security.communication.payloads.SecurityRequest;
import eu.h2020.symbiote.security.handler.IComponentSecurityHandler;

/**
 * @author RuggenthalerC
 *
 *         Validating Security Request from core.
 */
@Component
public class AuthManager {

	private static final Logger logger = LoggerFactory.getLogger(AuthManager.class);
	private IComponentSecurityHandler securityHandler = null;

	private final boolean isSecurityEnabled;

	@Autowired
	public AuthManager(@Value("${symbIoTe.component.username}") String componentOwnerName,
			@Value("${symbIoTe.component.password}") String componentOwnerPassword, @Value("${symbIoTe.localaam.url}") String aamAddress,
			@Value("${symbIoTe.component.clientId}") String clientId, @Value("${symbIoTe.component.keystore.path}") String keystoreName,
			@Value("${symbIoTe.component.keystore.password}") String keystorePass, @Value("${symbIoTe.aam.integration}") boolean isSecurityEnabled)
			throws SecurityHandlerException {
		this.isSecurityEnabled = isSecurityEnabled;

		logger.debug("componentOwnerName={}; componentOwnerPassword={}; aamAddress={}; clientId={}; keystoreName={}; keystorePass={}; isSecurityEnabled={}",
				componentOwnerName, componentOwnerPassword, aamAddress, clientId, keystoreName, keystorePass, isSecurityEnabled);

		if (isSecurityEnabled) {
			securityHandler = ComponentSecurityHandlerFactory.getComponentSecurityHandler(keystoreName, keystorePass, clientId, aamAddress, componentOwnerName,
					componentOwnerPassword);
		} else {
			logger.info("Security Request validation is disabled");
		}
	}

	/**
	 * Validate given http header to verify that issuer is core.
	 * 
	 * @param httpHeaders
	 *            security parameters
	 * @return ResponseEntity (@see ResponseEntity)
	 */
	public ResponseEntity<?> validateSecurityHeaders(HttpHeaders httpHeaders) {
		if (!isSecurityEnabled) {
			return buildResponseEntity(HttpStatus.OK, new HttpHeaders(), "Security disabled");
		}

		if (httpHeaders == null) {
			return buildResponseEntity(HttpStatus.BAD_REQUEST, new HttpHeaders(), "HTTP header missing");
		}

		try {
			SecurityRequest securityRequest = new SecurityRequest(httpHeaders.toSingleValueMap());
			Set<String> sadisfiedPolicies = securityHandler.getSatisfiedPoliciesIdentifiers(buildPolicies(), securityRequest);

			if (!sadisfiedPolicies.isEmpty()) {
				return buildResponseEntity(HttpStatus.OK, new HttpHeaders(), securityHandler.generateServiceResponse());
			}

			return buildResponseEntity(HttpStatus.UNAUTHORIZED, new HttpHeaders(), securityHandler.generateServiceResponse());
		} catch (InvalidArgumentsException | SecurityHandlerException ex) {
			logger.warn("Caught exception while verifying security headers", ex);
			return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, new HttpHeaders(), ex.getMessage());
		}
	}

	private Map<String, IAccessPolicy> buildPolicies() {
		Map<String, IAccessPolicy> policies = new HashMap<>();

		ComponentHomeTokenAccessPolicy policy;
		try {
			policy = new ComponentHomeTokenAccessPolicy(SecurityConstants.CORE_AAM_INSTANCE_ID, "administration", new HashMap<>());
			policies.put("administrationPolicy", policy);
		} catch (InvalidArgumentsException e) {
			logger.warn("buildPolicies failed", e);
		}
		return policies;
	}

	private ResponseEntity<?> buildResponseEntity(HttpStatus httpStatus, HttpHeaders httpHeaders, String securityResponse) {
		httpHeaders.put(SecurityConstants.SECURITY_RESPONSE_HEADER, Arrays.asList(securityResponse));
		return new ResponseEntity<>(null, httpHeaders, httpStatus);
	}
}