package eu.h2020.symbiote.fm.interfaces.rest;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
	public AuthManager(@Value("${aam.deployment.owner.username}") String componentOwnerName,
			@Value("${aam.deployment.owner.password}") String componentOwnerPassword, @Value("${symbIoTe.localaam.url}") String localAAM,
			@Value("${platform.id}") String platformId, @Value("${aam.security.KEY_STORE_FILE_NAME}") String keystoreName,
			@Value("${aam.security.KEY_STORE_PASSWORD}") String keystorePass, @Value("${symbiote.fm.security.enabled}") boolean isSecurityEnabled)
			throws SecurityHandlerException {

		this.isSecurityEnabled = isSecurityEnabled;

		if (isSecurityEnabled) {
			securityHandler = ComponentSecurityHandlerFactory.getComponentSecurityHandler(keystoreName, keystorePass, "fm@" + platformId, localAAM,
					componentOwnerName, componentOwnerPassword);
		} else {
			logger.info("Security Request validation is disabled");
		}

	}

	public void validateSecurityHeaders(HttpHeaders httpHeaders) throws SecurityRequestException {

		if (!isSecurityEnabled) {
			return;
		}

		if (httpHeaders == null) {
			throw new SecurityRequestException("HTTP header missing");
		}

		try {
			SecurityRequest securityRequest = new SecurityRequest(httpHeaders.toSingleValueMap());
			Map<String, IAccessPolicy> accessPoliciesMap = new HashMap<>();

			accessPoliciesMap.put("ComponentHomeTokenAccessPolicy",
					new ComponentHomeTokenAccessPolicy(null, SecurityConstants.CORE_AAM_INSTANCE_ID, new HashMap<>()));
			securityHandler.getSatisfiedPoliciesIdentifiers(accessPoliciesMap, securityRequest);
		} catch (InvalidArgumentsException ex) {
			throw new SecurityRequestException(ex);
		}
	}

	public class SecurityRequestException extends Exception {
		private static final long serialVersionUID = -1819679484926164364L;

		public SecurityRequestException(String msg) {
			super(msg);
		}

		public SecurityRequestException(Exception ex) {
			super(ex);
		}

	}
}