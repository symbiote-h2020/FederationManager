package eu.h2020.symbiote.fm.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import eu.h2020.symbiote.fm.services.FederationService;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.security.commons.SecurityConstants;

@RunWith(SpringRunner.class)
@WebMvcTest(FederationController.class)
@TestPropertySource(locations = "classpath:test.properties")
public class FederationControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private FederationService service;

	@MockBean
	private AuthManager authManager;

	@MockBean
	private DataSynchronization dataSynchronization;

	@Test
	public void testCreateFederation() throws Exception {
		Federation fed = new Federation();
		fed.setId("123");

		Mockito.when(authManager.validateSecurityHeaders(Mockito.any())).thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
		mvc.perform(post("/fm/federations/").contentType(MediaType.APPLICATION_JSON).content(convertObjectToJson(fed)).headers(generateHeaders()))
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdateFederation() throws Exception {

		Federation fed = new Federation();
		fed.setId("123");

		Mockito.when(authManager.validateSecurityHeaders(Mockito.any())).thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
		mvc.perform(delete("/fm/federations/123").headers(generateHeaders())).andExpect(status().isOk());
	}

	@Test
	public void testDeleteFederation() throws Exception {
		Mockito.when(authManager.validateSecurityHeaders(Mockito.any())).thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
		mvc.perform(delete("/fm/federations/123").headers(generateHeaders())).andExpect(status().isOk());
	}

	private String convertObjectToJson(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		return mapper.writeValueAsString(obj);
	}

	private HttpHeaders generateHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(SecurityConstants.SECURITY_CREDENTIALS_TIMESTAMP_HEADER, "1500000000");
		headers.add(SecurityConstants.SECURITY_CREDENTIALS_SIZE_HEADER, "1");
		headers.add(SecurityConstants.SECURITY_CREDENTIALS_HEADER_PREFIX + "1",
				"{\"token\":\"token\"," + "\"authenticationChallenge\":\"authenticationChallenge\"," + "\"clientCertificate\":\"clientCertificate\","
						+ "\"clientCertificateSigningAAMCertificate\":\"clientCertificateSigningAAMCertificate\","
						+ "\"foreignTokenIssuingAAMCertificate\":\"foreignTokenIssuingAAMCertificate\"}");
		return headers;
	}
}