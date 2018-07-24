package eu.h2020.symbiote.fm.interfaces.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import eu.h2020.symbiote.fm.repositories.FederationBackend;
import eu.h2020.symbiote.fm.services.FederationService;
import eu.h2020.symbiote.model.mim.Federation;

@RunWith(SpringRunner.class)
public class DataSynchronizationTest {
	@Mock
	private FederationBackend federationBackend;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private FederationService federationService;

	@Mock
	private AuthManager authManager;

	@InjectMocks
	private final DataSynchronization service = new DataSynchronization();

	@Before
	public void setup() throws Exception {
		ReflectionTestUtils.setField(service, "administrationURL", "xyz");
		ReflectionTestUtils.setField(service, "platformId", "123");
	}

	@Test
	public void testProcessCreate() throws Exception {
		List<Federation> localList = new ArrayList<>();
		List<Federation> coreList = new ArrayList<>();

		Federation f1 = new Federation();
		f1.setId("f1");
		f1.setLastModified(new Date(123));

		Federation f2 = new Federation();
		f2.setId("f2");
		f2.setLastModified(new Date(455));

		Federation f3 = new Federation();
		f3.setId("f3");
		f3.setLastModified(new Date(123));

		Federation f4a = new Federation();
		f4a.setId("f4");
		f4a.setLastModified(new Date(1231));

		Federation f4b = new Federation();
		f4b.setId("f4");
		f4b.setLastModified(new Date(123));

		localList.add(f1);
		localList.add(f3);
		localList.add(f4a);

		coreList.add(f1);
		coreList.add(f2);
		coreList.add(f4b);

		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class),
				Mockito.any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<List<Federation>>(coreList, HttpStatus.OK));

		Mockito.when(federationBackend.findAllFederations()).thenReturn(localList);
		Mockito.when(authManager.verifyResponseHeaders(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(true);

		service.synchronizeFederationDB();

		Mockito.verify(federationService, Mockito.times(2)).processUpdate(Mockito.any(Federation.class));
		Mockito.verify(federationService, Mockito.times(0)).processUpdate(f1);
		Mockito.verify(federationService, Mockito.times(1)).processUpdate(f2);
		Mockito.verify(federationService, Mockito.times(1)).processUpdate(f4b);

		Mockito.verify(federationService, Mockito.times(1)).processDelete(Mockito.anyString());
		Mockito.verify(federationService, Mockito.times(1)).processDelete(f3.getId());
	}
}