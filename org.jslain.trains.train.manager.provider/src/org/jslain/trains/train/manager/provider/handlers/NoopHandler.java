package org.jslain.trains.train.manager.provider.handlers;

import osgi.enroute.trains.cloud.api.Observation;

/**
 * No operations
 * 
 * @author ghislain
 *
 */
public class NoopHandler extends BaseHandler{

	@Override
	public boolean proceed() {
		return false;
	}

}
