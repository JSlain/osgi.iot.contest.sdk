package org.jslain.trains.track.manager.provider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jslain.trains.track.manager.provider.handlers.ChangeHandler;
import org.jslain.trains.track.manager.provider.handlers.ErrorHandler;
import org.jslain.trains.track.manager.provider.handlers.LocatedHandler;

import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.Observation.Type;
import osgi.enroute.trains.cloud.api.TrackForTrain;

public class TrainObservationHandlerFactoryTest {

	private TrackForTrain mockTrackManager;
	
	private JSlainTrainManagerImpl mockTrainManager;
	
	private TrainObservationHandlerFactory underTest;
	
	@Before
	public void setup(){
		mockTrackManager = mock(TrackForTrain.class);
		mockTrainManager = mock(JSlainTrainManagerImpl.class);
		
		underTest = new TrainObservationHandlerFactory(mockTrackManager, mockTrainManager);
	}
	
	@Test
	public void createTypes(){
		testType(Type.LOCATED, LocatedHandler.class);
		testType(Type.CHANGE, ChangeHandler.class);
		testType(Type.EMULATOR_TRAIN_WRONG_SWITCH, ErrorHandler.class);
	}
	
	private void testType(Type type, Class clazz){
		ITrainObservationHandler result = underTest.create(newObservation(type));
		
		assertNotNull(result);
		
		assertThat(result, instanceOf(clazz));
		assertSame(mockTrackManager, result.getTrackManager());
		assertSame(mockTrainManager, result.getTrainManager());
	}
	
	private Observation newObservation(Type type){
		Observation toReturn = new Observation();
		toReturn.type = type;
		
		return toReturn;
	}
}
