package com.goldornetwork.uhc.managers.ModifierManager;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.ModifierManager.actions.DealDamage;

public class LocationListener implements Runnable {

	private static LocationListener instance = new LocationListener();
	private TeamManager teamM = TeamManager.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	private ModifierManager modM = ModifierManager.getInstance();
	private boolean enableSkyHigh;
	
	public static LocationListener getInstance(){
		return instance;
	}
	
	public void enableSkyHigh(boolean val){
		this.enableSkyHigh=val;
	}
	
	
	@Override
	public void run() {
		if(enableSkyHigh){
			if(timerM.hasMatchStarted()){
				if(timerM.isPVPEnabled()){
					for(UUID u : teamM.getPlayersInGame()){
						if(Bukkit.getServer().getPlayer(u).isOnline()){
							if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()<=100){
								//TODO add damage tick
							}
							else if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()>=101){
								//TODO remove damage tick
							}
						}
					}
				}
			}
			
		}
		
		
	}

	
	
}
