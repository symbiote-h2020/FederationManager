package eu.h2020.symbiote.fm.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author RuggenthalerC
 *
 *         Utils class to handle common tasks.
 *
 */
public class Utils {

	private Utils() {

	}

	/**
	 * Convert JAVA object to JSON respresentation.
	 * 
	 * @param obj
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String convertObjectToJson(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		return mapper.writeValueAsString(obj);
	}
}
