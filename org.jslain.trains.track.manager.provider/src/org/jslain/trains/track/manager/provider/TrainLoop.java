package org.jslain.trains.track.manager.provider;

public class TrainLoop implements Runnable{

	private ITrainLifeCycle trainLife;
	
	public TrainLoop(ITrainLifeCycle trainLife){
		this.trainLife = trainLife;
	}
	
	@Override
	public void run() {
		try{
			trainLife.start();
			
			while(!Thread.currentThread().isInterrupted()){
				trainLife.update();
				
				Thread.currentThread().sleep(100L);
			}
			
		}catch(InterruptedException e){
			// Nothing to do, the finally will handle it...
		}finally{
			trainLife.end();
		}
	}
}
