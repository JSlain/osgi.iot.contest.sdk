package org.jslain.trains.train.manager.provider.handlers;

import org.jslain.trains.train.manager.provider.INavigationHandler;
import org.jslain.trains.train.manager.provider.ITrainObservationHandler;
import org.jslain.trains.train.manager.provider.TrainDto;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public abstract class BaseHandler implements ITrainObservationHandler{
	
	private Observation observation;
	
	private TrackForTrain trackManager;
	
	private TrainDto trainDto;
	
	private INavigationHandler navigationHandler;
	
	private TrainController trainController;
	
	@Override
	public void setTrackManager(TrackForTrain trackManager) {
		this.trackManager = trackManager;
	}
	
	public TrackForTrain getTrackManager() {
		return trackManager;
	}
	
	@Override
	public void setTrainDto(TrainDto trainDto) {
		this.trainDto = trainDto;		
	}

	@Override
	public TrainDto getTrainDto() {
		return trainDto;
	}

	public Observation getObservation() {
		return observation;
	}

	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	public INavigationHandler getNavigationHandler() {
		return navigationHandler;
	}

	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	public TrainController getTrainController() {
		return trainController;
	}

	public void setTrainController(TrainController trainController) {
		this.trainController = trainController;
	}
	
	
	
}
