package org.jslain.trains.train.manager.provider;

import java.util.HashSet;
import java.util.Set;

public class SignalManager {

	private Set<String> redLights;
	
	public SignalManager(){
		redLights = new HashSet<>();
	}
	
	public boolean isRedLight(String string) {
		return redLights.contains(string);
	}

	public void addRedLight(String redLightSegment) {
		redLights.add(redLightSegment);
	}

	public void removeRedLight(String redLightSegment) {
		redLights.remove(redLightSegment);
	}
	
	
}
