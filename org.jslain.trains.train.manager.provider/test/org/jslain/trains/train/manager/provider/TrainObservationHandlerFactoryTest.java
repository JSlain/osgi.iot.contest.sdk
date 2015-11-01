package org.jslain.trains.train.manager.provider;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.jslain.trains.train.manager.provider.handlers.AssignmentHandler;
import org.jslain.trains.train.manager.provider.handlers.LocatedHandler;
import org.jslain.trains.train.manager.provider.handlers.NoopHandler;
import org.jslain.trains.train.manager.provider.handlers.SignalHandler;
import org.junit.Before;
import org.junit.Test;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.Observation.Type;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class TrainObservationHandlerFactoryTest {

	private TrackForTrain mockTrackManager;
	
	private INavigationHandler mockNavHandler;

	private TrainController mockTrainController;
	
	private SignalManager mockSignalManager;
	
	private TrainDto trainDto;
	
	private TrainObservationHandlerFactory underTest;
	
	@Before
	public void setup(){
		mockTrackManager = mock(TrackForTrain.class);
		mockNavHandler = mock(INavigationHandler.class);
		mockTrainController = mock(TrainController.class);
		mockSignalManager = mock(SignalManager.class);
		trainDto = new TrainDto();
		
		underTest = new TrainObservationHandlerFactory(
				mockTrackManager, 
				trainDto,
				mockNavHandler,
				mockTrainController,
				mockSignalManager);
	}
	
	@Test
	public void createTypes(){
		testType(Type.LOCATED, LocatedHandler.class);
		testType(Type.ASSIGNMENT, AssignmentHandler.class);
		testType(Type.SIGNAL, SignalHandler.class);
		testType(Type.EMULATOR_TRAIN_WRONG_SWITCH, NoopHandler.class);
	}
	
	private void testType(Type type, Class<?> clazz){
		Observation observation = newObservation(type);
		ITrainObservationHandler result = underTest.create(observation);
		
		assertNotNull(result);
		
		assertThat(result, instanceOf(clazz));
		assertSame(mockTrackManager, result.getTrackManager());
		assertSame(trainDto, result.getTrainDto());
		assertSame(mockNavHandler, result.getNavigationHandler());
		assertSame(observation, result.getObservation());
		assertSame(mockTrainController, result.getTrainController());
		
	}
	
	private Observation newObservation(Type type){
		Observation toReturn = new Observation();
		toReturn.type = type;
		
		return toReturn;
	}
}
