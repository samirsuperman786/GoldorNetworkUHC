package com.goldornetwork.uhc.managers.ModifierManager;

import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.LandIsBadManager;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.SkyHigh;

public class LocationListener implements Runnable {

	//instances
	private static LocationListener instance = new LocationListener();
	private TimerManager timerM = TimerManager.getInstance();
	private SkyHigh skyHighM = SkyHigh.getInstance();
	private LandIsBadManager landIsBadM = LandIsBadManager.getInstance();
	//storage
	private boolean enableSkyHigh;
	private boolean enableLandIsBad;
	
	public static LocationListener getInstance(){
		return instance;
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
