package eu.h2020.symbiote.fm.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;

import eu.h2020.symbiote.fm.model.FederationEvent;
import eu.h2020.symbiote.model.mim.Federation;
import eu.h2020.symbiote.model.mim.FederationMember;

@RunWith(SpringRunner.class)
public class FederationBackendTest {

	@Mock
	private FederationRepository fedRepo;

	@Mock
	private FederationEventRepository fedEventRepo;

	@InjectMocks
	private final FederationBackend service = new FederationBackend();

	@Test
	public void testExists() throws Exception {
		Mockito.when(fedRepo.exists("123")).thenReturn(false);
		assertFalse(service.exists("123"));

		Mockito.when(fedRepo.exists("456")).thenReturn(true);
		assertTrue(service.exists("456"));

		Mockito.verify(fedRepo, Mockito.times(1)).exists("123");
		Mockito.verify(fedRepo, Mockito.times(1)).exists("456");
	}

	@Test
	public void testCreate() throws Exception {
		Federation fed = createValidObject();

		service.create(fed);

		Mockito.verify(fedRepo, Mockito.times(1)).save(fed);
		Mockito.verify(fedEventRepo, Mockito.times(3)).save((FederationEvent) Mockito.any());
	}

	@Test
	public void testGetAllFederations() throws Exception {
		Mockito.when(fedRepo.findAll()).thenReturn(Lists.newArrayList(new Federation(), new Federation()));

		List<Federation> lst = service.findAllFederations();

		Mockito.verify(fedRepo, Mockito.times(1)).findAll();
		assertEquals(2, lst.size());
	}

	@Test
	public void testGetFederationEventsByIds() throws Exception {
		Mockito.when(fedEventRepo.findEventsByFederationId("123", new Sort(Sort.Direction.ASC, "dateEvent")))
				.thenReturn(Lists.newArrayList(new FederationEvent()));

		List<FederationEvent> lst = service.getFederationEventsByIds(Lists.newArrayList("123"));
		assertFalse(lst.isEmpty());
	}

	@Test
	public void testUpdateNoStateChange() throws Exception {
		Federation fed = createValidObject();
		Federation curFed = createValidObject();
		curFed.setName("New name");
		curFed.getMembers().get(0).setInterworkingServiceURL("http://new");

		Mockito.when(fedRepo.exists(fed.getId())).thenReturn(true);
		Mockito.when(fedRepo.findOne(fed.getId())).thenReturn(curFed);

		service.update(fed);

		Mockito.verify(fedRepo, Mockito.times(1)).save(fed);
		Mockito.verify(fedEventRepo, Mockito.times(0)).save((FederationEvent) Mockito.any());
	}

	@Test
	public void testUpdateAddedMember() throws Exception {
		Federation fed = createValidObject();
		Federation curFed = createValidObject();
		fed.getMembers().add(new FederationMember("qwer", "https://qwer"));

		Mockito.when(fedRepo.exists(fed.getId())).thenReturn(true);
		Mockito.when(fedRepo.findOne(fed.getId())).thenReturn(curFed);

		service.update(fed);

		Mockito.verify(fedRepo, Mockito.times(1)).save(fed);
		Mockito.verify(fedEventRepo, Mockito.times(1)).save((FederationEvent) Mockito.any());
	}

	@Test
	public void testUpdateRemovedMember() throws Exception {
		Federation fed = createValidObject();
		Federation curFed = createValidObject();

		curFed.getMembers().add(new FederationMember("qwer", "https://qwer"));
		curFed.getMembers().add(new FederationMember("uiop", "https://uiop"));

		Mockito.when(fedRepo.exists(fed.getId())).thenReturn(true);
		Mockito.when(fedRepo.findOne(fed.getId())).thenReturn(curFed);

		service.update(fed);

		Mockito.verify(fedRepo, Mockito.times(1)).save(fed);
		Mockito.verify(fedEventRepo, Mockito.times(2)).save((FederationEvent) Mockito.any());
	}

	@Test
	public void testDelete() throws Exception {
		Federation fed = createValidObject();

		Mockito.when(fedRepo.exists(fed.getId())).thenReturn(true);
		Mockito.when(fedRepo.findOne(fed.getId())).thenReturn(fed);

		service.delete(fed.getId());

		Mockito.verify(fedRepo, Mockito.times(1)).delete(fed.getId());
		Mockito.verify(fedEventRepo, Mockito.times(3)).save((FederationEvent) Mockito.any());
	}

	private Federation createValidObject() {
		Federation fed = new Federation();
		fed.setId("123");

		fed.getMembers().add(new FederationMember("abc", "https://abc"));
		fed.getMembers().add(new FederationMember("xyz", "https://xyz"));

		return fed;
	}
}