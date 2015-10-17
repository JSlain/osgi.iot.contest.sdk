package org.jslain.trains.train.manager.provider.handlers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jslain.trains.train.manager.provider.INavigationHandler;
import org.jslain.trains.train.manager.provider.TrainDto;
import org.jslain.trains.train.manager.provider.handlers.AssignmentHandler;
import org.junit.Before;
import org.junit.Test;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class AssignmentHandlerTest {
	
	private AssignmentHandler underTest;
	
	private Observation observation;
	private TrainDto trainDto;
	private INavigationHandler mockNavigationHandler;
	private TrackForTrain mockTrackManager;
	private TrainController mockTrainController;
	
	@Before
	public void setup(){
		underTest = new AssignmentHandler();
		
		trainDto = new TrainDto();
		underTest.setTrainDto(trainDto);
		
		observation = new Observation();
		underTest.setObservation(observation);
		
		mockNavigationHandler = mock(INavigationHandler.class);
		underTest.setNavigationHandler(mockNavigationHandler);
		
		mockTrackManager = mock(TrackForTrain.class);
		underTest.setTrackManager(mockTrackManager);
		
		mockTrainController = mock(TrainController.class);
		underTest.setTrainController(mockTrainController);
	}
	
	@Test
	public void test_proceed(){
		observation.assignment = "Target Assignment";
		
		underTest.proceed();
		
		assertEquals("Target Assignment", trainDto.targetSegment);
		verify(mockNavigationHandler).updateTrip(
				trainDto, 
				mockTrackManager,
				mockTrainController);
	}
}
