package eu.h2020.symbiote.fm.interfaces.listeners;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import eu.h2020.symbiote.fm.model.FederationHistory;
import eu.h2020.symbiote.fm.model.FederationHistoryResponse;
import eu.h2020.symbiote.fm.services.FederationHistoryService;

@RunWith(SpringRunner.class)
public class FederationListenerTest {

	@Mock
	private FederationHistoryService federationHistoryService;

	@InjectMocks
	private final FederationListener listener = new FederationListener();

	@Test
	public void testGetFederationHistoryByPlatformId() throws Exception {
		List<FederationHistory> fh = new ArrayList<>();
		fh.add(new FederationHistory("f-123"));
		fh.add(new FederationHistory("f-345"));

		Mockito.when(federationHistoryService.getFederationHistoriesById(Mockito.anyString())).thenReturn(fh);

		FederationHistoryResponse res = listener.getFederationHistoryByPlatformId("p-098");

		assertEquals("p-098", res.getPlatformId());
		assertEquals(2, res.getEvents().size());
		assertEquals("f-123", res.getEvents().get(0).getFederationId());
		assertEquals("f-345", res.getEvents().get(1).getFederationId());
	}
}