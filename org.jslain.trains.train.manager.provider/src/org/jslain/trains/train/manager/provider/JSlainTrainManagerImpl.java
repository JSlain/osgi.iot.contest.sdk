package org.jslain.trains.train.manager.provider;

import org.jslain.trains.train.manager.provider.handlers.NavigationHandler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.scheduler.api.Scheduler;
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
	
	@Reference
	private Scheduler scheduler;
	
	private Thread trainThread;
	
	private TrainDto trainDto;
	
	@Activate
	public void activate(TrainConfiguration config){

		trainDto = new TrainDto();
		trainDto.name = config.name();
		trainDto.rfid = config.rfid();
		
		trainThread = new Thread(new TrainLoop(new TrainLifeCycle(
				trainDto, 
				trainController, 
				trackForTrain, 
				new TrainObservationHandlerFactory(
						trackForTrain, 
						trainDto,
						new NavigationHandler(new DefaultPathCalculatorFactory()),
						trainController))));
		
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
