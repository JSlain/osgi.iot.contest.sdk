package org.jslain.trains.train.manager.provider;

import java.util.Map;

import osgi.enroute.trains.cloud.api.Segment;

public interface IPathCalculatorFactory {

	IPathCalculator create(String from, String to, Map<String, Segment> map);
}
