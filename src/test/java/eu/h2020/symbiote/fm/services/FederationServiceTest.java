package eu.h2020.symbiote.fm.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import eu.h2020.symbiote.fm.repositories.FederationBackend;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.model.mim.FederationMember;

@RunWith(SpringRunner.class)
public class FederationServiceTest {

	@Mock
	private FederationBackend repository;

	@Mock
	private FederationAMQPService msgHandler;

	@InjectMocks
	private final FederationService service = new FederationService();

	@Before
	public void setup() throws Exception {
		ReflectionTestUtils.setField(service, "platformId", "xyz");
	}

	@Test
	public void testProcessCreate() throws Exception {
		Federation fed = createValidObject();

		Mockito.when(repository.exists(Mockito.anyString())).thenReturn(false);

		service.processUpdate(fed);

		Mockito.verify(repository, Mockito.times(1)).exists(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(1)).publishCreated(fed);
		Mockito.verify(msgHandler, Mockito.times(0)).publishUpdated(Mockito.any());
		Mockito.verify(msgHandler, Mockito.times(0)).publishDeleted(Mockito.any());
	}

	@Test
	public void testProcessCreateNoMember() throws Exception {
		Federation fed = createValidObject();

		// Platform not part of federation members
		ReflectionTestUtils.setField(service, "platformId", "xyz1");

		Mockito.when(repository.exists(Mockito.anyString())).thenReturn(false);

		service.processUpdate(fed);

		Mockito.verify(repository, Mockito.times(0)).exists(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(0)).publishCreated(fed);
		Mockito.verify(msgHandler, Mockito.times(0)).publishUpdated(Mockito.any());
		Mockito.verify(msgHandler, Mockito.times(0)).publishDeleted(Mockito.any());
	}

	@Test
	public void testProcessUpdate() throws Exception {
		Federation fed = createValidObject();
		Mockito.when(repository.exists(Mockito.anyString())).thenReturn(true);

		service.processUpdate(fed);

		Mockito.verify(repository, Mockito.times(1)).exists(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(1)).publishUpdated(fed);
		Mockito.verify(msgHandler, Mockito.times(0)).publishCreated(Mockito.any());
		Mockito.verify(msgHandler, Mockito.times(0)).publishDeleted(Mockito.any());
	}

	@Test
	public void testProcessDelete() throws Exception {
		Federation fed = createValidObject();

		Mockito.when(repository.exists(Mockito.anyString())).thenReturn(true);

		service.processDelete(fed.getId());

		Mockito.verify(repository, Mockito.times(1)).exists(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(1)).publishDeleted(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(0)).publishCreated(Mockito.any());
		Mockito.verify(msgHandler, Mockito.times(0)).publishUpdated(Mockito.any());
	}

	@Test
	public void testProcessDeleteNotFound() throws Exception {
		Federation fed = createValidObject();

		Mockito.when(repository.exists(Mockito.anyString())).thenReturn(false);

		service.processDelete(fed.getId());

		Mockito.verify(repository, Mockito.times(1)).exists(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(0)).publishDeleted(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(0)).publishCreated(Mockito.any());
		Mockito.verify(msgHandler, Mockito.times(0)).publishUpdated(Mockito.any());
	}

	private Federation createValidObject() {
		Federation fed = new Federation();
		fed.setId("123");

		fed.getMembers().add(new FederationMember("abc", "https://abc"));
		fed.getMembers().add(new FederationMember("xyz", "https://xyz"));

		return fed;
	}
}