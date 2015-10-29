package org.jslain.trains.train.manager.provider.handlers;

import org.jslain.trains.train.manager.provider.TrainState;

import osgi.enroute.trains.cloud.api.Segment;

public class LocatedHandler extends BaseHandler {

	@Override
	public boolean proceed() {
		getTrainDto().currentLocation = getObservation().segment;
		
		boolean toReturn = false;
		
		if(getTrainDto().targetSegment != null){
			if(getTrainDto().nextSegmentIsTarget){
				Segment segTarget = getTrackManager().getSegments().get(getTrainDto().targetSegment);
				Segment segCurrent = getTrackManager().getSegments().get(getObservation().segment);
				
				if(segTarget.track.equals(segCurrent.track)){
					getTrainDto().state = TrainState.SLOWLY_SEARCHING_LOCATOR_BACKWARD;
				}
			}
			
			toReturn = getNavigationHandler().updateTrip(
					getTrainDto(), 
					getTrackManager(),
					getTrainController());
		}
		
		return toReturn;
	}

	public String toString(){
		
		return String.format("LocateHandler Train: %s\tSegment: %s", 
				getObservation().train,
				getObservation().segment);
	}
}
