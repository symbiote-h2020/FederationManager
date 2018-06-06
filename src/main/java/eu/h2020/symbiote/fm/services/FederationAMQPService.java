package eu.h2020.symbiote.fm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
	 *            {@link Federation}
	 */
	public void publishCreated(Federation fedObj) {
		send(routingKeyFederationCreated, fedObj);
	}

	/**
	 * Publish updated federation object to topic.
	 * 
	 * @param fedObj
	 *            {@link Federation}
	 */
	public void publishUpdated(Federation fedObj) {
		send(routingKeyFederationChanged, fedObj);
	}

	/**
	 * Publish deleted federation Id to topic.
	 * 
	 * @param fedId
	 *            federation ID
	 */
	public void publishDeleted(String fedId) {
		send(routingKeyFederationDeleted, fedId);
	}

	/**
	 * Sends the given message to topic with given routing key.
	 * 
	 * @param routingKey
	 *            routing key
	 * @param fedObj
	 *            {@link Federation}
	 */
	private void send(String routingKey, Federation fedObj) {
		logger.debug("Message published with routingkey: {} and msg: {}", routingKey, fedObj);
		template.convertAndSend(federationTopic.getName(), routingKey, fedObj);
	}

	/**
	 * Sends the given message to topic with given routing key.
	 * 
	 * @param routingKey
	 *            routing key
	 * @param msg
	 *            content as string
	 */
	private void send(String routingKey, String msg) {
		logger.debug("Message published with routingkey: {} and msg: {}", routingKey, msg);
		Message message = new Message(msg.getBytes(), new MessageProperties());
		template.send(federationTopic.getName(), routingKey, message);
	}

}
