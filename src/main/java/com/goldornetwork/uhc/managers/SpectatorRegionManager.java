package com.goldornetwork.uhc.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpectatorRegionManager implements Runnable {

	private static SpectatorRegionManager instance = new SpectatorRegionManager();
	
	//instances
	private TeamManager teamM = TeamManager.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	private ScatterManager scatterM = ScatterManager.getInstance();
	
	//storage
	int bufferBlocks = 15;
	Location center = scatterM.getUHCWorld().getWorldBorder().getCenter();
	
	public static SpectatorRegionManager getInstance(){
		return instance;
	}
	
	@Override
	public void run() {
		int radius = (int) ((scatterM.getUHCWorld().getWorldBorder().getSize())/2);
		Vector minRegion = new Location(scatterM.getUHCWorld(), center.getBlockX() - radius - bufferBlocks, 0, center.getBlockZ() - radius - bufferBlocks).toVector();
		Vector maxRegion = new Location(scatterM.getUHCWorld(), center.getBlockX() + radius + bufferBlocks, scatterM.getUHCWorld().getMaxHeight(), center.getBlockZ() + radius + bufferBlocks).toVector();
		if(timerM.hasMatchStarted()){
			for(UUID u : teamM.getObservers()){
				Player p = (Player) Bukkit.getServer().getOfflinePlayer(u);
				if(p.isOnline()){
					Vector pLoc = p.getLocation().toVector();
					if(pLoc.isInAABB(minRegion, maxRegion)==false){
						p.teleport(scatterM.getUHCWorld().getSpawnLocation());
					}
				}
			}
		}
		
		
	}

}
