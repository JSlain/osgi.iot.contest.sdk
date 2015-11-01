package org.jslain.trains.train.manager.provider;

import org.jslain.trains.train.manager.provider.handlers.AssignmentHandler;
import org.jslain.trains.train.manager.provider.handlers.LocatedHandler;
import org.jslain.trains.train.manager.provider.handlers.NoopHandler;
import org.jslain.trains.train.manager.provider.handlers.SignalHandler;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class TrainObservationHandlerFactory implements ITrainObservationHandlerFactory {

	private static final NoopHandler NOOP = new NoopHandler();
	
	private final TrackForTrain trackManager;
	
	private final TrainDto trainDto;
	
	private final INavigationHandler navigationHandler;
	
	private final TrainController trainController;
	
	private final SignalManager signalManager;
	
	public TrainObservationHandlerFactory(
			TrackForTrain trackManager, 
			TrainDto trainDto,
			INavigationHandler navigationHandler,
			TrainController trainController,
			SignalManager signalManager) {
		super();
		this.trackManager = trackManager;
		this.trainDto = trainDto;
		this.navigationHandler = navigationHandler;
		this.trainController = trainController;
		this.signalManager = signalManager;
		
	}



	@Override
	public ITrainObservationHandler create(Observation observation) {
		ITrainObservationHandler toReturn;
		
		switch(observation.type){
		case LOCATED:
			toReturn =  new LocatedHandler();
			break;
		case ASSIGNMENT:
			toReturn =  new AssignmentHandler();
			break;
		case SIGNAL:
			toReturn = new SignalHandler(signalManager);
			break;
		default: 
			toReturn = NOOP;
			break;
		}
		
		toReturn.setTrackManager(trackManager);
		toReturn.setTrainDto(trainDto);
		toReturn.setNavigationHandler(navigationHandler);
		toReturn.setObservation(observation);
		toReturn.setTrainController(trainController);
		
		return toReturn;
	}

}
