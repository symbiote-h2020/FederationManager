package eu.h2020.symbiote.fm.interfaces.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:test.properties")
public class ControllersAdviceTest {

	@Test
	public void testHandleInvalidJson() throws Exception {
		ControllersAdvice adv = new ControllersAdvice();
		assertEquals("JSON deserialization failed", adv.handleInvalidJson(new JsonParseException(null, "exc")));
	}

	@Test
	public void testHandleServerErrors() throws Exception {
		ControllersAdvice adv = new ControllersAdvice();
		assertEquals("Internal server error", adv.handleServerErrors(new Throwable()));
	}
}