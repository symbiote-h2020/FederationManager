package eu.h2020.symbiote.fm.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import eu.h2020.symbiote.fm.services.FederationService;
import eu.h2020.symbiote.fm.utils.Utils;
import eu.h2020.symbiote.model.mim.Federation;

@RunWith(SpringRunner.class)
@WebMvcTest(FederationController.class)
public class FederationControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private FederationService service;

	@Test
	public void testCreateFederation() throws Exception {
		Federation fed = new Federation();
		fed.setId("123");

		mvc.perform(post("/fm/federations/").contentType(MediaType.APPLICATION_JSON).content(Utils.convertObjectToJson(fed))).andExpect(status().isOk());
	}

	@Test
	public void testUpdateFederation() throws Exception {

		Federation fed = new Federation();
		fed.setId("123");

		mvc.perform(delete("/fm/federations/123")).andExpect(status().isOk());
	}

	@Test
	public void testDeleteFederation() throws Exception {
		mvc.perform(delete("/fm/federations/123")).andExpect(status().isOk());
	}

}