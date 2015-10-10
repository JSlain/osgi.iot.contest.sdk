package org.jslain.trains.track.manager.provider;

import osgi.enroute.trains.cloud.api.TrackForTrain;

public interface ITrainObservationHandler {

	void setTrackManager(TrackForTrain trackManager);
	
	TrackForTrain  getTrackManager();
	
	void setTrainManager(JSlainTrainManagerImpl trainManager);
	
	JSlainTrainManagerImpl getTrainManager();
	
	void proceed();
}
