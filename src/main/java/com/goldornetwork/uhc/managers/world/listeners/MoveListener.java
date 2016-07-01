package com.goldornetwork.uhc.managers.world.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class MoveListener implements Listener {

	
	private TeamManager teamM;
	
	private boolean freezeAll;
	private Set<UUID> frozen = new HashSet<UUID>();
	
	public MoveListener(UHC plugin, TeamManager teamM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
	}

	public void setup(){
		this.freezeAll=false;
	}

	public void freezePlayers(){
		this.freezeAll=true;
	}

	public void unfreezePlayers(){
		this.freezeAll=false;
	}
	
	public void freezePlayer(UUID target){
		if(PlayerUtils.getOfflinePlayer(target).isOnline()){
			Player p = PlayerUtils.getPlayer(target);
			MessageSender.send(p, "You are now frozen.");
		}
		frozen.add(target);
	}
	
	public void unfreezePlayer(UUID target){
		if(PlayerUtils.getOfflinePlayer(target).isOnline()){
			Player p = PlayerUtils.getPlayer(target);
			MessageSender.send(p, "You are now unfrozen.");
		}
		frozen.remove(target);
	}
	
	public boolean isFrozen(UUID target){
		return frozen.contains(target);
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
		else if(frozen.contains(e.getPlayer().getUniqueId())){
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
	
	@EventHandler
	public void on(BlockPlaceEvent e){
		if(frozen.contains(e.getPlayer().getUniqueId())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void on(BlockBreakEvent e){
		if(frozen.contains(e.getPlayer().getUniqueId())){
			e.setCancelled(true);
		}
	}
}
