package org.jslain.trains.train.manager.provider;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class TrainLifeCycleTest {

	private static final String TRAIN_RFID = "Train Rfid";
	private static final String TRAIN_NAME = "Train Name";
	
	private int ids;
	
	private TrainDto trainDto;
	private TrainController mockController;
	private TrackForTrain mockTrackManager;
	private ITrainObservationHandlerFactory mockTrainObservationHandlerFactory;
	
	private TrainLifeCycle underTest;
	
	@Before
	public void setup(){
		trainDto = new TrainDto();
		trainDto.name = TRAIN_NAME;
		trainDto.rfid = TRAIN_RFID;
		
		mockController = mock(TrainController.class);
		mockTrackManager = mock(TrackForTrain.class);
		mockTrainObservationHandlerFactory = mock(ITrainObservationHandlerFactory.class);
		
		ids = 1;
		
		underTest = new TrainLifeCycle(
				trainDto, 
				mockController, 
				mockTrackManager,
				mockTrainObservationHandlerFactory);

	}
	
	@Test
	public void test_start(){
		underTest.start();
		
		verify(mockTrackManager).registerTrain(TRAIN_NAME, TRAIN_RFID);
		//verify(mockController).move(TrainLifeCycle.DEFAULT_SPEED);
		verify(mockController).light(true);
	}
	
	@Test
	public void test_update_noObservations(){
		when(mockTrackManager.getRecentObservations(TrainLifeCycle.STARTING_OBSERVATION))
			.thenReturn(new ArrayList<>());
		
		underTest.update();
		
		verify(mockTrackManager).getRecentObservations(TrainLifeCycle.STARTING_OBSERVATION);
	}
	
	@Test
	public void test_update_2observations(){
		Observation obs1 = newObservation();
		Observation obs2 = newObservation();

		ITrainObservationHandler mockHandler1 = mock(ITrainObservationHandler.class);
		ITrainObservationHandler mockHandler2 = mock(ITrainObservationHandler.class);
		
		mockRecentObservations(obs1, obs2);
		
		when(mockTrainObservationHandlerFactory.create(obs1)).thenReturn(mockHandler1);
		when(mockTrainObservationHandlerFactory.create(obs2)).thenReturn(mockHandler2);
		
		underTest.update();
		
		assertEquals(2, underTest.getLastObservation());
		verify(mockTrainObservationHandlerFactory).create(obs1);
		verify(mockTrainObservationHandlerFactory).create(obs2);
		
		verify(mockHandler1).proceed();
		verify(mockHandler2).proceed();
	}
	
	@Test
	public void test_update_UnrelatedObservation(){
		Observation observation = newObservation("Unrelated train");

		mockRecentObservations(observation);
		
		underTest.update();
		
		verify(mockTrainObservationHandlerFactory, never())
		.create(observation);
	}
	
	private Observation newObservation(){
		return newObservation(TRAIN_NAME);
	}
	
	private Observation newObservation(String trainName){
		Observation toReturn = new Observation();
		toReturn.id = ids++;
		toReturn.train = trainName;
		
		return toReturn;
	}
	
	private void mockRecentObservations(Observation... observations){
		when(mockTrackManager.getRecentObservations(TrainLifeCycle.STARTING_OBSERVATION))
		.thenReturn(Arrays.asList(observations));
	}
	
	@Test
	public void test_end(){
		underTest.end();
		
		verify(mockController).move(0);
		verify(mockController).light(false);
	}
}
