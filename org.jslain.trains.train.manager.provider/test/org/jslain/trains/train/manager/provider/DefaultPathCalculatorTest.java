package org.jslain.trains.train.manager.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import osgi.enroute.trains.cloud.api.Segment.Type;

public class DefaultPathCalculatorTest {

	private DefaultPathCalculator underTest;
	
	private TestSegments testSegments;
	
	@Before
	public void setup(){
		testSegments = new TestSegments();
	}
	
	@Test(timeout=1000L)
	public void shouldRequestAccess_nextSignal_noNeedToGetAccess(){
		// Loop, from 1 to 5 (without curves? lol...)
		testSegments.addSegment("TRACK1", "S1", Type.LOCATOR,  "S3", "S2");
		testSegments.addSegment("TRACK1", "S2", Type.STRAIGHT, "S1", "S3");
		testSegments.addSegment("TRACK1", "S3", Type.LOCATOR,  "S2", "S4");
		testSegments.addSegment("TRACK1", "S4", Type.STRAIGHT, "S3", "S5");
		testSegments.addSegment("TRACK1", "S5", Type.LOCATOR,  "S4", "S1");
		
		underTest = new DefaultPathCalculator(
				"S1", "S5", testSegments.getSegments());
		
		boolean result = underTest.shouldRequestAccess();
		
		assertFalse(result);
	}
	
	@Test(timeout=1000L)
	public void shouldRequestAccess_nextSwitch_needToGetAccess(){
		/**
		 *   **        Switch     
		 *   S1----S2----S3-------T2_S1_L
		 *                 \
		 *                  +-----T3_S1_L
		 */
		testSegments.addSegment("TRACK1", "S1", Type.LOCATOR, "S3", "S2");
		testSegments.addSegment("TRACK1", "S2", Type.STRAIGHT, "S1", "S3");
		testSegments.addSegment("TRACK1", "S3", Type.SWITCH, "S2", "S4");
		testSegments.addSegment("TRACK2", "T2_S1_L", Type.LOCATOR, "S3", "S5");
		testSegments.addSegment("TRACK3", "T3_S1_L", Type.LOCATOR, "S4", "S1");
		
		underTest = new DefaultPathCalculator(
				"S1", "T2_S1_L", testSegments.getSegments());
		
		boolean result = underTest.shouldRequestAccess();
		
		assertTrue(result);
	}
	
	@Test(timeout=1000L)
	public void shouldRequestAccess_fromNull_doesntCrash(){
		underTest = new DefaultPathCalculator(
				null, "T2_S1", testSegments.getSegments());
		
		boolean result = underTest.shouldRequestAccess();
		
		assertFalse(result);
	}
	
	@Test(timeout=1000L)
	public void getSegmentWeNeedAccessTo_fromNull_ReturnsNull(){
		underTest = new DefaultPathCalculator(
				null, "T2_S1", testSegments.getSegments());
		
		String result = underTest.getSegmentWeNeedAccessTo();
		
		assertNull(result);
	}
	
