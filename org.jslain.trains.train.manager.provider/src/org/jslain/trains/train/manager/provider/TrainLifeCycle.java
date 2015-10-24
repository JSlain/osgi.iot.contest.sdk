package org.jslain.trains.train.manager.provider;

import java.util.List;

import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class TrainLifeCycle implements ITrainLifeCycle{

	
	protected static final long STARTING_OBSERVATION = -1;
	
	private TrainDto trainDto;

	private TrainController trainController;
	
	private TrackForTrain trackManager;
	
	private ITrainObservationHandlerFactory trainObservationHandlerFactory;
	
	
	private long lastObservation = STARTING_OBSERVATION;
	
	public TrainLifeCycle(
			TrainDto trainDto, 
			TrainController trainController, 
			TrackForTrain trackManager,
			ITrainObservationHandlerFactory trainObservationHandlerFactory) {
		super();
		this.trainDto = trainDto;
		this.trainController = trainController;
		this.trackManager = trackManager;
		this.trainObservationHandlerFactory = trainObservationHandlerFactory;
	}

	@Override
	public void start() {
		trackManager.registerTrain(trainDto.name, trainDto.rfid);
		
		//trainController.move(DEFAULT_SPEED);
		trainController.light(true);
	}
	
	@Override
	public void update() {
		
		List<Observation> observations = trackManager.getRecentObservations(lastObservation);
		
		boolean goalReached = false;
		
		for(Observation obs : observations){
			lastObservation = obs.id;
			
			//If train is stopping, we don't care about the rest...
			if(!goalReached){
				if(trainDto.name.equals(obs.train)){
					ITrainObservationHandler handler = trainObservationHandlerFactory.create(obs);
					goalReached = handler.proceed();
				}
			}
		}
		
		if(goalReached){
			trainDto.state = TrainState.STANDBY;
			trainDto.targetSegment = null;
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
