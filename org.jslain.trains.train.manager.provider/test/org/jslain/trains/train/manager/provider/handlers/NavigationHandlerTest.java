package org.jslain.trains.train.manager.provider.handlers;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.jslain.trains.train.manager.provider.Constants.Direction;
import org.jslain.trains.train.manager.provider.Constants.Speed;
import org.jslain.trains.train.manager.provider.IPathCalculator;
import org.jslain.trains.train.manager.provider.IPathCalculatorFactory;
import org.jslain.trains.train.manager.provider.TrainDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.Segment.Type;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class NavigationHandlerTest {

	private NavigationHandler underTest;
	
	private TrainDto trainDto;
	
	private TrackForTrain mockTrackManager;
	
	private TrainController mockTrainController;
	
	private IPathCalculatorFactory mockPathCalculatorFactory;
	private IPathCalculator mockPathCalculator;
	
	private Map<String, Segment> segments;
	
	@Before
	public void setup(){
		mockPathCalculatorFactory = mock(IPathCalculatorFactory.class);
		mockPathCalculator = mock(IPathCalculator.class);
		
		underTest = new NavigationHandler(mockPathCalculatorFactory);

		
		trainDto = new TrainDto();
		mockTrackManager = mock(TrackForTrain.class);

		mockTrainController = mock(TrainController.class);
		
		segments = new HashMap<>();
		when(mockTrackManager.getSegments()).thenReturn(segments);
		
		when(mockPathCalculatorFactory.create(anyObject(), anyObject(), anyObject())).thenReturn(mockPathCalculator);
		
		when(mockPathCalculator.getDirection()).thenReturn(Direction.FORWARD);
		
		/**
		 *  TRACK1                         TRACK2                     TRACK1
		 *   RFID                           RFID
		 * T1_SEG1--------X01-----T2_SEG1--T2_SEG2--T2_SEG3-----X02---T1_SEG1 ...
		 *                  \                                   /
		 *                   \             TRACK3              /
		 *                    \             RFID              /
		 *                     +--T3_SEG1--T3_SEG2--T3_SEG3--+
		 */
		//         | Track  | Segment  |      Type         | From                             | To                         |
		addSegment("TRACK1", "T1_SEG1", Type.LOCATOR,       "X02",                              new String[]{"X01"});
		addSegment("TRACK1", "X01",     Type.SWITCH,        "T1_SEG1",                          new String[]{"T2_SEG1", "T3_SEG1"});
		addSegment("TRACK2", "T2_SEG1", Type.STRAIGHT,      "X01",                              new String[]{"T2_SEG2"});
		addSegment("TRACK2", "T2_SEG2", Type.LOCATOR,       "T2_SEG1",                          new String[]{"T2_SEG3"});
		addSegment("TRACK2", "T2_SEG3", Type.SIGNAL,        "T2_SEG2",                          new String[]{"X02"});
		addSegment("TRACK3", "T3_SEG1", Type.STRAIGHT,      "T1_SEG2",                          new String[]{"T3_SEG2"});
		addSegment("TRACK3", "T3_SEG2", Type.LOCATOR,       "T3_SEG1",                          new String[]{"T3_SEG3"});
		addSegment("TRACK3", "T3_SEG3", Type.SIGNAL,        "T3_SEG2",                          new String[]{"X02"});
		addSegment("TRACK1", "X02",     Type.SWITCH,        new String[]{"T2_SEG3", "T3_SEG3"}, new String[]{"T1_SEG1"});
		
	}
	
	@Test(timeout=1000L)
	public void test_noCurrentLocation_noCrash(){
		trainDto.currentLocation = null;
		trainDto.targetSegment = "T2_SEG1";
		
		underTest.updateTrip(
				trainDto, 
				mockTrackManager, 
				mockTrainController);
	}
	
	@Test(timeout=1000L)
	public void test_noGoalSelected_noCrash(){
		trainDto.currentLocation = "T1_SEG1";
		trainDto.targetSegment = null;
		
		underTest.updateTrip(
				trainDto, 
				mockTrackManager, 
				mockTrainController);
	}
	
	@Test(timeout=1000L)
	public void test_updateTrip_stopWhenGoalReached(){
		trainDto.currentLocation = "T1_SEG1";
		trainDto.targetSegment = "T1_SEG1";
		
		
		underTest.updateTrip(
				trainDto, 
				mockTrackManager,
				mockTrainController);
		
		verify(mockTrainController).move(Speed.IDLE.value);
	}
	
	@Test(timeout=1000L)
	public void test_moveForeward(){
		trainDto.currentLocation = "T2_SEG1";
		trainDto.targetSegment = "T2_SEG3";
		
		when(mockPathCalculator.shouldRequestAccess()).thenReturn(false);
		
		underTest.updateTrip(
				trainDto, 
				mockTrackManager,
				mockTrainController);
		
		
		ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
		verify(mockTrainController).move(captor.capture());
		
		assertThat(captor.getValue(), is(greaterThan(0)));
	}
	
	@Test(timeout=1000L)
	public void test_requestAccess(){
		trainDto.currentLocation = "T1_SEG1";
		trainDto.targetSegment = "T2_SEG1";
		
		when(mockPathCalculator.shouldRequestAccess()).thenReturn(true);
		
		when(mockPathCalculator.getSegmentWeNeedAccessTo()).thenReturn("T2_SEG1");
		
		when(mockTrackManager.requestAccessTo(
				trainDto.name, 
				"T1_SEG1", 
				"T2_SEG1")).thenReturn(true);
		
		underTest.updateTrip(
				trainDto, 
				mockTrackManager,
				mockTrainController);
		
		ArgumentCaptor<Integer> captor1 = ArgumentCaptor.forClass(Integer.class);
		verify(mockTrainController, times(2)).move(captor1.capture());
		
		assertThat(captor1.getAllValues().get(0), is(equalTo(0)));
		assertThat(captor1.getAllValues().get(1), is(greaterThan(0)));
		
	}
	
	@Test(timeout=1000L)
	public void test_requestAccess_refused(){
		trainDto.currentLocation = "X01";
		trainDto.targetSegment = "T1_SEG1";
		
		when(mockPathCalculator.shouldRequestAccess()).thenReturn(true);
		
		when(mockPathCalculator.getSegmentWeNeedAccessTo()).thenReturn("T2_SEG1", "T3_SEG1");
		
		when(mockTrackManager.requestAccessTo(
				trainDto.name, 
				"X01", 
				"T2_SEG1")).thenReturn(false);
		
		// pathCalculator.excludePossibility("T2_SEG1")
		
		when(mockTrackManager.requestAccessTo(
				trainDto.name, 
				"X01", 
				"T3_SEG1")).thenReturn(true);
		
		underTest.updateTrip(
				trainDto, 
				mockTrackManager,
				mockTrainController);
	
		ArgumentCaptor<Integer> captor1 = ArgumentCaptor.forClass(Integer.class);
		verify(mockTrainController, times(2)).move(captor1.capture());
		
		verify(mockPathCalculator).excludePossibility("T2_SEG1");
		
		assertThat(captor1.getAllValues().get(0), is(equalTo(0)));
		assertThat(captor1.getAllValues().get(1), is(greaterThan(0)));
		

	}
	
	
	private void addSegment(String trackName, String segmentName, Segment.Type type, String from, String[] to){
		addSegment(trackName,
				segmentName,
				type,
				new String[]{from},
				to);
	}
	
	private void addSegment(String trackName, String segmentName, Segment.Type type, String[] from, String[] to){
		Segment toReturn = new Segment();
		
		toReturn.id = segmentName;
		toReturn.from = from;
		toReturn.to = to;
		toReturn.track = trackName;
		toReturn.type = type;
		
		segments.put(segmentName, toReturn);
	}
}
