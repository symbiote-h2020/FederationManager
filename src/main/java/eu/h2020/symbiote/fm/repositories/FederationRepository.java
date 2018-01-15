package eu.h2020.symbiote.fm.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import eu.h2020.symbiote.model.mim.Federation;

/**
 * @author ruggenthalerc
 * 
 *         MongoDB repository interface for Federation objects providing CRUD operations.
 */
interface FederationRepository extends MongoRepository<Federation, String> {
}
