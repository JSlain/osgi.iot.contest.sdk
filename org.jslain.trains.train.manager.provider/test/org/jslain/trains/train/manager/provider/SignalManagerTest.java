package org.jslain.trains.train.manager.provider;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SignalManagerTest {

	private static final String NOT_RED_LIGHT_SEGMENT = "SEGMENT_1";
	private static final String RED_LIGHT_SEGMENT = "RED_LIGHT_SEGMENT";
	private static final String ANOTHER_RED_LIGHT_SEGMENT = "ANOTHER_RED_LIGHT_SEGMENT";
	private SignalManager underTest;

	@Before
	public void setup(){
		underTest = new SignalManager();
		
	}
	
	@Test
	public void noRedLight_isRedLight_no(){
		boolean result = underTest.isRedLight(NOT_RED_LIGHT_SEGMENT);
		
		assertFalse(result);
	}
	
	@Test
	public void redLight_isRedLight_yes(){
		underTest.addRedLight(RED_LIGHT_SEGMENT);
		
		boolean result = underTest.isRedLight(RED_LIGHT_SEGMENT);
		
		assertTrue(result);
	}
	
	@Test
	public void wrongRedLightSet_isRedLight_no(){
		underTest.addRedLight(ANOTHER_RED_LIGHT_SEGMENT);
		
		boolean result = underTest.isRedLight(RED_LIGHT_SEGMENT);
		
		assertFalse(result);
	}
	
	@Test
	public void redLightRemoved_isRedLight_no(){
		underTest.addRedLight(RED_LIGHT_SEGMENT);
		underTest.removeRedLight(RED_LIGHT_SEGMENT);
		
		boolean result = underTest.isRedLight(RED_LIGHT_SEGMENT);
		
		assertFalse(result);
	}
}
