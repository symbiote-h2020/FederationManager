package eu.h2020.symbiote.fm;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by mateuszl on 30.09.2016.
 *
 * Note: to be used by components with MongoDB
 */

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories
class AppConfig extends AbstractMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "symbiote-cloud-fm-database";
	}

	@Bean
	public TopicExchange federationUpdatesTopic(@Value("${rabbit.exchange.federation}") String exchange) {
		return new TopicExchange(exchange);
	}

	@Bean
	public Queue federationHistoryQueue(@Value("${rabbit.queue.federation.get_federation_history}") String queue) {
		return new Queue(queue);
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient();
	}
}