package eu.h2020.symbiote.fm.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FederationRepository extends MongoRepository<FederationObject, String> {

	FederationObject getById(String id);

}
