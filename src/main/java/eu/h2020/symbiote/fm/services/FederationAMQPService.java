package eu.h2020.symbiote.fm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

	private static final String FEDERATION_TOPIC = "symbIoTe.federation";
	private static final String FEDERATION_KEY_CREATED = FEDERATION_TOPIC + ".created";
	private static final String FEDERATION_KEY_CHANGED = FEDERATION_TOPIC + ".changed";
	private static final String FEDERATION_KEY_DELETED = FEDERATION_TOPIC + ".deleted";

	@Autowired
	private RabbitTemplate template;

	/**
	 * Publish created federation object to topic.
	 * 
	 * @param fedObj
	 */
	public void publishCreated(FederationObject fedObj) {
		send(FEDERATION_KEY_CREATED, fedObj);
	}

	/**
	 * Publish updated federation object to topic.
	 * 
	 * @param fedObj
	 */
	public void publishUpdated(FederationObject fedObj) {
		send(FEDERATION_KEY_CHANGED, fedObj);
	}

	/**
	 * Publish deleted federation Id to topic.
	 * 
	 * @param fedId
	 */
	public void publishDeleted(String fedId) {
		send(FEDERATION_KEY_DELETED, fedId);
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
		template.setExchange(FEDERATION_TOPIC);
		template.convertAndSend(routingKey, msg);
	}

}
