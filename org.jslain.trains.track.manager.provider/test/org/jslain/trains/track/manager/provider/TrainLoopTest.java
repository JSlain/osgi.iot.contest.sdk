package org.jslain.trains.track.manager.provider;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TrainLoopTest {

	private TrainLoop underTest;
	
	private ITrainLifeCycle mockLifeCycle;
	
	@Before
	public void setup(){
		mockLifeCycle = Mockito.mock(ITrainLifeCycle.class);
		
		underTest = new TrainLoop(mockLifeCycle);
	}
	
	@Test
	public void testRun() throws Exception{
		Thread thread = new Thread(underTest);

		thread.start();
		Thread.sleep(100L);
		thread.interrupt();
		
		verify(mockLifeCycle).start();
		verify(mockLifeCycle, atLeastOnce()).update();
		verify(mockLifeCycle).end();
	}
}
