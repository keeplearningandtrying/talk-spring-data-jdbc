/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schauderhaft.spring.data.jdbc.talk.aggregate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Jens Schauder
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AggregateConfiguration.class)
public class AggregateTest {

	@Autowired
	ProperWorkoutRepository workoutRepo;

	@Autowired
	ExerciseRepository exerciseRepo;


	Exercise pushUps;
	Exercise pullUps;
	Exercise burpees;


	@Before
	public void before() {

		pushUps = new Exercise("Push-ups");
		pullUps = new Exercise("Pull-ups");
		burpees = new Exercise("Burpees");

		exerciseRepo.saveAll(asList(pushUps, pullUps, burpees));
	}

	@Test
	public void testConfiguration() {
		assertThat(workoutRepo).isNotNull();
	}

	@Test
	public void simpleCrud() {

		Workout saved = createAndSaveWorkout();

		Optional<Workout> reloaded = workoutRepo.findById(saved.getId());
		assertThat(reloaded.isPresent()).isTrue();

		assertThat(reloaded.get().totalCount(pullUps)).isEqualTo(45);
		assertThat(reloaded.get().totalCount(burpees)).isEqualTo(60);
		assertThat(reloaded.get().totalCount(pushUps)).isEqualTo(0);

		saved.setName("Jens Schauder");
		workoutRepo.save(saved);

		workoutRepo.deleteById(saved.getId());

		assertThat(workoutRepo.findById(saved.getId())
				.isPresent()).isFalse();
	}

	@Test
	public void enableDirectNavigation() {

		createAndSaveWorkout();

		Iterable<Workout> all = workoutRepo.findAll();

		assertThat(all).hasSize(1);

		all.forEach(
				workout -> System.out.println(workout.getExercise(2).name.equals("Pull-Ups"))
		);


	}

	private Workout createAndSaveWorkout() {
		Workout workout = new Workout("Jens Schauder", Focus.STRENGTH);

		workout.add(5, pullUps);
		workout.add(30, burpees);

		workout.add(15, pullUps);
		workout.add(20, burpees);

		workout.add(25, pullUps);
		workout.add(10, burpees);

		return workoutRepo.save(workout);
	}

}
