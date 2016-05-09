package com.goldornetwork.uhc.listeners.team;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class TeamChat implements Listener{

	private TeamManager teamM;
	public TeamChat(UHC plugin, TeamManager teamM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
	}
	
	private void sendToTeamates(String team, Player sender, String msg){
		for(UUID u : teamM.getPlayersOnATeam(team)){
			if(Bukkit.getOfflinePlayer(u).isOnline()){
				Player teamate = Bukkit.getPlayer(u);
				teamate.sendMessage(ChatColor.GREEN + "[Team] " + PlayerUtils.getPrefix(sender) + sender.getName() + ChatColor.WHITE +  ": " + msg);
			}
		}
	}
	
	
}
