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
				Thread.sleep(50L);
				trainLife.update();
			}
			
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			trainLife.end();
		}
	}
}
