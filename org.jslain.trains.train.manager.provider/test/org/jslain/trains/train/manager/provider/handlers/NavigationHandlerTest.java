package org.jslain.trains.train.manager.provider.handlers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jslain.trains.train.manager.provider.Constants.Direction;
import org.jslain.trains.train.manager.provider.Constants.Speed;
import org.jslain.trains.train.manager.provider.IPathCalculator;
import org.jslain.trains.train.manager.provider.IPathCalculatorFactory;
import org.jslain.trains.train.manager.provider.TestSegments;
import org.jslain.trains.train.manager.provider.TrainDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class NavigationHandlerTest {

	private NavigationHandler underTest;
	
	private TrainDto trainDto;
	
	private TrackForTrain mockTrackManager;
	
	private TrainController mockTrainController;
	
	private IPathCalculatorFactory mockPathCalculatorFactory;
	private IPathCalculator mockPathCalculator;
	
	private TestSegments testSegments;
	
	@Before
	public void setup(){
		mockPathCalculatorFactory = mock(IPathCalculatorFactory.class);
		mockPathCalculator = mock(IPathCalculator.class);
		
		underTest = new NavigationHandler(mockPathCalculatorFactory);

		
		trainDto = new TrainDto();
		mockTrackManager = mock(TrackForTrain.class);

		mockTrainController = mock(TrainController.class);
		
		testSegments = new TestSegments();
		testSegments.initPath1();
		
		when(mockTrackManager.getSegments()).thenReturn(testSegments.getSegments());
		
		when(mockPathCalculatorFactory.create(anyObject(), anyObject(), anyObject())).thenReturn(mockPathCalculator);
		
		when(mockPathCalculator.getDirection()).thenReturn(Direction.FORWARD);
		
		
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
				"TRACK1", 
				"TRACK2")).thenReturn(true);
		
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
				"TRACK1", 
				"TRACK2")).thenReturn(false);
		
		// pathCalculator.excludePossibility("T2_SEG1")
		
		when(mockTrackManager.requestAccessTo(
				trainDto.name, 
				"TRACK1", 
				"TRACK3")).thenReturn(true);
		
		
		underTest.updateTrip(
				trainDto, 
				mockTrackManager,
				mockTrainController);
	
		ArgumentCaptor<Integer> captor1 = ArgumentCaptor.forClass(Integer.class);
		verify(mockTrainController, times(2)).move(captor1.capture());
		
		verify(mockPathCalculator).excludePossibility("TRACK2");
		
		assertThat(captor1.getAllValues().get(0), is(equalTo(0)));
		assertThat(captor1.getAllValues().get(1), is(greaterThan(0)));
		

	}
	
	
}
