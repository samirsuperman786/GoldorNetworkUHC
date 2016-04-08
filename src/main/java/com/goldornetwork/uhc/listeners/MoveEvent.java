package com.goldornetwork.uhc.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;

public class MoveEvent implements Listener {

	private TeamManager teamM;
	private boolean freezeAll;
	
	public MoveEvent(UHC plugin, TeamManager teamM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
	}
	
	/**
	 * Will clear all frozen players
	 */
	public void setup(){
		this.freezeAll=false;
	}

	/**
	 * Will freeze all players in game
	 */
	public void freezePlayers(){
		this.freezeAll=true;
	}
	
	/**
	 * Will unfreeze all players in game
	 */
	public void unfreezePlayers(){
		this.freezeAll=false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerMoveEvent e){
		if(freezeAll){
			if(teamM.getPlayersInGame().contains(e.getPlayer().getUniqueId())){
				Location from=e.getFrom();
				Location to=e.getTo();
				double x=Math.floor(from.getX());
				double z=Math.floor(from.getZ());
				if(Math.floor(to.getX())!=x||Math.floor(to.getZ())!=z){
					x+=.5;
					z+=.5;
					e.getPlayer().teleport(new Location(from.getWorld(),x,from.getY(),z,from.getYaw(),from.getPitch()));
				}
			}
		}
	}
	
	@EventHandler
	public void on(EntityDamageEvent e){
		if(freezeAll){
			if(e.getEntity() instanceof Player){
				Player p = (Player) e.getEntity();
				if(teamM.getPlayersInGame().contains(p.getUniqueId())){
					e.setCancelled(true);
				}
			}
		}
		
	}
	
}
