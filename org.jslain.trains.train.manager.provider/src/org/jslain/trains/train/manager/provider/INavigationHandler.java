package org.jslain.trains.train.manager.provider;

import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public interface INavigationHandler {

	/**
	 * Contains the logic to calculate the path, the speed of the train and asks for permissions.
	 * 
	 * @param trainDto
	 * @param trackManager
	 * @param trainController
	 * @return true if the train reached it's location
	 */
	boolean updateTrip(
			TrainDto trainDto,
			TrackForTrain trackManager,
			TrainController trainController);
}
