package org.jslain.trains.train.manager.provider;

import java.util.HashMap;
import java.util.Map;

import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.Segment.Type;

public class TestSegments {

	private Map<String, Segment> segments;
	
	public TestSegments(){
		segments = new HashMap<>();
	}
	
	public void initPath1(){
		/**
		 *  TRACK1                         TRACK2                     TRACK1
		 *   RFID                           RFID
		 * T1_SEG1--------X01-----T2_SEG1--T2_SEG2--T2_SEG3-----X02---T1_SEG1 ...
		 *                  \                                   /
		 *                   \             TRACK3              /
		 *                    \             RFID              /
		 *                     +--T3_SEG1--T3_SEG2--T3_SEG3--+
		 */
		//         | Track  | Segment  |      Type         | From                             | To                         |
		addSegment("TRACK1", "T1_SEG1", Type.LOCATOR,       "X02",                              new String[]{"X01"});
		addSegment("TRACK1", "X01",     Type.SWITCH,        "T1_SEG1",                          new String[]{"T2_SEG1", "T3_SEG1"});
		addSegment("TRACK2", "T2_SEG1", Type.STRAIGHT,      "X01",                              new String[]{"T2_SEG2"});
		addSegment("TRACK2", "T2_SEG2", Type.LOCATOR,       "T2_SEG1",                          new String[]{"T2_SEG3"});
		addSegment("TRACK2", "T2_SEG3", Type.SIGNAL,        "T2_SEG2",                          new String[]{"X02"});
		addSegment("TRACK3", "T3_SEG1", Type.STRAIGHT,      "T1_SEG2",                          new String[]{"T3_SEG2"});
		addSegment("TRACK3", "T3_SEG2", Type.LOCATOR,       "T3_SEG1",                          new String[]{"T3_SEG3"});
		addSegment("TRACK3", "T3_SEG3", Type.SIGNAL,        "T3_SEG2",                          new String[]{"X02"});
		addSegment("TRACK1", "X02",     Type.SWITCH,        new String[]{"T2_SEG3", "T3_SEG3"}, new String[]{"T1_SEG1"});
	}
	
	public Map<String, Segment> getSegments(){
		return segments;
	}
	
	public void addSegment(String trackName, String segmentName, Segment.Type type, String from, String... to){
		addSegment(trackName,
				segmentName,
				type,
				new String[]{from},
				to);
	}
	
	public void addSegment(String trackName, String segmentName, Segment.Type type, String[] from, String[] to){
		Segment toReturn = new Segment();
		
		toReturn.id = segmentName;
		toReturn.from = from;
		toReturn.to = to;
		toReturn.track = trackName;
		toReturn.type = type;
		
		segments.put(segmentName, toReturn);
	}
}
