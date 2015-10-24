package org.jslain.trains.train.manager.provider.handlers;

public class AssignmentHandler extends BaseHandler {

	@Override
	public boolean proceed() {
		getTrainDto().targetSegment = getObservation().assignment;
		
		return getNavigationHandler().updateTrip(
				getTrainDto(), 
				getTrackManager(),
				getTrainController());
		
		
	}

	public String toString(){
		return String.format("AssignmentHandler Train: %s\tTrack: %s", 
				getTrainDto().name,
				getObservation().assignment);
	}

}
