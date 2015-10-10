package org.jslain.trains.track.manager.provider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainConfiguration;
import osgi.enroute.trains.train.api.TrainController;

public class TrainLifeCycleTest {

	private TrainConfiguration mockConfig;
	private TrainController mockController;
	private TrackForTrain mockTrackManager;
	private ITrainObservationHandlerFactory mockTrainObservationHandlerFactory;
	
	private TrainLifeCycle underTest;
	
	@Before
	public void setup(){
		mockConfig = mock(TrainConfiguration.class);
		mockController = mock(TrainController.class);
		mockTrackManager = mock(TrackForTrain.class);
		mockTrainObservationHandlerFactory = mock(ITrainObservationHandlerFactory.class);
		
		underTest = new TrainLifeCycle(
				mockConfig, 
				mockController, 
				mockTrackManager,
				mockTrainObservationHandlerFactory);
	}
	
	@Test
	public void test_start(){
		String trainName = "Train Name";
		String trainRfid = "Train Rfid";
		
		when(mockConfig.name()).thenReturn(trainName);
		when(mockConfig.rfid()).thenReturn(trainRfid);
		
		underTest.start();
		
		verify(mockTrackManager).registerTrain(trainName, trainRfid);
		verify(mockController).move(TrainLifeCycle.DEFAULT_SPEED);
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
		
		
		
		Observation obs1 = new Observation();
		Observation obs2 = new Observation();
		
		obs1.id=1;
		obs2.id=2;

		ITrainObservationHandler mockHandler1 = mock(ITrainObservationHandler.class);
		ITrainObservationHandler mockHandler2 = mock(ITrainObservationHandler.class);
		
		when(mockTrackManager.getRecentObservations(TrainLifeCycle.STARTING_OBSERVATION))
		.thenReturn(Arrays.asList(obs1, obs2));
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
	public void test_end(){
		underTest.end();
		
		verify(mockController).move(0);
		verify(mockController).light(false);
	}
}
