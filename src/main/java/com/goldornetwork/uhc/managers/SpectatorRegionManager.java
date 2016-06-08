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
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.events.GameStartEvent;

public class SpectatorRegionManager implements Listener {

	//should implements listener to listen for game start event then run a runnable
	//instances
	private UHC plugin;
	private TeamManager teamM;
	private WorldManager worldM;

	//storage
	final int BUFFERBLOCKS= 15;
	Location center;

	public SpectatorRegionManager(UHC plugin, TeamManager teamM, WorldManager worldM) {
		this.plugin=plugin;
		this.teamM=teamM;
		this.worldM=worldM;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void setup(){
		center= worldM.getCenter();
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
		
			int radius = (int) ((worldM.getUHCWorld().getWorldBorder().getSize())/2);
			Vector minRegion = new Location(worldM.getUHCWorld(), center.getBlockX() - radius - BUFFERBLOCKS, 0, center.getBlockZ() - radius - BUFFERBLOCKS).toVector();
			Vector maxRegion = new Location(worldM.getUHCWorld(), center.getBlockX() + radius + BUFFERBLOCKS, worldM.getUHCWorld().getMaxHeight(), center.getBlockZ() + radius + BUFFERBLOCKS).toVector();
			for(UUID u : teamM.getObservers()){
				OfflinePlayer p = (OfflinePlayer) Bukkit.getServer().getOfflinePlayer(u);
				if(p.isOnline()){		
					Player target = (Player) p;
					if(target.getWorld().equals(worldM.getUHCWorld())){
						Vector pLoc = target.getLocation().toVector();
						if(pLoc.isInAABB(minRegion, maxRegion)==false){
							target.teleport(worldM.getCenter());
						}
					}
					else{
						target.teleport(worldM.getCenter());
					}
				}
			}
	}

}
