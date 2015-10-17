package org.jslain.trains.train.manager.provider.handlers;

public class LocatedHandler extends BaseHandler {

	@Override
	public boolean proceed() {
		getTrainDto().currentLocation = getObservation().segment;
		
		boolean toReturn = false;
		
		if(getTrainDto().targetSegment != null){
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
