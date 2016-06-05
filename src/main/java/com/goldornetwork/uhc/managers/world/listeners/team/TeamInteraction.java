package com.goldornetwork.uhc.managers.world.listeners.team;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class TeamInteraction {

	private TeamManager teamM;
	
	public TeamInteraction(TeamManager teamM) {
		this.teamM=teamM;
	}
	public void sendCoords(String team, Player sender){
		Location pLoc = sender.getLocation();
		String msg = ChatColor.GREEN + "[Team] " + PlayerUtils.getPrefix(sender) + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + ": " + ChatColor.GOLD + "X: " + ChatColor.WHITE + pLoc.getBlockX() + ChatColor.GOLD + " Y: " + ChatColor.WHITE + pLoc.getBlockY() + ChatColor.GOLD + " Z: " + ChatColor.WHITE + pLoc.getBlockZ();
		for(UUID u : teamM.getPlayersOnATeam(team)){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				Player teamate = Bukkit.getServer().getPlayer(u);
				teamate.sendMessage(msg);
			}
		}
	}
	
	public void sendMsgTeamates(String team, Player sender, String msg){
		for(UUID u : teamM.getPlayersOnATeam(team)){
			if(Bukkit.getOfflinePlayer(u).isOnline()){
				Player teamate = Bukkit.getPlayer(u);
				teamate.sendMessage(ChatColor.GREEN + "[Team] " + PlayerUtils.getPrefix(sender) + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + ": " + ChatColor.GREEN + msg);
			}
		}
	}
}
