package org.jslain.trains.track.manager.provider.handlers;

import org.jslain.trains.track.manager.provider.ITrainObservationHandler;

public class ChangeHandler extends BaseHandler {

	@Override
	public void proceed() {
		System.out.println("Change");
	}

}
