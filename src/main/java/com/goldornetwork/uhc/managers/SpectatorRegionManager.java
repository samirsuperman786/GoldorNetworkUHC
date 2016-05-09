package com.goldornetwork.uhc.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class SpectatorRegionManager implements Listener {

	//should implements listener to listen for game start event then run a runnable
	//instances
	private UHC plugin;
	private TeamManager teamM;
	private ScatterManager scatterM;

	//storage
	final int BUFFERBLOCKS= 15;
	Location center;

	public SpectatorRegionManager(UHC plugin, TeamManager teamM,ScatterManager scatterM) {
		this.plugin=plugin;
		this.teamM=teamM;
		this.scatterM=scatterM;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void setup(){
		center= scatterM.getCenter();
	}
	@EventHandler
	public void on(GameStartEvent e){
		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){

			@Override
			public void run() {
				runTask();
			}
			
		}, 0L, 40L);
	}
	/**
	 * Checks the region of spectators and teleports them if they are not inside of the world border
	 */
	public void runTask() {
			int radius = (int) ((scatterM.getUHCWorld().getWorldBorder().getSize())/2);
			Vector minRegion = new Location(scatterM.getUHCWorld(), center.getBlockX() - radius - BUFFERBLOCKS, 0, center.getBlockZ() - radius - BUFFERBLOCKS).toVector();
			Vector maxRegion = new Location(scatterM.getUHCWorld(), center.getBlockX() + radius + BUFFERBLOCKS, scatterM.getUHCWorld().getMaxHeight(), center.getBlockZ() + radius + BUFFERBLOCKS).toVector();
			for(UUID u : teamM.getObservers()){
				OfflinePlayer p = (OfflinePlayer) Bukkit.getServer().getOfflinePlayer(u);
				if(p.isOnline()){
					Player target = (Player) p;
					Vector pLoc = target.getLocation().toVector();
					if(pLoc.isInAABB(minRegion, maxRegion)==false){
						target.teleport(scatterM.getCenter());
					}
				}
			}
	}

}
