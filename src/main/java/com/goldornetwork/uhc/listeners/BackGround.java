package com.goldornetwork.uhc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class BackGround implements Listener {

	
	public BackGround(UHC plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void on(FoodLevelChangeEvent e){
		if(!(State.getState().equals(State.INGAME))){
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void on(EntityDamageEvent e){
		if(!(State.getState().equals(State.INGAME))){
			if(e.getEntity() instanceof Player){
				e.setCancelled(true);	
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(AsyncPlayerChatEvent e){
		e.setFormat("%s: %s");
	}
	
}
