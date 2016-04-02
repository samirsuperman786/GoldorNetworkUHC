package com.goldornetwork.uhc.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.goldornetwork.uhc.managers.GameModeManager.State;

public class SpectatorRegionManager implements Runnable {

	
	//instances
	private TeamManager teamM;
	private ScatterManager scatterM;
	
	//storage
	final int BUFFERBLOCKS= 15;
	Location center;
	
	public SpectatorRegionManager(TeamManager teamM,ScatterManager scatterM) {
		this.teamM=teamM;
		this.scatterM=scatterM;
		center= scatterM.getUHCWorld().getWorldBorder().getCenter();
	}
	@Override
	public void run() {
		int radius = (int) ((scatterM.getUHCWorld().getWorldBorder().getSize())/2);
		Vector minRegion = new Location(scatterM.getUHCWorld(), center.getBlockX() - radius - BUFFERBLOCKS, 0, center.getBlockZ() - radius - BUFFERBLOCKS).toVector();
		Vector maxRegion = new Location(scatterM.getUHCWorld(), center.getBlockX() + radius + BUFFERBLOCKS, scatterM.getUHCWorld().getMaxHeight(), center.getBlockZ() + radius + BUFFERBLOCKS).toVector();
		if(State.getState().equals(State.INGAME)){
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
