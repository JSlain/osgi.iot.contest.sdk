package org.jslain.trains.train.manager.provider;

import org.jslain.trains.train.manager.provider.Constants.Direction;

import osgi.enroute.trains.cloud.api.Segment;

public interface IPathCalculator {

	Direction getDirection();
	
	boolean shouldRequestAccess();
	
	String getSegmentWeNeedAccessTo();

	void excludePossibility(String string);
	
}
