package eu.h2020.symbiote.fm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.h2020.symbiote.fm.utils.Utils;
import eu.h2020.symbiote.model.mim.Federation;

/**
 * @author RuggenthalerC
 * 
 *         Handles the connection to platform RabbitMQ broker and publishes updates (created, updated, deleted federations) to federation topic.
 *
 */
@Service
public class FederationAMQPService {
	private static final Logger logger = LoggerFactory.getLogger(FederationAMQPService.class);

	@Value("${rabbit.routingKey.federation.created}")
	private String routingKeyFederationCreated;

	@Value("${rabbit.routingKey.federation.changed}")
	private String routingKeyFederationChanged;

	@Value("${rabbit.routingKey.federation.deleted}")
	private String routingKeyFederationDeleted;

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private TopicExchange federationTopic;

	/**
	 * Publish created federation object to topic.
	 * 
	 * @param fedObj
	 */
	public void publishCreated(Federation fedObj) {
		send(routingKeyFederationCreated, fedObj);
	}

	/**
	 * Publish updated federation object to topic.
	 * 
	 * @param fedObj
	 */
	public void publishUpdated(Federation fedObj) {
		send(routingKeyFederationChanged, fedObj);
	}

	/**
	 * Publish deleted federation Id to topic.
	 * 
	 * @param fedId
	 */
	public void publishDeleted(String fedId) {
		send(routingKeyFederationDeleted, fedId);
	}

	/**
	 * Sends the given message to topic with given routing key.
	 * 
	 * @param routingKey
	 * @param fedObj
	 */
	private void send(String routingKey, Federation fedObj) {
		logger.debug("Message published with routingkey: {} and msg: {}", routingKey, fedObj);
		template.convertAndSend(federationTopic.getName(), routingKey, fedObj);
	}

	/**
	 * Sends the given message to topic with given routing key.
	 * 
	 * @param routingKey
	 * @param msg
	 */
	private void send(String routingKey, String msg) {
		logger.debug("Message published with routingkey: {} and msg: {}", routingKey, msg);
		template.convertAndSend(federationTopic.getName(), routingKey, msg);
	}

}
