package org.jslain.trains.train.manager.provider;

import org.jslain.trains.train.manager.provider.handlers.AssignmentHandler;
import org.jslain.trains.train.manager.provider.handlers.LocatedHandler;
import org.jslain.trains.train.manager.provider.handlers.NoopHandler;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class TrainObservationHandlerFactory implements ITrainObservationHandlerFactory {

	private final TrackForTrain trackManager;
	
	private final TrainDto trainDto;
	
	private final INavigationHandler navigationHandler;
	
	private final TrainController trainController;
	
	public TrainObservationHandlerFactory(
			TrackForTrain trackManager, 
			TrainDto trainDto,
			INavigationHandler navigationHandler,
			TrainController trainController) {
		super();
		this.trackManager = trackManager;
		this.trainDto = trainDto;
		this.navigationHandler = navigationHandler;
		this.trainController = trainController;
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
		default: 
			toReturn = new NoopHandler();
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
