package com.goldornetwork.uhc.managers.ModifierManager.actions;

import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TimerManager;

public class LandIsBadManager implements Runnable{

	//instances
	private static LandIsBadManager instance = new LandIsBadManager();
	//private TeamManager teamM = TeamManager.getInstance();
	private ScatterManager scatterM = ScatterManager.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	
	//storage
	private int counter;
	private boolean enableLandIsBad;
	
	
	public static LandIsBadManager getInstance(){
		return instance;
	}
	public void enableLandIsBad(boolean val){
		this.enableLandIsBad=val;
	}
	
	@Override
	public void run() {
		if(enableLandIsBad){
			if(timerM.hasCountDownEnded()){
				if(scatterM.isScatteringComplete()){
					++counter;
					if(counter>20*60){
						//TODO spawn op creatures
					}
				}
			}
		}
		
	}

}
