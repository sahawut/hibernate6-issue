package com.example.hibernate.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;


@MappedSuperclass
public abstract class IdObject {
	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 100, initialValue = 1000)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@Column(name = "id", updatable = false, nullable = false)
	protected Long id;
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		Long id = this.getId();
		return (id == null ? -1 : id.hashCode());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof IdObject)) return false;
		Long id = this.getId();
		if (id == null) return false;
		return id.equals(((IdObject)other).getId());
	}

	@Override
	public String toString() {
		Long id = this.getId();
		return String.format("%s(%d)", this.getClass().getSimpleName(),
				id == null ? -1 : id);
	}
}