package com.example.hibernate.vault;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.util.Objects;

public class VaultType implements CompositeUserType<VaultValue> {
	public static class EmbeddableMapper {
		String hash;
		Long id;
	}
	@Override
	public Object getPropertyValue(VaultValue component, int property) throws HibernateException {
		return switch (property) {  // Hibernate sorts the properties by their names alphabetically
			case 0 -> component.getHashValue();
			case 1 -> component.getVaultId();
			default -> null;
		};
	}
	@Override
	public VaultValue instantiate(ValueAccess values, SessionFactoryImplementor sessionFactory) {
		final String hash = values.getValue(0, String.class);
		final Long id = values.getValue(1, Long.class);
		return new VaultValue(id, hash);
	}
	@Override public Class<?> embeddable() { return EmbeddableMapper.class; }

	@Override public Class<VaultValue> returnedClass() { return VaultValue.class; }

	@Override public boolean equals(VaultValue x, VaultValue y) { return Objects.equals(x, y); }
	@Override public int hashCode(VaultValue x) { return Objects.hashCode(x); }
	@Override public boolean isMutable() { return true; }  // Yes, a VaultValue is stateful and we have to make it mutable.
	@Override public VaultValue deepCopy(VaultValue value) {
		if (value == null) return null;
		return new VaultValue(value);
	}
	@Override public Serializable disassemble(VaultValue value) { throw new UnsupportedOperationException(); }
	@Override public VaultValue assemble(Serializable cached, Object owner) { throw new UnsupportedOperationException(); }
	@Override public VaultValue replace(VaultValue detached, VaultValue managed, Object owner) {
		return deepCopy(detached);
	}
}
