package eu.h2020.symbiote.fm.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author ruggenthalerc
 * 
 *         Maintains the states and history of each federation object.
 */
public interface FederationEventRepository extends MongoRepository<FederationEvent, String> {
}
