package org.jslain.trains.train.manager.provider.handlers;

import org.jslain.trains.train.manager.provider.Constants.Speed;
import org.jslain.trains.train.manager.provider.INavigationHandler;
import org.jslain.trains.train.manager.provider.IPathCalculator;
import org.jslain.trains.train.manager.provider.IPathCalculatorFactory;
import org.jslain.trains.train.manager.provider.TrainDto;
import org.jslain.trains.train.manager.provider.TrainState;

import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;

public class NavigationHandler implements INavigationHandler {

	private IPathCalculatorFactory pathCalculatorFactory;
	
	public NavigationHandler(IPathCalculatorFactory pathCalculatorFactory){
		this.pathCalculatorFactory = pathCalculatorFactory;
	}
	
	@Override
	public boolean updateTrip(
			TrainDto trainDto, 
			TrackForTrain trackManager,
			TrainController trainController) {
		
		boolean goalReached = isGoalReached(trainDto);
		if(goalReached){
			changeState(trainDto, trainController, TrainState.STOPPING);
		}else{
			IPathCalculator calculator = pathCalculatorFactory.create(
					trainDto.currentLocation, 
					trainDto.targetSegment, 
					trackManager.getSegments());
			
			if(calculator.shouldRequestAccess()){
				changeState(trainDto, trainController, TrainState.STOPPING);
				
				String nextSegment = calculator.getSegmentWeNeedAccessTo();
				while(!trackManager.requestAccessTo(trainDto.name, trainDto.currentLocation, nextSegment)){
					calculator.excludePossibility(nextSegment);
					
					nextSegment = calculator.getSegmentWeNeedAccessTo();
				}

				changeState(trainDto, trainController, TrainState.MOVING);
				
			}else{
				changeState(trainDto, trainController, TrainState.MOVING);
			}
		}
		
		return goalReached;
	}
	
	private boolean isGoalReached(TrainDto trainDto){
		return trainDto.currentLocation != null && trainDto.currentLocation.equals(trainDto.targetSegment);
	}
	
	private void changeState(
			
			TrainDto trainDto,
			TrainController trainController, 
			TrainState state){
		trainDto.state = state;
		
		switch(state){
		case STOPPING:
		case STANDBY:
			trainController.move(Speed.IDLE.value);
			trainController.light(false);
			break;
		case MOVING:
			trainController.move(Speed.SPEED_3.value);
			trainController.light(true);
			break;
		}
	}
	
}
