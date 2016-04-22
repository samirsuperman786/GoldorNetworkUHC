package com.goldornetwork.uhc.listeners.team;

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
		//plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
	}
	@EventHandler
	public void on(AsyncPlayerChatEvent e){
		Player sender = e.getPlayer();
		if(teamM.isTeamsEnabled()){
			if(teamM.isPlayerOnTeam(sender)){
				for(Player all : e.getRecipients()){
					if(teamM.isPlayerOnTeam(all)){
						if(teamM.getTeamOfPlayer(all).equals(teamM.getTeamOfPlayer(sender))){
							e.getRecipients().remove(all);
							all.sendMessage(ChatColor.GREEN + sender.getName() + ": " + e.getMessage());
						}
					}
				}
			}
			
		}
		
	}
}
