package eu.h2020.symbiote.fm.model;

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
		this.dateEvent = new Date();
	}

	public enum EventType {
		FEDERATION_CREATED, FEDERATION_REMOVED, PLATFORM_JOINED, PLATFORM_LEFT
	}

	@JsonProperty("event_type")
	private EventType eventType;

	@JsonProperty("date_event")
	private Date dateEvent;

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

	public Date getDateEvent() {
		return this.dateEvent;
	}

	public void setDateEvent(Date eventTime) {
		this.dateEvent = eventTime;
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
