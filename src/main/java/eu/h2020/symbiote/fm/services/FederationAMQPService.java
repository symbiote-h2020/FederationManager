package eu.h2020.symbiote.fm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.h2020.symbiote.fm.repositories.FederationObject;
import eu.h2020.symbiote.fm.utils.Utils;

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
	public void publishCreated(FederationObject fedObj) {
		send(routingKeyFederationCreated, fedObj);
	}

	/**
	 * Publish updated federation object to topic.
	 * 
	 * @param fedObj
	 */
	public void publishUpdated(FederationObject fedObj) {
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
	 * @param msg
	 */
	private void send(String routingKey, FederationObject fedObj) {
		try {
			send(routingKey, Utils.convertObjectToJson(fedObj));
		} catch (JsonProcessingException e) {
			logger.error("JSON conversion failed", e);
		}
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
