package eu.h2020.symbiote.fm.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import eu.h2020.symbiote.cloud.federation.model.FederationHistory;
import eu.h2020.symbiote.fm.model.FederationEvent;
import eu.h2020.symbiote.fm.model.FederationEvent.EventType;
import eu.h2020.symbiote.fm.repositories.FederationBackend;

@RunWith(SpringRunner.class)
public class FederationHistoryServiceTest {

	@Mock
	private FederationBackend federationBackend;

	@InjectMocks
	private final FederationHistoryService service = new FederationHistoryService();

	@Test
	public void testGetFederationHistoriesById() throws Exception {
		List<FederationEvent> pEvents = new ArrayList<>();
		pEvents.add(createEvent(EventType.PLATFORM_JOINED, "f-1", "p-1", 11));
		pEvents.add(createEvent(EventType.PLATFORM_JOINED, "f-2", "p-1", 12));
		pEvents.add(createEvent(EventType.PLATFORM_LEFT, "f-1", "p-1", 21));
		pEvents.add(createEvent(EventType.PLATFORM_LEFT, "f-2", "p-1", 22));
		pEvents.add(createEvent(EventType.PLATFORM_JOINED, "f-3", "p-1", 33));

		List<FederationEvent> fEvents = new ArrayList<>();
		fEvents.add(createEvent(EventType.FEDERATION_CREATED, "f-1", null, 1));
		fEvents.add(createEvent(EventType.FEDERATION_CREATED, "f-2", null, 2));
		fEvents.add(createEvent(EventType.FEDERATION_CREATED, "f-3", null, 3));
		fEvents.add(createEvent(EventType.FEDERATION_REMOVED, "f-1", null, 100));
		fEvents.add(createEvent(EventType.FEDERATION_REMOVED, "f-3", null, 300));

		Mockito.when(federationBackend.getPlatformEventsById(Mockito.anyString())).thenReturn(pEvents);
		Mockito.when(federationBackend.getFederationEventsByIds(Mockito.anyListOf(String.class))).thenReturn(fEvents);

		List<FederationHistory> hl = service.getFederationHistoriesById("p-1");

		assertEquals(3, hl.size());

		for (FederationHistory h : hl) {
			if ("f-1".equals(h.getFederationId())) {
				verifyFederationHistory("f-1", new Date(1), new Date(100), new Date(11), new Date(21), h);
			} else if ("f-2".equals(h.getFederationId())) {
				verifyFederationHistory("f-2", new Date(2), null, new Date(12), new Date(22), h);
			} else if ("f-3".equals(h.getFederationId())) {
				verifyFederationHistory("f-3", new Date(3), new Date(300), new Date(33), null, h);
			} else {
				fail("Federation Id " + h.getFederationId() + " not expected");
			}
		}
	}

	@Test
	public void testGetFederationHistoriesByIdOwnRejoined() throws Exception {
		List<FederationEvent> fEvents = new ArrayList<>();
		fEvents.add(createEvent(EventType.FEDERATION_CREATED, "f-1", null, 1));
		fEvents.add(createEvent(EventType.FEDERATION_REMOVED, "f-1", null, 100));

		fEvents.add(createEvent(EventType.FEDERATION_CREATED, "f-1", null, 101));
		fEvents.add(createEvent(EventType.FEDERATION_REMOVED, "f-1", null, 200));

		fEvents.add(createEvent(EventType.FEDERATION_CREATED, "f-1", null, 201));

		List<FederationEvent> pEvents = new ArrayList<>();
		pEvents.add(createEvent(EventType.PLATFORM_JOINED, "f-1", "p-1", 11));
		pEvents.add(createEvent(EventType.PLATFORM_LEFT, "f-1", "p-1", 21));
		pEvents.add(createEvent(EventType.PLATFORM_JOINED, "f-1", "p-1", 102));
		pEvents.add(createEvent(EventType.PLATFORM_LEFT, "f-1", "p-1", 121));
		pEvents.add(createEvent(EventType.PLATFORM_JOINED, "f-1", "p-1", 211));

		Mockito.when(federationBackend.getPlatformEventsById(Mockito.anyString())).thenReturn(pEvents);
		Mockito.when(federationBackend.getFederationEventsByIds(Mockito.anyListOf(String.class))).thenReturn(fEvents);

		List<FederationHistory> hl = service.getFederationHistoriesById("p-1");

		assertEquals(3, hl.size());

		for (FederationHistory h : hl) {
			if (h.getDatePlatformJoined().equals(new Date(11))) {
				verifyFederationHistory("f-1", new Date(1), new Date(100), new Date(11), new Date(21), h);
			} else if (h.getDatePlatformJoined().equals(new Date(102))) {
				verifyFederationHistory("f-1", new Date(101), new Date(200), new Date(102), new Date(121), h);
			} else if (h.getDatePlatformJoined().equals(new Date(211))) {
				verifyFederationHistory("f-1", new Date(201), null, new Date(211), null, h);
			} else {
				fail("Element not expected");
			}
		}
	}

