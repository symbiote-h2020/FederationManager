package eu.h2020.symbiote.fm.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

// FIXME: Has to replaced with Library Version of FederationObject 

/**
 * @author RuggenthalerC
 *
 *         Main object to represent one federation.
 */
@Document(collection = "federations")
public class FederationObject {

	@Id
	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

	@Field("public")
	@JsonProperty("public")
	private boolean isPublic = true;

	@JsonProperty("slaDefinition")
	private String slaDefinition;

	@JsonProperty("members")
	private List<String> members = new ArrayList<>();

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public boolean isPublic() {
		return this.isPublic;
	}

	public String getSlaDefinition() {
		return this.slaDefinition;
	}

	public List<String> getMembers() {
		return this.members;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void setSlaDefinition(String slaDefinition) {
		this.slaDefinition = slaDefinition;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}
}
