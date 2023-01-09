package com.example.hibernate;

import com.example.hibernate.model.Encounter;
import com.example.hibernate.model.Observation;
import com.example.hibernate.repository.EncounterRepository;
import com.example.hibernate.repository.ObservationRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static com.example.hibernate.vault.MockVaultInterceptor.mockPersistToVault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class HibernateApplicationTests {
	@Autowired EncounterRepository encounterRepository;
	@Autowired ObservationRepository observationRepository;

	//
	// Test case 1
	//

	@Test
	void saveEncounterOnceAtTheEnd_OK() {
		final Encounter encounter = makeEncounter();
		/* encounterRepository.save(encounter); */   // If Encounter is not saved here, everything is fine.

		final Observation observation = addNewObservation(encounter);

		encounterRepository.save(encounter);  // Saving the Encounter will propagate saving of Observations via one-to-many cascade.
		assertThat(observation.getId()).isNotNull();  // desired
	}

	//
	// Test case 2 - Please pay attention to this particular case; this is where we are puzzled.
	//

	@Test
	void saveEncounterWithoutSavingObservationsThenSaveEncounterAgain_NOT_OK() {
		final Encounter encounter = makeEncounter();
		encounterRepository.save(encounter);

		final Observation observation = addNewObservation(encounter);

		// Attempt re-saving the Encounter in order to propagate saving of Observations via one-to-many cascade.
		assertThatThrownBy(() -> encounterRepository.save(encounter))
			.isInstanceOf(NullPointerException.class);  // Why does PropertyAccessCompositeUserTypeImpl::getSetter() return null?

		/* encounterRepository.save(encounter); //  */           // desired
		/* assertThat(observation.getId()).isNotNull(); */   // desired
	}

	//
	// Test case 3
	//

	@Test
	void saveEncounterThenSaveObservationsThenSaveEncounterAgain_OK() {
		final Encounter encounter = makeEncounter();
		encounterRepository.save(encounter);

		final Observation observation = addNewObservation(encounter);
		observationRepository.saveAll(encounter.getObservations());  // Can we avoid saving Observations here before saving the Encounter? Saving via one-to-many cascade should work.

		encounterRepository.save(encounter);  // Re-save the Encounter in order to propagate saving of Observations via one-to-many cascade.
		assertThat(observation.getId()).isNotNull();  // desired
	}

	//
	// private helper functions
	//

	private Observation addNewObservation(Encounter encounter) {
		final Observation observation = new Observation();
		observation.setEncounter(encounter);
		encounter.getObservations().add(observation);
		observation.setSecureValue("triage notes including patient's private information blah blah blah...", Instant.now());
		mockPersistToVault(observation.getSecureValue());
		return observation;
	}

	private Encounter makeEncounter() {
		final Encounter encounter = new Encounter();
		encounter.setIdentifier("Medical-Record-Number-123456", Instant.now());
		mockPersistToVault(encounter.getIdentifier());
		return encounter;
	}
}
