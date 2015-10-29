package org.jslain.trains.train.manager.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jslain.trains.train.manager.provider.Constants.Direction;
import org.osgi.dto.DTO;

import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.Segment.Type;

public class DefaultPathCalculator implements IPathCalculator{

	private String from;
	private String to;
	private Map<String, Segment> map;
	private Set<String> tracksDenied;
	
	public DefaultPathCalculator(String from, String to, Map<String, Segment> map) {
		this.from = from;
		this.to = to;
		this.map = map;
		tracksDenied = new HashSet<>();
	}

	@Override
	public Direction getDirection() {
		return Direction.FORWARD;
	}
	
	@Override
	public String getSegmentWeNeedAccessTo() {
		if(from == null){
			return null;
		}
		
		boolean found = false;
		boolean allPathExplored = false;
		
		PathExplorer pathFound = null;
		
		Map<Set<String>, PathExplorer> explorers = new HashMap<>();
		
		PathExplorer defaultPath = new PathExplorer();
		defaultPath.current = map.get(from);
		
		explorers.put(defaultPath.switches, defaultPath);
		
		while(!found && !allPathExplored){
			Collection<Set<String>> toRemove = new ArrayList<>();
			Collection<PathExplorer> toAdd = new ArrayList<>();
			
			for(Set<String> keys : explorers.keySet()){
				PathExplorer explorer = explorers.get(keys);
				
				boolean removed = false;
				for(String to : explorer.current.to){					
					if(explorer.alreadyVisited.contains(to)){
						toRemove.add(keys);
						removed = true;
					}
					
				}
				
				if(!removed){
					if(explorer.current.type == Type.SWITCH){
						//We remove the current
						toRemove.add(keys);
						
						//We create 2 new paths with each 'to'
						for(String to : explorer.current.to){
							Segment segTo = map.get(to);
							if(!tracksDenied.contains(segTo.track)){
								PathExplorer explorerToAdd = new PathExplorer();
								explorerToAdd.switches.addAll(explorer.switches);
								explorerToAdd.switches.add(to);
								explorerToAdd.alreadyVisited.addAll(explorer.alreadyVisited);
								explorerToAdd.alreadyVisited.add(to);
								explorerToAdd.current = map.get(to);
								explorerToAdd.firstLocatorAfterFirstSwitch = explorer.firstLocatorAfterFirstSwitch;
								
								if(explorerToAdd.firstLocatorAfterFirstSwitch == null
										&& Segment.Type.LOCATOR == explorerToAdd.current.type){
									explorerToAdd.firstLocatorAfterFirstSwitch = explorerToAdd.current.id;
								}
								
								toAdd.add(explorerToAdd);
								
								if(explorerToAdd.current.id.equals(this.to)){
									pathFound = explorerToAdd;
									found = true;
								}
							}
						}
					}
					else{
						explorer.alreadyVisited.add(explorer.current.id);
						explorer.current = map.get(explorer.current.to[0]);
						
						if(explorer.current.id.equals(this.to)){
							pathFound = explorer;
							found = true;
						}
						
						if(explorer.firstLocatorAfterFirstSwitch == null
								&& Segment.Type.LOCATOR == explorer.current.type){
							explorer.firstLocatorAfterFirstSwitch = explorer.current.id;
						}
					}
				}
			}
			
			toRemove.forEach(e -> explorers.remove(e));
			toAdd.forEach(e -> explorers.put(e.switches, e));
		}
		
		return pathFound.firstLocatorAfterFirstSwitch;
	}
	
	private static class PathExplorer extends DTO{
		Set<String> switches = new HashSet<>();
		Set<String> alreadyVisited = new HashSet<>();
		
		Segment current;
		String firstLocatorAfterFirstSwitch;
	}
	
	@Override
	public boolean shouldRequestAccess() {
		if(from == null){
			return false;
		}
		
		Segment current = map.get(from);
		
		boolean found = false;
		while(!found){
			
			current = map.get(current.to[0]);
			if(current.type == Type.LOCATOR){
				return false;
			}else if(current.type == Type.SWITCH){
				return true;
			}
			
			
		}
		
		return false;
	}
	
	@Override
	public void excludePossibility(String track) {
		tracksDenied.add(track);
	}
}
