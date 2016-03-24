package com.goldornetwork.uhc.managers.ModifierManager;

import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.ModifierManager.actions.SkyHigh;

public class LocationListener implements Runnable {

	//instances
	private static LocationListener instance = new LocationListener();
	private TimerManager timerM = TimerManager.getInstance();
	private SkyHigh skyHighM = SkyHigh.getInstance();
	
	//storage
	private boolean enableSkyHigh;
	
	public static LocationListener getInstance(){
		return instance;
	}
	public void setup(){
		this.enableSkyHigh=false;
	}
	
	public void enableSkyHigh(boolean val){
		this.enableSkyHigh=val;
	}

	
	@Override
	public void run() {
		if(enableSkyHigh){
			if(timerM.hasMatchStarted()){
				if(timerM.isPVPEnabled()){
					skyHighM.run();
				}
			}
			
		}
		
		
	}

	
	
}
