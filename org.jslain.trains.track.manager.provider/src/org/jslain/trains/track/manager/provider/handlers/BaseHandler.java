package org.jslain.trains.track.manager.provider.handlers;

import org.jslain.trains.track.manager.provider.ITrainObservationHandler;
import org.jslain.trains.track.manager.provider.JSlainTrainManagerImpl;

import osgi.enroute.trains.cloud.api.TrackForTrain;

public abstract class BaseHandler implements ITrainObservationHandler{
	
	private TrackForTrain trackManager;
	
	private JSlainTrainManagerImpl trainManager;
	
	@Override
	public void setTrackManager(TrackForTrain trackManager) {
		this.trackManager = trackManager;
	}
	
	public TrackForTrain getTrackManager() {
		return trackManager;
	}
	
	@Override
	public void setTrainManager(JSlainTrainManagerImpl trainManager) {
		this.trainManager = trainManager;		
	}

	@Override
	public JSlainTrainManagerImpl getTrainManager() {
		return trainManager;
	}
	
	
	
}
