package org.jslain.trains.track.manager.provider;

/**
 * Manages the life of a train
 * 
 * @author Ghislain Nadeau
 */
public interface ITrainLifeCycle {

	/**
	 * Called once, to setup the train
	 */
	void start();
	
	/**
	 * Called in loop, like in a video-game
	 */
	void update();
	
	/**
	 * Called when the bundle is stopped.
	 */
	void end();
}
