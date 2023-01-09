package com.example.hibernate.vault;

public class MockVaultInterceptor {
	private static long nextId = 1L;
	private MockVaultInterceptor() {}

	public static void mockPersistToVault(VaultValue value) {
		if (value != null && value.vaultId != null) {
			value.vaultId = nextId++;
		}
	}
}