	@Test
	public void testGetFederationHistoriesByIdForeignRejoined() throws Exception {
		List<FederationEvent> fEvents = new ArrayList<>();
		fEvents.add(createEvent(EventType.FEDERATION_CREATED, "f-1", null, 1));

		List<FederationEvent> pEvents = new ArrayList<>();
		pEvents.add(createEvent(EventType.PLATFORM_JOINED, "f-1", "p-1", 11));
		pEvents.add(createEvent(EventType.PLATFORM_LEFT, "f-1", "p-1", 21));
		pEvents.add(createEvent(EventType.PLATFORM_JOINED, "f-1", "p-1", 102));
		pEvents.add(createEvent(EventType.PLATFORM_LEFT, "f-1", "p-1", 121));
		pEvents.add(createEvent(EventType.PLATFORM_LEFT, "f-1", "p-1", 129));
		pEvents.add(createEvent(EventType.PLATFORM_JOINED, "f-1", "p-1", 211));

		Mockito.when(federationBackend.getPlatformEventsById(Mockito.anyString())).thenReturn(pEvents);
		Mockito.when(federationBackend.getFederationEventsByIds(Mockito.anyListOf(String.class))).thenReturn(fEvents);

		List<FederationHistory> hl = service.getFederationHistoriesById("p-1");

		assertEquals(3, hl.size());

		for (FederationHistory h : hl) {
			if (h.getDatePlatformJoined().equals(new Date(11))) {
				verifyFederationHistory("f-1", new Date(1), null, new Date(11), new Date(21), h);
			} else if (h.getDatePlatformJoined().equals(new Date(102))) {
				verifyFederationHistory("f-1", new Date(1), null, new Date(102), new Date(121), h);
			} else if (h.getDatePlatformJoined().equals(new Date(211))) {
				verifyFederationHistory("f-1", new Date(1), null, new Date(211), null, h);
			} else {
				fail("Element not expected");
			}
		}

	}

	private void verifyFederationHistory(String fedId, Date fc, Date fr, Date pj, Date pl, FederationHistory actual) {
		assertEquals(fedId, actual.getFederationId());

		if (fc != null)
			assertTrue(fc.equals(actual.getDateFederationCreated()));
		else
			assertNull(actual.getDateFederationCreated());

		if (fr != null)
			assertTrue(fr.equals(actual.getDateFederationRemoved()));
		else
			assertNull(actual.getDateFederationRemoved());

		if (pj != null)
			assertTrue(pj.equals(actual.getDatePlatformJoined()));
		else
			assertNull(actual.getDatePlatformJoined());

		if (pl != null)
			assertTrue(pl.equals(actual.getDatePlatformLeft()));
		else
			assertNull(actual.getDatePlatformLeft());
	}

	private FederationEvent createEvent(EventType type, String fedId, String pId, long date) {
		FederationEvent fe = new FederationEvent(type, fedId, pId);
		fe.setDateEvent(new Date(date));
		return fe;
	}
}