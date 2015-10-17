package org.jslain.trains.train.manager.provider.handlers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.jslain.trains.train.manager.provider.INavigationHandler;
import org.jslain.trains.train.manager.provider.TrainDto;
import org.jslain.trains.train.manager.provider.handlers.LocatedHandler;
import org.junit.Before;
import org.junit.Test;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class LocatedHandlerTest {

	private LocatedHandler underTest;
	
	private Observation observation;
	
	private TrainDto trainDto;
	
	private INavigationHandler mockNavigationHandler;
	
	private TrackForTrain mockTrackManager;
	
	private TrainController mockTrainController;
	
	private Map<String, Segment> segments;
	
	@Before
	public void setup(){
		underTest = new LocatedHandler();
		
		observation = new Observation();
		underTest.setObservation(observation);
		
		trainDto = new TrainDto();
		underTest.setTrainDto(trainDto);
		
		mockNavigationHandler = mock(INavigationHandler.class);
		underTest.setNavigationHandler(mockNavigationHandler);
		
		mockTrackManager = mock(TrackForTrain.class);
		underTest.setTrackManager(mockTrackManager);
		
		mockTrainController = mock(TrainController.class);
		underTest.setTrainController(mockTrainController);
		
		segments = new HashMap<>();
		when(mockTrackManager.getSegments()).thenReturn(segments);
	}
	
	@Test
	public void test_proceed(){
		trainDto.targetSegment = "";
		observation.segment = "SEGMENT";
		
		Segment segment = new Segment();
		segment.id = "SEGMENT";
		segments.put("SEGMENT", segment);
		
		underTest.proceed();
		
		assertSame(segment.id, trainDto.currentLocation);
		verify(mockNavigationHandler).updateTrip(
				trainDto, 
				mockTrackManager,
				mockTrainController);
	}
}
