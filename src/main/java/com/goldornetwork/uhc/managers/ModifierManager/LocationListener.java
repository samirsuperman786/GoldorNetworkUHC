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
	private DealDamage dealD = DealDamage.getInstance();
	public static LocationListener getInstance(){
		return instance;
	}
	
	
	@Override
	public void run() {
		if(modM.getModesThatAreEnabled().get(ModifierManager.Gamemodes.SKYHIGH)){
			if(timerM.hasMatchStarted()){
				if(timerM.isPVPEnabled()){
					for(UUID u : teamM.getPlayersInGame()){
						if(Bukkit.getServer().getPlayer(u).isOnline()){
							if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()<=100){
								dealD.addPlayerToTick(Bukkit.getServer().getPlayer(u), 1);
							}
						}
					}
				}
			}
			
		}
		
	}

	
	
}
