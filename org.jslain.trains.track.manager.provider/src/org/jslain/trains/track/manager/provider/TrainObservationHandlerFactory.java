package org.jslain.trains.track.manager.provider;

import org.jslain.trains.track.manager.provider.handlers.ChangeHandler;
import org.jslain.trains.track.manager.provider.handlers.ErrorHandler;
import org.jslain.trains.track.manager.provider.handlers.LocatedHandler;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;

public class TrainObservationHandlerFactory implements ITrainObservationHandlerFactory {

	private final TrackForTrain trackManager;
	
	private final JSlainTrainManagerImpl trainManager;
	
	public TrainObservationHandlerFactory(TrackForTrain trackManager, JSlainTrainManagerImpl trainManager) {
		super();
		this.trackManager = trackManager;
		this.trainManager = trainManager;
	}



	@Override
	public ITrainObservationHandler create(Observation observation) {
		ITrainObservationHandler toReturn;
		
		switch(observation.type){
		case LOCATED:
			toReturn =  new LocatedHandler();
			break;
		case CHANGE:
			toReturn =  new ChangeHandler();
			break;
		default: 
			toReturn = new ErrorHandler();
			break;
		}
		
		toReturn.setTrackManager(trackManager);
		toReturn.setTrainManager(trainManager);
		
		return toReturn;
	}

}
