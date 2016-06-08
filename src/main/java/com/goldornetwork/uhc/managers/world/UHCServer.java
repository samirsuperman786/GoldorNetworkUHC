package com.goldornetwork.uhc.managers.world;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.goldornetwork.uhc.UHC;

public class UHCServer implements Listener{

	private UHC plugin;
	private int FAKE_PLAYER_SLOTS;
	private int BUFFER_PLAYER_SLOTS;
	
	public UHCServer(UHC plugin) {
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	public void setup(){
		plugin.getConfig().addDefault("Fake-Player-Slots", 50);
		plugin.getConfig().addDefault("BUFFER-PLAYER-SLOTS", 10);
		plugin.saveConfig();
		this.FAKE_PLAYER_SLOTS=plugin.getConfig().getInt("Fake-Player-Slots");
		this.BUFFER_PLAYER_SLOTS=plugin.getConfig().getInt("BUFFER-PLAYER-SLOTS");
	}
	
	@EventHandler
	public void on(PlayerLoginEvent e){
		Player target = e.getPlayer();
		if(target.hasPermission("uhc.whitelist.bypass")){
			e.allow();
		}
		else if(plugin.getServer().getOnlinePlayers().size()>= FAKE_PLAYER_SLOTS){
			if(target.isWhitelisted()==false){
				e.disallow(PlayerLoginEvent.Result.KICK_FULL, ChatColor.AQUA + "Server is full!");
			}
			else if(plugin.getServer().getOnlinePlayers().size()<(FAKE_PLAYER_SLOTS + BUFFER_PLAYER_SLOTS)){
				e.allow();
			}
			else{
				e.disallow(PlayerLoginEvent.Result.KICK_FULL, ChatColor.RED + "Not enough slots left in this server.");
			}
		}
	}
}