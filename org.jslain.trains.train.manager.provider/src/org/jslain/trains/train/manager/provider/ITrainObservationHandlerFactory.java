package org.jslain.trains.train.manager.provider;

import osgi.enroute.trains.cloud.api.Observation;

public interface ITrainObservationHandlerFactory {

	ITrainObservationHandler create(Observation observation);
}
