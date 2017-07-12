package eu.h2020.symbiote.fm.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import eu.h2020.symbiote.fm.repositories.FederationObject;

public class UtilsTest {

	@Test
	public void testConvertObjToJSON() throws Exception {

		FederationObject fed = new FederationObject();
		fed.setId("fedId");
		fed.setName("fedName");
		fed.setPublic(false);
		fed.setSlaDefinition("FedSla");
		fed.setMembers(Stream.of("mem1", "mem2").collect(Collectors.toList()));

		String serialFed = Utils.convertObjectToJson(fed);

		Assert.assertTrue(serialFed.startsWith("{"));
		Assert.assertTrue(serialFed.endsWith("}"));
		Assert.assertTrue(serialFed.contains("\"id\":" + "\"" + fed.getId() + "\""));
		Assert.assertTrue(serialFed.contains("\"name\":" + "\"" + fed.getName() + "\""));
		Assert.assertTrue(serialFed.contains("\"slaDefinition\":" + "\"" + fed.getSlaDefinition() + "\""));
		Assert.assertTrue(serialFed.contains("\"public\":" + fed.isPublic()));
	}
}