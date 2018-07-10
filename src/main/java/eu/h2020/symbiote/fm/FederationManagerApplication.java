package eu.h2020.symbiote.fm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;

import eu.h2020.symbiote.fm.interfaces.rest.DataSynchronization;

/**
 * @author RuggenthalerC
 *
 *         Main entry point to start Federation Manager.
 */
@EnableDiscoveryClient
@EnableAutoConfiguration
@SpringBootApplication
public class FederationManagerApplication {

	@Autowired
	private DataSynchronization dataSync;

	public static void main(String[] args) {
		WaitForPort.waitForServices(WaitForPort.findProperty("SPRING_BOOT_WAIT_FOR_SERVICES"));
		SpringApplication.run(FederationManagerApplication.class, args);
	}

	/**
	 * Synchronize state on startup.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		dataSync.synchronizeFederationDB();
	}
}
