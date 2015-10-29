package org.jslain.trains.train.manager.provider.handlers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.jslain.trains.train.manager.provider.INavigationHandler;
import org.jslain.trains.train.manager.provider.TrainDto;
import org.jslain.trains.train.manager.provider.TrainState;
import org.jslain.trains.train.manager.provider.handlers.LocatedHandler;
import org.junit.Before;
import org.junit.Test;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class LocatedHandlerTest {

	private static final TrainState DEFAULT_TRAIN_STATE = TrainState.MOVING;

	private static final String SEGMENT_TOO_FAR_AWAY = "S8";

	private static final String TARGET_SEGMENT = "SEGMENT";

	private static final String WRONG_TRACK = "TRACK2";

	private static final String DEFAULT_TRACK_NAME = "TRACK_NAME";

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
		trainDto.state = DEFAULT_TRAIN_STATE;
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
		observation.segment = TARGET_SEGMENT;
		Segment segment = createSegment(TARGET_SEGMENT);
		
		underTest.proceed();
		
		assertSame(segment.id, trainDto.currentLocation);
		verify(mockNavigationHandler).updateTrip(
				trainDto, 
				mockTrackManager,
				mockTrainController);
	}

	@Test
	public void nextSegmentIsTargetButWrongLocator_whenProceed_trainSlowlyMovesBackward(){
		trainDto.targetSegment = TARGET_SEGMENT;
		trainDto.nextSegmentIsTarget = true;
		observation.segment = SEGMENT_TOO_FAR_AWAY;
		createSegment(DEFAULT_TRACK_NAME, TARGET_SEGMENT);
		createSegment(DEFAULT_TRACK_NAME, SEGMENT_TOO_FAR_AWAY);
		
		underTest.proceed();
		
		assertEquals(TrainState.SLOWLY_SEARCHING_LOCATOR_BACKWARD, trainDto.state);
	}
	
	@Test
	public void nextSegmentIsTargetButWrongLocatorAndWrongTrack_whenProceed_continueToUpdateTrip(){
		trainDto.targetSegment = TARGET_SEGMENT;
		trainDto.nextSegmentIsTarget = true;
		observation.segment = SEGMENT_TOO_FAR_AWAY;
		
		createSegment(DEFAULT_TRACK_NAME, TARGET_SEGMENT);
		createSegment(WRONG_TRACK, SEGMENT_TOO_FAR_AWAY);
		
		underTest.proceed();
		
		assertNotEquals(TrainState.SLOWLY_SEARCHING_LOCATOR_BACKWARD, trainDto.state);
	}
	
	private Segment createSegment(String segmentName){
		return createSegment(DEFAULT_TRACK_NAME, segmentName);
	}
	
	private Segment createSegment(String trackName, String segmentName){
		Segment segment = new Segment();
		segment.track = trackName;
		segment.id = segmentName;
		segments.put(segmentName, segment);
		
		return segment;
	}
}
