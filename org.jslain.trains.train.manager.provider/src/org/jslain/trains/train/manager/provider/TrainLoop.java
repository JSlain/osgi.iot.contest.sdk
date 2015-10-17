package org.jslain.trains.train.manager.provider;

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
			}
			
		}finally{
			trainLife.end();
		}
	}
}
