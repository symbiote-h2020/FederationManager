package eu.h2020.symbiote.fm.interfaces.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonParseException;

import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;

/**
 * @author RuggenthalerC
 *
 *         Handles and logs global REST errors
 */
@RestControllerAdvice
public class ControllersAdvice {
	private static final Logger logger = LoggerFactory.getLogger(ControllersAdvice.class);

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleInvalidJson(JsonParseException e) {
		logger.warn("Input validation failed: {}", e.getMessage());
		return "Input validation failed: Invalid JSON";
	}

	@ExceptionHandler(InvalidArgumentsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public String handleInvalidSecurityHeaders(InvalidArgumentsException e) {
		logger.warn("Security header validation failed: {}", e.getMessage());
		return "Invalid security headers";
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleServerErrors(Throwable e) {

		logger.error("Unhandled exception caught: {}", e.getMessage());
		return "Internal server error";
	}
}