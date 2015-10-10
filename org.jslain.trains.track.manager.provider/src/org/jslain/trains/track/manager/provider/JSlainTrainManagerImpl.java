package org.jslain.trains.track.manager.provider;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainConfiguration;
import osgi.enroute.trains.train.api.TrainController;

/**
 * 
 */
@Component(name = TrainConfiguration.TRAIN_CONFIGURATION_PID,
configurationPolicy = ConfigurationPolicy.REQUIRE,
immediate=true,
service=Object.class)
public class JSlainTrainManagerImpl {

	@Reference
	private TrackForTrain trackForTrain;
	
	@Reference
	private TrainController trainController;
	
	private Thread trainThread;
	
	@Activate
	public void activate(TrainConfiguration config){

		trainThread = new Thread(new TrainLoop(new TrainLifeCycle(
				config, 
				trainController, 
				trackForTrain, 
				new TrainObservationHandlerFactory(trackForTrain, this))));
		
		trainThread.start();
	}
	
	@Deactivate
	public void deactivate() {
		try{
			trainThread.interrupt();
			trainThread.join();
		}catch(InterruptedException e){
			//Nothing to do..
		}
	}
}