	private void initComplexPath(){
		/**
		 * **
		 * S1L---S2----X0----------T2_S1L----T2_S2----T2_S3-----------T2_S4----------T2_S5L--X3---S0L-- (S1)
		 *               \                                                                   /
		 *                +-T3_S1--T3_S2L---X1-------T4_S1-----------T4_S2L---X2----T6_S1L--+
		 *                                   \                                /
		 *                                    +------T5_S1L--T5_S2---T5_S3L--+
		 */
		testSegments.addSegment("TRACK1", "S1L",   Type.LOCATOR,  "S0L",                          "S2");
		testSegments.addSegment("TRACK1", "S2",    Type.STRAIGHT, "S1L",                          "X0");
		testSegments.addSegment("TRACK1", "X0",    Type.SWITCH,   "S2",                           new String[]{"T2_S1L", "T3_S1"});
		testSegments.addSegment("TRACK2", "T2_S1L",Type.LOCATOR,  "X0",                           "T2_S2");
		testSegments.addSegment("TRACK2", "T2_S2", Type.STRAIGHT, "T2_S1",                        "T2_S3");
		testSegments.addSegment("TRACK2", "T2_S3", Type.STRAIGHT, "T2_S2",                        "T2_S4");
		testSegments.addSegment("TRACK2", "T2_S4", Type.STRAIGHT, "T2_S3",                        "T2_S5L");
		testSegments.addSegment("TRACK2", "T2_S5L",Type.LOCATOR,  "T2_S4",                        "X3");
		testSegments.addSegment("TRACK3", "T3_S1", Type.STRAIGHT, "X0",                           "T3_S2L");
		testSegments.addSegment("TRACK3", "T3_S2L",Type.LOCATOR,  "T3_S1",                        "X1");
		testSegments.addSegment("TRACK3", "X1",    Type.SWITCH,   "T3_S2L",                       new String[]{"T4_S1", "T5_S1L"});
		testSegments.addSegment("TRACK4", "T4_S1", Type.STRAIGHT, "X1",                           "T4_S2L");
		testSegments.addSegment("TRACK4", "T4_S2L",Type.LOCATOR,  "T4_S1",                        "X2");
		testSegments.addSegment("TRACK5", "T5_S1L",Type.STRAIGHT, "X1",                           "T5_S2");
		testSegments.addSegment("TRACK5", "T5_S2", Type.STRAIGHT, "T5_S1L",                       "T5_S3L");
		testSegments.addSegment("TRACK5", "T5_S3L",Type.LOCATOR,  "T5_S2",                        "X2");
		testSegments.addSegment("TRACK6", "X2",    Type.SWITCH,   new String[]{"T4_S2L", "T5_S3L"},new String[]{"T6_S1L"});
		testSegments.addSegment("TRACK6", "T6_S1L",Type.LOCATOR,  "X2",                           "X3");
		testSegments.addSegment("TRACK1", "X3",    Type.SWITCH,   new String[]{"T2_S5L", "T6_S1L"},new String[]{"S0L"});
		testSegments.addSegment("TRACK1", "S0L",   Type.LOCATOR,  "X3",                           "S1L");
		
	}
	
	@Test(timeout=1000L)
	public void getSegmentWeNeedAccessTo_shortestPath(){
		initComplexPath();
		
		underTest = new DefaultPathCalculator("S1L", "S0L", testSegments.getSegments());
		
		String result = underTest.getSegmentWeNeedAccessTo();
		
		assertEquals("T2_S1L", result);
	}
	
	@Test(timeout=1000L)
	public void getSegmentWeNeedAccessTo_shortestPath_alternate(){
		initComplexPath();
		
		underTest = new DefaultPathCalculator("S1L", "T5_S3L", testSegments.getSegments());
		
		String result = underTest.getSegmentWeNeedAccessTo();
		
		assertEquals("T3_S2L", result);
	}
	
	@Test(timeout=1000L)
	public void getSegmentWeNeedAccessTo_whenPathExcluded_alternateWayFound(){
		initComplexPath();
		underTest = new DefaultPathCalculator("S1L", "S0L", testSegments.getSegments());
		underTest.excludePossibility("TRACK2");
		
		String result = underTest.getSegmentWeNeedAccessTo();
		
		assertEquals("T3_S2L", result);
	}
	
	@Test(timeout=1000L)
	public void getNextLocator_whenStraightLine_nextReturned(){
		initComplexPath();
		underTest = new DefaultPathCalculator("T2_S1L", "S0L", testSegments.getSegments());
		
		String result = underTest.getNextLocator();
		
		assertEquals("T2_S5L", result);
	}
	
	@Test(timeout=1000L)
	public void getNextLocator_whenAlternate_nextReturned(){
		initComplexPath();
		underTest = new DefaultPathCalculator("S1L", "T5_S1L", testSegments.getSegments());
		
		String result = underTest.getNextLocator();
		
		assertEquals("T3_S2L", result);
	}

	@Test(timeout=1000L)
	public void getNextLocator_whenFarAway_nextReturned(){
		initComplexPath();
		underTest = new DefaultPathCalculator("S0L", "T2_S5L", testSegments.getSegments());
		
		String result = underTest.getNextLocator();
		
		assertEquals("S1L", result);
	}
}
