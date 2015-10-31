package org.jslain.trains.train.manager.provider.handlers;

import org.jslain.trains.train.manager.provider.Constants.Speed;
import org.jslain.trains.train.manager.provider.INavigationHandler;
import org.jslain.trains.train.manager.provider.IPathCalculator;
import org.jslain.trains.train.manager.provider.IPathCalculatorFactory;
import org.jslain.trains.train.manager.provider.TrainDto;
import org.jslain.trains.train.manager.provider.TrainState;

import osgi.enroute.trains.cloud.api.Segment;
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
			goalReached(trainDto);
			changeState(trainDto, trainController, TrainState.STOPPING);
		}else if(trainDto.state == TrainState.SLOWLY_SEARCHING_LOCATOR_BACKWARD){
			changeState(trainDto, trainController, TrainState.SLOWLY_SEARCHING_LOCATOR_BACKWARD);
		}else{
			IPathCalculator calculator = pathCalculatorFactory.create(
					trainDto.currentLocation, 
					trainDto.targetSegment, 
					trackManager.getSegments());
			
			String nextSegment = calculator.getNextLocator();
			if(trainDto.targetSegment != null &&
					trainDto.targetSegment.equals(nextSegment)){
				trainDto.nextSegmentIsTarget = true;
			}
			
			if(calculator.shouldRequestAccess()){
				changeState(trainDto, trainController, TrainState.STOPPING);
				
				String nextSegmentWeNeedAccessTo = calculator.getSegmentWeNeedAccessTo();
				Segment segFrom = trackManager.getSegments().get(trainDto.currentLocation);
				Segment segTo = trackManager.getSegments().get(nextSegmentWeNeedAccessTo);
				
				while(!trackManager.requestAccessTo(trainDto.name, segFrom.track, segTo.track)){
					calculator.excludePossibility(segTo.track);
					
					nextSegment = calculator.getSegmentWeNeedAccessTo();
					segFrom = trackManager.getSegments().get(trainDto.currentLocation);
					segTo = trackManager.getSegments().get(nextSegment);
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
	
	private void goalReached(TrainDto trainDto){
		trainDto.nextSegmentIsTarget=false;
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
		case SLOWLY_SEARCHING_LOCATOR_BACKWARD:
			trainController.move(-Speed.SPEED_1.value);
			break;
		}
	}
	
}
