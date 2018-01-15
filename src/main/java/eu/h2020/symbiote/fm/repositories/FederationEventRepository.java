package eu.h2020.symbiote.fm.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import eu.h2020.symbiote.fm.model.FederationEvent;

/**
 * @author ruggenthalerc
 * 
 *         Maintains the states and history of each federation object.
 */
interface FederationEventRepository extends MongoRepository<FederationEvent, String> {

	@Query("{'platform_id': ?0}")
	List<FederationEvent> findEventsByPlatformId(String platformId);

	@Query("{'federation_id': ?0}")
	List<FederationEvent> findEventsByFederationId(String federationId);
}
