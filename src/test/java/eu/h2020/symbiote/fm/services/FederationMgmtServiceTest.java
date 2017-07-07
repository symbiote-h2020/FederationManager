package eu.h2020.symbiote.fm.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import eu.h2020.symbiote.fm.repositories.FederationObject;
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
	public void testProcessUpdate() throws Exception {
		FederationObject fed = new FederationObject();

		Mockito.when(repository.getById(Mockito.anyString())).thenReturn(fed);

		Assert.assertTrue(service.processUpdate(fed));
		Mockito.verify(repository, Mockito.times(1)).getById(Mockito.anyString());
		Mockito.verify(msgHandler, Mockito.times(1)).publishCreated(Mockito.any());
	}
}