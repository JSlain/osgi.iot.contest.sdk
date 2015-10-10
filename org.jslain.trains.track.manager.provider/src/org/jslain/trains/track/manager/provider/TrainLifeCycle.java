package org.jslain.trains.track.manager.provider;

import java.util.List;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainConfiguration;
import osgi.enroute.trains.train.api.TrainController;

public class TrainLifeCycle implements ITrainLifeCycle{

	protected static final int DEFAULT_SPEED = 1;
	
	protected static final long STARTING_OBSERVATION = -1;
	
	private TrainConfiguration config;

	private TrainController trainController;
	
	private TrackForTrain trackManager;
	
	private ITrainObservationHandlerFactory trainObservationHandlerFactory;
	
	
	private long lastObservation = STARTING_OBSERVATION;
	
	public TrainLifeCycle(
			TrainConfiguration config, 
			TrainController trainController, 
			TrackForTrain trackManager,
			ITrainObservationHandlerFactory trainObservationHandlerFactory) {
		super();
		this.config = config;
		this.trainController = trainController;
		this.trackManager = trackManager;
		this.trainObservationHandlerFactory = trainObservationHandlerFactory;
	}

	@Override
	public void start() {
		trackManager.registerTrain(config.name(), config.rfid());
		
		trainController.move(DEFAULT_SPEED);
		trainController.light(true);
	}
	
	@Override
	public void update() {
		
		List<Observation> observations = trackManager.getRecentObservations(lastObservation);
		
		for(Observation obs : observations){
			lastObservation = obs.id;
			
			ITrainObservationHandler handler = trainObservationHandlerFactory.create(obs);
			handler.proceed();
		}
		
	}
	
	@Override
	public void end() {
		trainController.move(0);
		trainController.light(false);
	}

	protected long getLastObservation() {
		return lastObservation;
	}

	protected void setLastObservation(long lastObservation) {
		this.lastObservation = lastObservation;
	}

	protected ITrainObservationHandlerFactory getTrainObservationHandlerFactory() {
		return trainObservationHandlerFactory;
	}

	protected void setTrainObservationHandlerFactory(ITrainObservationHandlerFactory trainObservationHandlerFactory) {
		this.trainObservationHandlerFactory = trainObservationHandlerFactory;
	}
	
	
}
