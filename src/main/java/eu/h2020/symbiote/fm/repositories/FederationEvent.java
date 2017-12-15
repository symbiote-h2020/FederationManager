package eu.h2020.symbiote.fm.repositories;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author RuggenthalerC
 *
 *         Object to track federation change events
 */
public class FederationEvent {

	public FederationEvent() {

	}

	public FederationEvent(EventType type, String fedId, String platformId) {
		this.eventType = type;
		this.federationId = fedId;
		this.platformId = platformId;
	}

	public enum EventType {
		FEDERATION_CREATED, FEDERATION_DELETED, PLATFORM_ADDED, PLATFORM_REMOVED
	}

	@JsonProperty("event_type")
	private EventType eventType;

	@JsonProperty("event_time")
	private Date eventTime = new Date();

	@JsonProperty("federation_id")
	private String federationId;

	@JsonProperty("platform_id")
	private String platformId;

	public EventType getEventType() {
		return this.eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Date getEventTime() {
		return this.eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getFederationId() {
		return this.federationId;
	}

	public void setFederationId(String federationId) {
		this.federationId = federationId;
	}

	public String getPlatformId() {
		return this.platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}
}
