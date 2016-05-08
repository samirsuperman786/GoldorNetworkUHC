package com.goldornetwork.uhc.listeners.team;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;

public class TeamChat implements Listener{

	private TeamManager teamM;
	public TeamChat(UHC plugin, TeamManager teamM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
	}
	@EventHandler
	public void on(AsyncPlayerChatEvent e){
		Player sender = e.getPlayer();
		if(teamM.isTeamsEnabled()){
			if(teamM.isPlayerOnTeam(sender)){
				for(UUID u : teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(sender))){
					if(Bukkit.getOfflinePlayer(u).isOnline()){
						Player teamate = Bukkit.getPlayer(u);
						if(e.getRecipients().contains(teamate)){
							e.getRecipients().remove(teamate);
							teamate.sendMessage(ChatColor.GREEN + ChatColor.stripColor(sender.getDisplayName()) +  ": " + e.getMessage());
						}
					}
				}
			}
			
		}
		
	}
}
