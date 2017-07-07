package eu.h2020.symbiote.fm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest({"eureka.client.enabled=false"})
public class FederationManagerApplicationTests {

	@Test
	public void contextLoads() {
	}

}