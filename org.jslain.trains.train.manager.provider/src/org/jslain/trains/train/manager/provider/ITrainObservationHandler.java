package org.jslain.trains.train.manager.provider;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public interface ITrainObservationHandler {
	
	/**
	 * The method that contains the logic of the handler
	 * 
	 * @return true if the train reached it's goal
	 */
	boolean proceed();
	
	// Next methods are only get/set of useful services.
	
	
	void setTrackManager(TrackForTrain trackManager);
	
	TrackForTrain  getTrackManager();
	
	void setTrainDto(TrainDto trainDto);
	
	TrainDto getTrainDto();
	
	Observation getObservation();
	
	void setObservation(Observation observation);
	
	INavigationHandler getNavigationHandler();
	
	void setNavigationHandler(INavigationHandler navigationHandler);
	
	TrainController getTrainController();
	
	void setTrainController(TrainController trainController);
}
