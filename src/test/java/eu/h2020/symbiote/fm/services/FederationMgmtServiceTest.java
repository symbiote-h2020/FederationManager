package eu.h2020.symbiote.fm.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import eu.h2020.symbiote.core.model.Federation;
import eu.h2020.symbiote.fm.repositories.FederationRepository;

@RunWith(SpringRunner.class)
public class FederationMgmtServiceTest {

	@Mock
	private FederationRepository repository;

	@Mock
	private FederationAMQPService msgHandler;

	@InjectMocks
	private final FederationMgmtService service = new FederationMgmtService();

	@Test
	public void testProcessCreate() throws Exception {
		Federation fed = new Federation();
		fed.setId("123");

		Mockito.when(repository.exists(Mockito.anyString())).thenReturn(false);

		service.processUpdate(fed);

		Mockito.verify(repository, Mockito.times(1)).exists(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(1)).publishCreated(fed);
		Mockito.verify(msgHandler, Mockito.times(0)).publishUpdated(Mockito.any());
		Mockito.verify(msgHandler, Mockito.times(0)).publishDeleted(Mockito.any());
	}

	@Test
	public void testProcessUpdate() throws Exception {
		Federation fed = new Federation();
		fed.setId("123");

		Mockito.when(repository.exists(Mockito.anyString())).thenReturn(true);

		service.processUpdate(fed);

		Mockito.verify(repository, Mockito.times(1)).exists(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(1)).publishUpdated(fed);
		Mockito.verify(msgHandler, Mockito.times(0)).publishCreated(Mockito.any());
		Mockito.verify(msgHandler, Mockito.times(0)).publishDeleted(Mockito.any());
	}

	@Test
	public void testProcessDelete() throws Exception {
		Federation fed = new Federation();
		fed.setId("123");

		Mockito.when(repository.exists(Mockito.anyString())).thenReturn(true);

		service.processDelete(fed.getId());

		Mockito.verify(repository, Mockito.times(1)).exists(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(1)).publishDeleted(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(0)).publishCreated(Mockito.any());
		Mockito.verify(msgHandler, Mockito.times(0)).publishUpdated(Mockito.any());
	}

	@Test
	public void testProcessDeleteNotFound() throws Exception {
		Federation fed = new Federation();
		fed.setId("123");

		Mockito.when(repository.exists(Mockito.anyString())).thenReturn(false);

		service.processDelete(fed.getId());

		Mockito.verify(repository, Mockito.times(1)).exists(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(0)).publishDeleted(fed.getId());
		Mockito.verify(msgHandler, Mockito.times(0)).publishCreated(Mockito.any());
		Mockito.verify(msgHandler, Mockito.times(0)).publishUpdated(Mockito.any());
	}
}