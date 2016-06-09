package com.goldornetwork.uhc.managers.world;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;

public class UHCServer implements Listener{

	private UHC plugin;
	private TeamManager teamM;
	private int FAKE_PLAYER_SLOTS;
	private int BUFFER_PLAYER_SLOTS;
	
	public UHCServer(UHC plugin, TeamManager teamM) {
		this.plugin=plugin;
		this.teamM=teamM;
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
		if(target.isBanned()){
			e.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.GOLD + Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntry(target.getName()).getReason());
		}
		else if(target.hasPermission("uhc.whitelist.bypass")){
			e.allow();
		}
		else if(teamM.isPlayerInGame(target.getUniqueId())){
			e.allow();
		}
		else if(plugin.getServer().getOnlinePlayers().size()<FAKE_PLAYER_SLOTS){
			if(plugin.getServer().hasWhitelist()){
				e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server not open yet.");
			}
			else{
				e.allow();
			}
		}
		else if(plugin.getServer().getOnlinePlayers().size()>= FAKE_PLAYER_SLOTS){
			if(target.isWhitelisted()==false){
				e.disallow(PlayerLoginEvent.Result.KICK_FULL, ChatColor.RED + "Server is full!");
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
