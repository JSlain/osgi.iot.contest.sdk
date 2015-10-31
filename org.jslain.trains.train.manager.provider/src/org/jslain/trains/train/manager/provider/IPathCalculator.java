package org.jslain.trains.train.manager.provider;

import org.jslain.trains.train.manager.provider.Constants.Direction;

public interface IPathCalculator {

	Direction getDirection();
	
	/**
	 * Check for the next Signal or Switch segment. If it's a switch, we must request an access.
	 * 
	 * @return
	 */
	boolean shouldRequestAccess();
	
	String getSegmentWeNeedAccessTo();

	void excludePossibility(String string);

	String getNextLocator();
	
}
