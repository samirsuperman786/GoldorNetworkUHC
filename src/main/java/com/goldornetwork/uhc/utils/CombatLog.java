package com.goldornetwork.uhc.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.listeners.DeathEvent;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class CombatLog implements Listener{
	
	private ScatterManager scatterM;
	private TeamManager teamM;
	private DeathEvent deathE;
	//storage
	private Map<String, UUID> disconnectedPlayers = new HashMap<String, UUID>();
	private Map<UUID, LivingEntity> disconnectedReplacements = new HashMap<UUID, LivingEntity>();
	
	/*
	 * TODO work on this
	 */
	public CombatLog(UHC plugin, ScatterManager scatterM, TeamManager teamM, DeathEvent deathE){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.scatterM=scatterM;
		this.teamM=teamM;
		this.deathE=deathE;
	}
	private void spawnReplacement(Player p){
		LivingEntity chicken = (LivingEntity) scatterM.getUHCWorld().spawnEntity(p.getLocation(), EntityType.CHICKEN);
		chicken.setCustomName(p.getName());
		chicken.setHealth(p.getHealth());
		disconnectedPlayers.put(p.getName(), p.getUniqueId());
		disconnectedReplacements.put(p.getUniqueId(), chicken);
	}
	private Map<String, UUID> getDisconnected(){
		return disconnectedPlayers;
	}
	private void removePlayer(Player p){
		disconnectedPlayers.remove(p.getUniqueId());
	}
	private void removeReplacement(Player p){
		if(disconnectedReplacements.get(p.getUniqueId()).isDead()==false){
			disconnectedReplacements.get(p.getUniqueId()).remove();
		}
		disconnectedReplacements.remove(p.getUniqueId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(State.getState().equals(State.INGAME)){
			if(teamM.isPlayerInGame(p)){
				spawnReplacement(p);
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(State.getState().equals(State.INGAME)){
			if(teamM.isPlayerInGame(p)){
				if(getDisconnected().containsKey(p.getUniqueId())){
					removePlayer(p);
					removeReplacement(p);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(EntityDeathEvent e){
		if(e instanceof Chicken){
			LivingEntity chic = e.getEntity();
			if(disconnectedReplacements.containsValue(chic)){
				//deathE.playerDied(p);
			}
		}
	}
	
}
