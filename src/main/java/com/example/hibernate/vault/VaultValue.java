package com.example.hibernate.vault;

import java.time.Instant;
import java.util.Objects;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public class VaultValue {
	protected String value = null;  // plaintext to be persisted into a secure Vault storage
	protected Instant date = null;  // the date when plaintext was persisted into the secure Vault storage
	protected Long vaultId = null;  // an opaque id used to reference the plaintext persisted in the secure Vault storage
	protected String hashValue = null;  // a hash value calculated based on the plaintext
	public VaultValue() {}

	public VaultValue(Long vaultId, String hashValue) {
		this.vaultId = vaultId;
		this.hashValue = hashValue;
	}

	public VaultValue(VaultValue src) {
		this.vaultId = src.vaultId;
		this.hashValue = src.hashValue;
		this.value = src.value;
		this.date = src.date;
	}

	public Long getVaultId() {
		return this.vaultId;
	}

	public String getHashValue() {
		return this.hashValue;
	}

	private boolean setValue(String value, Instant date) {
		final var newHash = hash(value);
		final boolean changed = !StringUtils.equals(this.hashValue, newHash);

		this.value = value;
		this.date = date;
		this.hashValue = newHash;
		return changed;
	}

	public static VaultValue setValue(VaultValue vaultValue, String value, Instant date) {
		VaultValue retval = (vaultValue == null ? new VaultValue() : vaultValue);
		retval.setValue(value, date);
		return retval;
	}

	public static String hash(String value) {
		if (value == null)
			return "";
		return DigestUtils.sha256Hex(value.getBytes());
	}

	@Override public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof VaultValue)) return false;
		VaultValue other = (VaultValue)o;
		// For use by Hibernate dirty-checking, it is important that this test only the
		// values that are persisted in the database: vaultId and hashValue.
		if (!Objects.equals(this.vaultId, other.vaultId)) return false;
		if (!Objects.equals(this.hashValue, other.hashValue)) return false;
		return true;
	}

	@Override public int hashCode() {
		return Objects.hashCode(this.vaultId);
	}
}
