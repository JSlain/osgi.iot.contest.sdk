package org.jslain.trains.train.manager.provider;

import java.util.Map;

import org.jslain.trains.train.manager.provider.Constants.Direction;

import osgi.enroute.trains.cloud.api.Segment;

public class DefaultPathCalculator implements IPathCalculator{

	private String from;
	private String to;
	private Map<String, Segment> map;
	
	public DefaultPathCalculator(String from, String to, Map<String, Segment> map) {
		this.from = from;
		this.to = to;
		this.map = map;
	}

	@Override
	public Direction getDirection() {
		return Direction.FORWARD;
	}
	
	@Override
	public String getSegmentWeNeedAccessTo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean shouldRequestAccess() {
		return false;
	}
	
	@Override
	public void excludePossibility(String string) {
		// TODO Auto-generated method stub
		
	}
}
