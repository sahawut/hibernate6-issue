package com.example.hibernate.model;

import com.example.hibernate.vault.VaultType;
import com.example.hibernate.vault.VaultValue;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CompositeType;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Encounter extends IdObject {
	@Embedded
	@AttributeOverride(name = "id", column = @Column(name = "identifier_id"))
	@AttributeOverride(name = "hash", column = @Column(name = "identifier_hash"))
	@CompositeType(VaultType.class)
	private VaultValue identifier = new VaultValue();
	public void setIdentifier(String rawValue, Instant referenceDate) {
		String value = StringUtils.trimToNull(rawValue);
		identifier = VaultValue.setValue(identifier, value, referenceDate);
	}
	public VaultValue getIdentifier() {
		return this.identifier;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "encounter")
	private Set<Observation> observations = new HashSet<>();
	public Set<Observation> getObservations() {
		return this.observations;
	}
	public void setObservations(Set<Observation> observations) {
		this.observations = observations;
	}
}
