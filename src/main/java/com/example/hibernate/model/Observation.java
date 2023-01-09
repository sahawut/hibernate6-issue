package com.example.hibernate.model;

import com.example.hibernate.vault.VaultType;
import com.example.hibernate.vault.VaultValue;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CompositeType;

import java.time.Instant;

@Entity
public class Observation extends IdObject {
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_encounter", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_observation_has_encounter"))
	private Encounter encounter;
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}
	public Encounter getEncounter() {
		return this.encounter;
	}

	@Embedded
	@AttributeOverride(name = "id", column = @Column(name = "secure_value_id"))
	@AttributeOverride(name = "hash", column = @Column(name = "secure_value_hash"))
	@CompositeType(VaultType.class)
	private VaultValue secureValue = new VaultValue();
	public void setSecureValue(VaultValue secureValue) {
		this.secureValue = secureValue;
	}
	public void setSecureValue(String value, Instant referenceDate) {
		secureValue = VaultValue.setValue(secureValue, StringUtils.trimToNull(value), referenceDate);
	}
	public VaultValue getSecureValue() {
		return this.secureValue;
	}
}
