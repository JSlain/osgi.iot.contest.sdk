package org.jslain.trains.train.manager.provider;

public interface Constants {

	public static enum Speed {
		IDLE(0),
		
		SPEED_1(14),
		SPEED_2(29),
		SPEED_3(43),
		;
		
		public final int value;
		
		private Speed(int value){
			this.value = value;
		}
	}
	
	public static enum Direction {
		FORWARD,
		BACKWARD;
	}
}
