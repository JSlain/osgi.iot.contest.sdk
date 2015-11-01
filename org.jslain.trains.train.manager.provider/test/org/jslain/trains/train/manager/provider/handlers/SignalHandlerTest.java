package org.jslain.trains.train.manager.provider.handlers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jslain.trains.train.manager.provider.INavigationHandler;
import org.jslain.trains.train.manager.provider.SignalManager;
import org.junit.Before;
import org.junit.Test;

import osgi.enroute.trains.cloud.api.Color;
import osgi.enroute.trains.cloud.api.Observation;

public class SignalHandlerTest {
	
	private SignalManager mockSignalManager;
	private INavigationHandler mockNavigationHandler;
	
	private Observation observation;
	
	private SignalHandler underTest;
	
	@Before
	public void setup(){
		mockSignalManager = mock(SignalManager.class);
		mockNavigationHandler = mock(INavigationHandler.class);
		
		underTest = new SignalHandler(mockSignalManager);
		
		observation = new Observation();
		observation.segment = "CURRENT_SEGMENT";
		
		underTest.setObservation(observation);
		underTest.setNavigationHandler(mockNavigationHandler);
	}
	
	@Test
	public void green_removeRedLight(){
		observation.signal = Color.GREEN;
		
		underTest.proceed();
		
		verify(mockSignalManager).removeRedLight(observation.segment);
	}
	
	@Test
	public void yellow_addRedLight(){
		observation.signal = Color.YELLOW;
		
		underTest.proceed();
		
		verify(mockSignalManager).addRedLight(observation.segment);
	}
	

	@Test
	public void red_addRedLight(){
		observation.signal = Color.RED;
		
		underTest.proceed();
		
		verify(mockSignalManager).addRedLight(observation.segment);
	}
}
