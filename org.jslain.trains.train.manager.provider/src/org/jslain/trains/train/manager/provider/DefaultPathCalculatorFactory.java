package org.jslain.trains.train.manager.provider;

import java.util.Map;

import osgi.enroute.trains.cloud.api.Segment;

public class DefaultPathCalculatorFactory implements IPathCalculatorFactory{

	@Override
	public IPathCalculator create(String from, String to, Map<String, Segment> map) {
		DefaultPathCalculator toReturn = new DefaultPathCalculator(from, to, map);
		
		return toReturn;
	}
}
