package com.goldornetwork.uhc.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class BackGround implements Listener {

	private UHC plugin;
	private boolean mutePlayers;
	
	public BackGround(UHC plugin) {
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		mutePlayers=false;
	}
	
	public void mutePlayers(){
		mutePlayers=true;
		MessageSender.broadcast(ChatColor.GOLD + "Chat has been muted.");
	}
	
	public void unMutePlayers(){
		mutePlayers= false;
		MessageSender.broadcast(ChatColor.GOLD + "Chat has been unmuted!");
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
	
	@EventHandler
	public void on(PlayerLoginEvent e){
		if(e.getResult().equals(PlayerLoginEvent.Result.KICK_OTHER) || e.getResult().equals(PlayerLoginEvent.Result.KICK_WHITELIST)|| e.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)){
			if(e.getPlayer().hasPermission("uhc.whitelist.bypass")){
				e.allow();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void on(AsyncPlayerChatEvent e){
		if(mutePlayers){
			if(e.getPlayer().hasPermission("uhc.chat.mutebypass")==false){
				e.setCancelled(true);
			}
		}
		
		if(e.getPlayer().hasPermission("uhc.chat.mod")){
			e.setFormat(ChatColor.RED + "\u2739" + ChatColor.WHITE +  "%s: %s");
		}
		else if(e.getPlayer().hasPermission("uhc.chat.admin")){
			e.setFormat(ChatColor.GOLD + "\u2739" + ChatColor.WHITE +  "%s: %s");
		}
		else{
			e.setFormat("%s: %s");
		}
	}
	
	
}
