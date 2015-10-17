package org.jslain.trains.train.manager.provider;

import org.osgi.dto.DTO;

import osgi.enroute.trains.cloud.api.Segment;

public class TrainDto extends DTO{

	public String name;
	
	public String rfid;
	
	public String targetSegment;
	
	public String currentLocation;

	public TrainState state;
}
