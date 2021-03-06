package eu.h2020.symbiote.fm;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;

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

	@Value("${spring.data.mongodb.host:localhost}")
	private String mongoHost;

	@Override
	protected String getDatabaseName() {
		return "symbiote-cloud-fm-database";
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.setConnectTimeout(1 * 1000).setReadTimeout(10 * 1000).build();
	}

	@Bean
	public TopicExchange federationUpdatesTopic(@Value("${rabbit.exchange.federation}") String exchange,
			@Value("${rabbit.exchange.federation.durable}") Boolean durable, @Value("${rabbit.exchange.federation.autodelete}") Boolean autoDelete) {
		return new TopicExchange(exchange, durable, autoDelete);
	}

	@Bean
	public Queue federationHistoryQueue(@Value("${rabbit.queue.federation.get_federation_history}") String queue) {
		return new Queue(queue);
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient();
	}

    @Bean
    @Override
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(new MongoClient(mongoHost), getDatabaseName());
    }

}