package org.jslain.trains.train.manager.provider.handlers;

import org.jslain.trains.train.manager.provider.SignalManager;

public class SignalHandler extends BaseHandler {

	private SignalManager signalManager;
	
	public SignalHandler(SignalManager signalManager) {
		this.signalManager = signalManager;
	}

	@Override
	public boolean proceed() {
		switch(getObservation().signal){
		case GREEN:
			signalManager.removeRedLight(getObservation().segment);
			break;
		case YELLOW:
		case RED:
			signalManager.addRedLight(getObservation().segment);
			break;
		}
		
		getNavigationHandler().updateTrip(getTrainDto(), getTrackManager(), getTrainController());
		
		return false;
	}



}
