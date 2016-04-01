package com.goldornetwork.uhc.managers.ModifierManager;

import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.LandIsBadManager;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.SkyHigh;

public class LocationListener implements Runnable {

	//instances
	private TimerManager timerM;
	private SkyHigh skyHighM;
	private LandIsBadManager landIsBadM;
	//storage
	private boolean enableSkyHigh;
	private boolean enableLandIsBad;
	
	public LocationListener(TimerManager timerM, SkyHigh skyHighM, LandIsBadManager landIsBadM) {
		this.timerM=timerM;
		this.skyHighM=skyHighM;
		this.landIsBadM=landIsBadM;
	}
	public void setup(){
		enableSkyHigh=false;
		enableLandIsBad=false;
	}
	
	public void enableSkyHigh(boolean val){
		this.enableSkyHigh=val;
	}
	public void enableLandIsBad(boolean val){
		this.enableLandIsBad=val;
	}
	
	@Override
	public void run() {
		if(timerM.hasMatchStarted()){
			if(timerM.isPVPEnabled()){
				if(enableSkyHigh){
					skyHighM.run();
				}
				if(enableLandIsBad){
					landIsBadM.run();
				}
			}
		}
		
		
		
	}

	
	
}
